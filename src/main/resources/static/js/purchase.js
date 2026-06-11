/**
 * AI 발주 관리 대시보드 전용 통합 스크립트
 */
document.addEventListener("DOMContentLoaded", function() {

    // =================================================================
    // 1. 날짜 설정 및 초기화
    // =================================================================
    function getFormattedDate(date) {
        const yyyy = date.getFullYear();
        const mm = String(date.getMonth() + 1).padStart(2, '0');
        const dd = String(date.getDate()).padStart(2, '0');
        return yyyy + '-' + mm + '-' + dd;
    }

    const today = new Date();

    // 예측 스냅샷 날짜 동적 바인딩 (어제 22시 배치 기준)
    const snapshotTarget = document.getElementById('predictSnapshot');
    if (snapshotTarget) {
        const yesterday = new Date(today);
        yesterday.setDate(today.getDate() - 1);
        snapshotTarget.textContent = getFormattedDate(yesterday) + ' 22:00 (배치)';
    }

    // 발주 예정일 동적 바인딩 (내일 날짜 자동 세팅)
    const orderDateInput = document.getElementById('orderTargetDate');
    if (orderDateInput) {
        const tomorrow = new Date(today);
        tomorrow.setDate(today.getDate() + 1);
        orderDateInput.value = getFormattedDate(tomorrow);
    }

    // 학습 로그 범위 안내 문구 출력
    const rangeInput = document.getElementById('learningLogRange');
    if (rangeInput) {
        rangeInput.value = "정기 가중치 학습 모델 적용 (매월 1회 정밀 갱신)";
    }


    // =================================================================
    // 2. AI 재분석 API 호출 (실시간 재계산)
    // =================================================================
    const reanalyzeBtn = document.getElementById("btn-ai-reanalyze");

    if (reanalyzeBtn) {
        reanalyzeBtn.addEventListener("click", function() {
            if (!confirm("최신 현재고 기준의 실시간 데이터 재분석을 요청하시겠습니까?")) {
                return;
            }

            reanalyzeBtn.disabled = true;
            reanalyzeBtn.innerText = "AI 분석 데이터 수거 중...";

            fetch("/api/order/reanalyze", {
                method: "POST"
            })
                .then(response => {
                    if (!response.ok) throw new Error("AI 분석 서버 응답 실패");
                    return response.json();
                })
                .then(data => {
                    if (data.status === "SUCCESS") {
                        alert("최신 재고 기준으로 AI 추천 수량이 갱신되었습니다.");
                        location.reload();
                    } else {
                        alert("AI 재분석 실패: " + data.message);
                        resetReanalyzeButton();
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                    alert("AI 분석 서버 통신 중 시스템 장애가 발생했습니다.");
                    resetReanalyzeButton();
                });
        });
    }

    function resetReanalyzeButton() {
        if (reanalyzeBtn) {
            reanalyzeBtn.disabled = false;
            reanalyzeBtn.innerText = "AI 재분석";
        }
    }


    // =================================================================
    // 3. 테이블 동적 이벤트 (수정 / 제외) 및 합계 계산기
    // =================================================================

    // 전역 총 금액 재계산 함수
    function recalculateTotalOrderPrice() {
        let grandTotal = 0;
        document.querySelectorAll('.ai-order-row').forEach(row => {
            // 7번째 열 (예상 금액)에서 숫자만 추출
            const priceText = row.querySelector('td:nth-child(7)').textContent.replace(/[^0-9]/g, '');
            grandTotal += parseInt(priceText) || 0;
        });

        const formattedPrice = grandTotal.toLocaleString() + '원';

        // 상단 히어로 배너 금액 업데이트
        const heroAccent = document.querySelector('.hero .accent');
        if (heroAccent) heroAccent.textContent = formattedPrice;

        // 하단 테이블 합계 로우 금액 업데이트
        const footerTotal = document.querySelector('tr[style*="background: #FAFAF9;"] td.num');
        if (footerTotal) footerTotal.textContent = formattedPrice;
    }

    // 로우 내부 수정, 제외 이벤트 바인딩 순회
    document.querySelectorAll('.ai-order-row').forEach(row => {
        const ingredientId = parseInt(row.dataset.ingredientId, 10);

        // 제외 버튼 핸들러
        row.querySelector('.danger').addEventListener('click', function() {
            if (confirm(`'${row.querySelector('strong').textContent}' 품목을 이번 발주에서 제외하시겠습니까?`)) {
                row.remove();
                recalculateTotalOrderPrice();
            }
        });

        // 수정 버튼 핸들러
        row.querySelector('button:not(.danger)').addEventListener('click', function() {
            const currentQty = row.dataset.suggestedQty;
            const newQtyStr = prompt("변경할 발주 제안량을 입력하세요 (숫자만 입력):", currentQty);

            if (newQtyStr === null) return;

            const newQty = parseInt(newQtyStr, 10);
            if (isNaN(newQty) || newQty < 0) {
                alert('올바른 수량을 입력해주세요.');
                return;
            }

            // dataset 수량 실시간 갱신
            row.dataset.suggestedQty = newQty;

            // 5번째 열 (발주 제안량) 텍스트 변경
            const qtyTd = row.querySelector('td:nth-child(5)');
            const unitStr = qtyTd.textContent.replace(/[0-9,\s]/g, '');
            qtyTd.textContent = newQty.toLocaleString() + ' ' + unitStr;

            // 6번째 열 (계약 단가) 추출
            const unitPrice = parseInt(row.querySelector('td:nth-child(6)').textContent.replace(/[^0-9]/g, ''), 10);
            let newTotalPrice = 0;

            // 원두(1) 및 우유(4) 대량 자재 1000분율 올림 연산 반영
            if (ingredientId === 1 || ingredientId === 4) {
                let packQty = Math.ceil(newQty / 1000.0);
                newTotalPrice = packQty * unitPrice;
            } else {
                newTotalPrice = newQty * unitPrice;
            }

            // 7번째 열 (예상 금액) 실시간 텍스트 변경
            row.querySelector('td:nth-child(7)').textContent = newTotalPrice.toLocaleString() + '원';

            // 전역 금액 합계 재계산 가동
            recalculateTotalOrderPrice();
        });
    });


    // =================================================================
    // 4. 승인 및 발주 전송 (발주 생성 후 장부 화면 초기화)
    // =================================================================
    const submitBtn = document.getElementById("btn-submit-bulk-order");

    if (submitBtn) {
        submitBtn.addEventListener("click", function() {
            const rows = document.querySelectorAll(".ai-order-row");
            const orderListPayload = [];

            // 수정된 수량 기준으로 데이터 수집
            rows.forEach(row => {
                const ingredientId = row.dataset.ingredientId;
                const suggestedQty = parseInt(row.dataset.suggestedQty, 10);

                if (ingredientId && suggestedQty > 0) {
                    orderListPayload.push({
                        ingredientId: parseInt(ingredientId, 10),
                        suggestedQty: suggestedQty
                    });
                }
            });

            if (orderListPayload.length === 0) {
                alert("AI 발주 추천 수량이 없거나 제외되어 전송할 내역이 존재하지 않습니다.");
                return;
            }

            if (!confirm(`총 ${orderListPayload.length}개의 자재를 최종 수량 기준으로 일괄 발주 등록하시겠습니까?`)) {
                return;
            }

            submitBtn.disabled = true;
            submitBtn.innerText = "발주 처리 중...";

            fetch("/api/order/bulk-ai", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(orderListPayload)
            })
                .then(response => {
                    if (!response.ok) throw new Error("발주 요청 처리 실패");
                    return response.json();
                })
                .then(data => {
                    if (data.status === "SUCCESS") {
                        alert("일괄 발주 장부 생성이 완료되었습니다.");
                        clearAiOrderTable();
                    } else {
                        alert("발주 등록 실패: " + data.message);
                        resetSubmitButton();
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                    alert("서버 통신 중 에러가 발생했습니다. 장부 적재를 확인하세요.");
                    resetSubmitButton();
                });
        });
    }

    function resetSubmitButton() {
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerText = "승인 및 발주생성";
        }
    }

    // 마스터 테이블 리셋 헬퍼 내부 함수
    function clearAiOrderTable() {
        const tbody = document.querySelector('.data-table tbody');
        if (tbody) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center" style="padding: 60px; color: var(--text-muted); font-weight: 500; background-color: #fafafa;">
                        전송이 완료되었거나 처리할 AI 추천 발주 항목이 존재하지 않습니다.
                    </td>
                </tr>
            `;
        }
        const heroAccent = document.querySelector('.hero .accent');
        if (heroAccent) heroAccent.textContent = '0원';

        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerText = "발주 전송 완료";
        }
    }
});