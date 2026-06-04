package com.example.CafeAutoSystem.ai_rpa.service;

import com.example.CafeAutoSystem.ai_rpa.dto.OrderItemDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class RpaExcelService {

    /**
     * 📊 승인된 다중 발주 데이터를 기반으로 진짜 거래처 제출용 엑셀 파일(.xlsx)을 생성하는 메서드
     * @param vendorName   거래처명
     * @param orderList    발주할 자재 및 수량 리스트 (DTO 묶음)
     */
    public String createOrderExcelSheet(String vendorName, List<OrderItemDto> orderList) {
        log.info("📂 [RPA 엑셀 엔진] {}용 자동 발주 명세서 파일 생성을 시작합니다.", vendorName);

        // 가상의 엑셀 통합 문서(.xlsx) 및 시트 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("발주서_명세");

        // 컬럼 너비 기본 세팅
        sheet.setColumnWidth(0, 4000); // 발주일자
        sheet.setColumnWidth(1, 6000); // 거래처명
        sheet.setColumnWidth(2, 6000); // 품목명
        sheet.setColumnWidth(3, 3000); // 발주수량

        // 엑셀 폰트 및 상단 타이틀 헤더 스타일 정의
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex()); // 이쁜 파란색 배경
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // 타이틀 헤더 행(Row 0) 생성 및 셀 값 주입
        Row headerRow = sheet.createRow(0);

        String[] headers = {"발주일자", "거래처명", "요청 품목명", "발주 수량(개)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle); // 스타일 적용
        }

        int rowIdx = 1; // 1번 행부터 데이터 시작
        String todayStr = LocalDate.now().toString();

        for (OrderItemDto item : orderList) {
            Row dataRow = sheet.createRow(rowIdx++);

            dataRow.createCell(0).setCellValue(todayStr); // 발주일자
            dataRow.createCell(1).setCellValue(vendorName); // 거래처명
            dataRow.createCell(2).setCellValue(item.getIngredientName()); // 품목명
            dataRow.createCell(3).setCellValue(item.getOrderQty()); // 수량
        }

        String fileName = "Cafe_Order_" + vendorName + "_" + LocalDate.now() + ".xlsx";

        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
            log.info("🎉 [RPA 엑셀 엔진 완료] 엑셀 파일 빌드 성공! 파일명: {}", fileName);

            // 엑셀 저장 위치를 콘솔에서 바로 확인할 수 있도록 가이드 로그 추가
            File file = new File(fileName);
            log.info("📍 [다중 품목 엑셀 저장 절대경로]: {}", file.getAbsolutePath());

            return fileName; // 성공 시 파일명 리턴
        } catch (Exception e) {
            log.error("❌ [엑셀 엔진 오류] 파일 생성 중 크리티컬 장애 발생: {}", e.getMessage());
            return null;
        } finally {
            try {
                workbook.close(); // 메모리 누수 방지를 위해 가상 워크북 닫기
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
