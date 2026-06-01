package com.example.CafeAutoSystem.jms_ai_rpa.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;

@Slf4j
@Service
public class RpaExcelService {

    /**
     * 📊 승인된 발주 데이터를 기반으로 진짜 거래처 제출용 엑셀 파일(.xlsx)을 생성하는 메서드
     * @param vendorName   거래처명
     * @param ingredientName 자재명
     * @param orderQty     최종 발주 수량
     */
    public void createOrderExcelSheet(String vendorName, String ingredientName, int orderQty) {
        log.info("📂 [RPA 엑셀 엔진] {}용 자동 발주 명세서 파일 생성을 시작합니다.", vendorName);

        // 1. 가상의 엑셀 통합 문서(.xlsx) 및 시트 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("발주서_명세");

        // 컬럼 너비 기본 세팅 (보기 좋게 넓혀주기)
        sheet.setColumnWidth(0, 4000); // 발주일자
        sheet.setColumnWidth(1, 6000); // 거래처명
        sheet.setColumnWidth(2, 6000); // 품목명
        sheet.setColumnWidth(3, 3000); // 발주수량

        // 2. 엑셀 폰트 및 상단 타이틀 헤더 스타일 정의
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex()); // 이쁜 파란색 배경
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // 3. 타이틀 헤더 행(Row 0) 생성 및 셀 값 주입
        Row headerRow = sheet.createRow(0);

        String[] headers = {"발주일자", "거래처명", "요청 품목명", "발주 수량(개)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle); // 스타일 적용
        }

        // 4. 진짜 데이터 행(Row 1) 생성 및 값 주입
        Row dataRow = sheet.createRow(1);

        dataRow.createCell(0).setCellValue(LocalDate.now().toString()); // 오늘 날짜
        dataRow.createCell(1).setCellValue(vendorName);
        dataRow.createCell(2).setCellValue(ingredientName);
        dataRow.createCell(3).setCellValue(orderQty);

        // 5. 내 컴퓨터 로컬 폴더에 진짜 파일로 내보내기 (Output Stream)
        // 시연하기 편하게 내 컴퓨터 바탕화면이나 프로젝트 루트 폴더에 생성하도록 세팅합니다.
        String fileName = "Cafe_Order_" + vendorName + "_" + LocalDate.now() + ".xlsx";

        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
            log.info("🎉 [RPA 엑셀 엔진 완료] 엑셀 파일 빌드 성공! 파일명: {}", fileName);

            // 엑셀이 잘 만들어졌는지 눈으로 확인하기 위해 절대 경로 출력
            File file = new File(fileName);
            log.info("📍 [엑셀 저장 절대경로]: {}", file.getAbsolutePath());

        } catch (Exception e) {
            log.error("❌ [엑셀 엔진 오류] 파일 생성 중 크리티컬 장애 발생: {}", e.getMessage());
        } finally {
            try {
                workbook.close(); // 메모리 누수 방지를 위해 가상 워크북 닫기
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
