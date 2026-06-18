package com.example.CafeAutoSystem.ai_rpa.service;

import com.example.CafeAutoSystem.ai_rpa.dto.AiPredictRequestDto;
import com.example.CafeAutoSystem.common.entity.HistoricalStockLogEntity;
import com.example.CafeAutoSystem.common.entity.CurrentStockLogEntity;
import com.example.CafeAutoSystem.common.entity.IngredientEntity;
import com.example.CafeAutoSystem.common.repository.CurrentStockLogRepository;
import com.example.CafeAutoSystem.common.repository.IngredientRepository;
import com.example.CafeAutoSystem.purchase.dto.PurchaseOrderDto;
import com.example.CafeAutoSystem.purchase.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiSchedulerService {
    private final AiPredictService aiPredictService;
    private final CurrentStockLogRepository currentStockLogRepository;
    private final IngredientRepository ingredientRepository;
    private final PurchaseOrderService purchaseOrderService;
    private final RpaMailService rpaMailService;

    // 파이썬 AI 배포 서버 기본 베이스 URL 주소
    private final String pythonServerBaseUrl = "http://cafe-ai-system-env.eba-ppyuzfmm.ap-northeast-2.elasticbeanstalk.com/order";


    @Scheduled(cron = "0 0 22 * * *")
    public void runDailyAiStockAnalysis() {
        log.info("[AI 스케줄러] 전 자재 대상 PyTorch 딥러닝 기반 실시간 초고속 발주 제안 연산 시작");

        int successCount = 0;
        StringBuilder orderItemRows = new StringBuilder();

        List<PurchaseOrderDto> aiOrderDtoList = new ArrayList<>();

        try {
            List<IngredientEntity> allIngredients = ingredientRepository.findAll();
            String todayDayOfWeek = LocalDate.now().getDayOfWeek().toString();
            RestTemplate restTemplate = new RestTemplate();
            String predictUrl = pythonServerBaseUrl + "/predict";

            for (IngredientEntity ingredient : allIngredients) {
                Integer targetIngredientId = ingredient.getIngredientId();
                log.info("[자재 예측 조회] ID: {} | 이름: {}", targetIngredientId, ingredient.getIngredientName());

                List<Integer> amountList = gatherRawAmounts(targetIngredientId);
                int currentStockSum = currentStockLogRepository.convertToCurrentStock(targetIngredientId);
                int realSafetyStock = aiPredictService.getSafetyStockByIngredient(targetIngredientId);

                AiPredictRequestDto requestDto = AiPredictRequestDto.builder()
                        .ingredientId(targetIngredientId)
                        .dayOfWeek(todayDayOfWeek)
                        .currentStock(currentStockSum)
                        .safetyStock(realSafetyStock)
                        .rawAmounts(amountList)
                        .build();

                Map<String, Object> response = restTemplate.postForObject(predictUrl, requestDto, Map.class);

                if (response != null) {
                    String status = (String) response.get("status");
                    Integer suggestedQty = (Integer) response.get("suggestedQty");
                    String message = (String) response.get("message");
                    String code = (String) response.get("code");

                    // 1. 기존 분석 로그 적재 (유지)
                    aiPredictService.saveAiAnalysisLog(targetIngredientId, status, suggestedQty, message, code);
                    log.info("자재 ID {}번 AI 실시간 제안량 장부 적재 완료", targetIngredientId);

                    // 🌟 2. [정합성 파이프라인 연동]
                    // PurchaseOrderService 규격에 맞춰 DTO를 조립하여 리스트에 누적합니다.
                    aiOrderDtoList.add(PurchaseOrderDto.builder()
                            .ingredientId(targetIngredientId)
                            .suggestedQty(suggestedQty)
                            .finalQty(suggestedQty) // 검토 전 초기 수량은 AI 제안량과 동기화
                            .build());

                    successCount++;

                    // 예측 성공한 품목의 이름, 현재고, AI 제안 수량을 관리자 메일용 HTML 테이블 행으로 적재
                    String unit = ingredient.getUnit() != null ? ingredient.getUnit() : "개";
                    orderItemRows.append("<tr>")
                            .append("<td style='padding:10px; border:1px solid #cbd5e1;'>").append(ingredient.getIngredientName()).append("</td>")
                            .append("<td style='padding:10px; border:1px solid #cbd5e1; text-align:right;'>").append(currentStockSum).append(" ").append(unit).append("</td>")
                            .append("<td style='padding:10px; border:1px solid #cbd5e1; text-align:right; color:#10b981; font-weight:bold;'>").append(suggestedQty).append(" ").append(unit).append("</td>")
                            .append("</tr>");
                }
            } // [For 루프 마감]

            // 🌟 3. [발주 테이블 대기상태 일괄 등록 트리거]
            // 수집된 AI 예측 결과 리스트가 존재한다면 비즈니스 서비스 레이어를 호출하여 PENDING 원장을 일괄 빌드합니다.
            if (!aiOrderDtoList.isEmpty()) {
                purchaseOrderService.createBulkOrdersFromAi(aiOrderDtoList);
                log.info("🎯 [정합성 연동 완료] AI 최신 제안 수량 기반 PENDING 발주 원장 일괄 데이터베이스 영속화 성공!");
            }

            // 4. 모든 품목 예측 및 원장 생성이 정상 완료된 후 관리자 컨펌 메일 발송 파트
            if (successCount > 0) {
                String adminEmail = "wkdalstj0522@gmail.com"; // 관리자 수신 이메일 주소
                String approvalLink = "http://localhost:8080/api/jms-rpa/approve-from-mail";

                // 누적된 orderItemRows를 깔끔한 HTML 스타일 테이블로 감싸서 본문 구성
                String emailContent = "<h3>[CafeAutoSystem] 금일 AI 발주 예측 결과 분석 완료</h3>"
                        + "<p>PyTorch 신경망 모델 분석 결과, 아래 품목에 대한 발주가 제안되었습니다.</p>"
                        + "<p>내역을 확인하신 후 하단의 <strong>일괄 승인</strong> 버튼을 누르시면 거래처별 명세서 분할 전송이 실행됩니다.</p>"
                        + "<br>"
                        + "<table style='width:100%; max-width:600px; border-collapse:collapse; margin-bottom:25px; font-size:14px;'>"
                        + "  <thead>"
                        + "    <tr style='background-color:#f1f5f9;'>"
                        + "      <th style='padding:10px; border:1px solid #cbd5e1; text-align:left;'>품목명</th>"
                        + "      <th style='padding:10px; border:1px solid #cbd5e1; text-align:right;'>현재고</th>"
                        + "      <th style='padding:10px; border:1px solid #cbd5e1; text-align:right;'>AI 제안 발주량</th>"
                        + "    </tr>"
                        + "  </thead>"
                        + "  <tbody>"
                        + orderItemRows.toString() // 누적된 자재 리스트 꽂아넣기
                        + "  </tbody>"
                        + "</table>"
                        + "<a href='" + approvalLink + "' style='display:inline-block; background:#10b981; color:white; padding:12px 24px; text-decoration:none; border-radius:6px; font-weight:bold;'>위 내역으로 최종 발주 일괄 승인하기</a>"
                        + "<br><br><p style='color:#94a3b8; font-size:12px;'>본 메일은 시스템 스케줄러에 의해 자동 발송되었습니다.</p>";

                rpaMailService.sendAdminNotificationEmail(adminEmail, "[AI 발주 알림] 금일 분석 완료건에 대한 승인 요청", emailContent);
                log.info("📢 [스케줄러 완료] 관리자 최종 승인 요청 메일(품목 리스트 포함) 발송 완료 -> 수신: {}", adminEmail);
            }

        } catch (Exception e) {
            log.error("[AI 예측 엔진 오류] 분석 순회 중 장애 발생: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 3 1 * * ")
    public void runMonthlyAiModelTraining() {
        log.info("[AI 스케줄러] 매달 1일 PyTorch LSTM 신경망 가중치 정기 배치 학습 프로세스 가동");

        try {
            List<IngredientEntity> allIngredients = ingredientRepository.findAll();
            RestTemplate restTemplate = new RestTemplate();
            String trainUrl = pythonServerBaseUrl + "/train";

            for (IngredientEntity ingredient : allIngredients) {
                Integer targetIngredientId = ingredient.getIngredientId();
                log.info("[모델 학습 주입] 자재 ID: {} | 이름: {}", targetIngredientId, ingredient.getIngredientName());

                List<Integer> amountList = gatherRawAmounts(targetIngredientId);
                log.info("자재 ID {}: 총 {}개의 원장 히스토리 학습 데이터 파이썬 전송 예정", targetIngredientId, amountList.size());

                Map<String, Object> requestPayload = Map.of(
                        "ingredientId", targetIngredientId,
                        "rawAmounts", amountList
                );

                Map<String, Object> response = restTemplate.postForObject(trainUrl, requestPayload, Map.class);

                if (response != null) {
                    log.info("자재 ID {}번 정기 역전파 가중치 훈련 완료: {}", targetIngredientId, response.get("message"));
                } else {
                    log.error("자재 ID {}번 파이썬 배치 학습 서버 응답 백랭크 누락", targetIngredientId);
                }
            }
            log.info("전 자재에 대한 PyTorch LSTM 가중치 파일(.pt) 정기 동기화 완료.");
        } catch (Exception e) {
            log.error("[AI 학습 엔진 오류] 배치 정기 학습 순회 중 장애 발생: {}", e.getMessage());
        }
    }

    private List<Integer> gatherRawAmounts(Integer targetIngredientId) {
        List<HistoricalStockLogEntity> logEntities =
                aiPredictService.getHistoricalLogsByIngredient(targetIngredientId);

        List<CurrentStockLogEntity> currentLogEntities =
                currentStockLogRepository.findByIngredient_IngredientIdOrderByCreatedAtAsc(targetIngredientId);

        List<Integer> amountList = new ArrayList<>();

        if (logEntities != null) {
            for (HistoricalStockLogEntity entity : logEntities) {
                amountList.add(entity.getAmount());
            }
        }

        if (currentLogEntities != null) {
            for (CurrentStockLogEntity entity : currentLogEntities) {
                amountList.add(entity.getAmount());
            }
        }
        return amountList;
    }
}