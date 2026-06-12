package com.example.CafeAutoSystem.ai_rpa.service;

import com.example.CafeAutoSystem.ai_rpa.dto.OrderItemDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class RpaExcelService {

    public String createOrderExcelSheet(String vendorName, List<OrderItemDto> orderList) {
        log.info("📂 [RPA 엑셀 엔진] {}용 자동 발주 명세서 파일(단가/금액 포함) 생성을 시작합니다.", vendorName);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("발주서_명세");

        // 엑셀 컬럼 너비 설정 설정 (칸이 깨지지 않도록 확장)
        sheet.setColumnWidth(0, 4000); // 발주일자
        sheet.setColumnWidth(1, 6000); // 거래처명
        sheet.setColumnWidth(2, 6000); // 요청 품목명
        sheet.setColumnWidth(3, 4500); // 발주 수량
        sheet.setColumnWidth(4, 4000); // 계약 단가 (추가)
        sheet.setColumnWidth(5, 5000); // 예상 금액 (추가)

        // 폰트 스타일 정의
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        // 헤더 스타일 스타일 (파란색 배경)
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);

        // 숫자 컴마 표시를 위한 셀 스타일 정의
        CellStyle numberStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        numberStyle.setDataFormat(format.getFormat("#,##0"));
        numberStyle.setAlignment(HorizontalAlignment.RIGHT);

        // 합계 행 전용 볼드 스타일 정의
        Font totalFont = workbook.createFont();
        totalFont.setBold(true);
        CellStyle totalStyle = workbook.createCellStyle();
        totalStyle.setFont(totalFont);
        totalStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        totalStyle.setAlignment(HorizontalAlignment.RIGHT);

        // 1. 헤더 생성 (단가, 예상 금액 컬럼 추가)
        Row headerRow = sheet.createRow(0);
        String[] headers = {"발주일자", "거래처명", "요청 품목명", "발주 수량(단위)", "계약 단가", "예상 금액"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 2. 데이터 적재 및 금액 연산
        int rowIdx = 1;
        String todayStr = LocalDate.now().toString();
        int totalOrderPrice = 0; // 총 금액 합산용 변수

        for (OrderItemDto item : orderList) {
            Row dataRow = sheet.createRow(rowIdx++);

            dataRow.createCell(0).setCellValue(todayStr);
            dataRow.createCell(1).setCellValue(vendorName);
            dataRow.createCell(2).setCellValue(item.getIngredientName());

            // 수량 표시
            String qtyWithUnit = item.getOrderQty() + " " + (item.getIngredientUnit() != null ? item.getIngredientUnit() : "개");
            dataRow.createCell(3).setCellValue(qtyWithUnit);

            // 계약 단가 바인딩 및 포맷팅
            Cell priceCell = dataRow.createCell(4);
            priceCell.setCellValue(item.getUnitPrice());
            priceCell.setCellStyle(numberStyle);

            // 예상 금액 바인딩 및 포맷팅 (화면에서 변경된 1:1 곱셈 수식 적용)
            Cell totalPriceCell = dataRow.createCell(5);
            totalPriceCell.setCellValue(item.getTotalPrice());
            totalPriceCell.setCellStyle(numberStyle);

            totalOrderPrice += item.getTotalPrice();
        }

        // 3. 맨 하단 총 발주 금액 합계 행 추가 (말과 행동의 일치 포인트!)
        Row totalRow = sheet.createRow(rowIdx);
        Cell labelCell = totalRow.createCell(4);
        labelCell.setCellValue("총 발주 금액:");
        labelCell.setCellStyle(totalStyle);

        Cell sumCell = totalRow.createCell(5);
        sumCell.setCellValue(totalOrderPrice);
        sumCell.setCellStyle(totalStyle);
        sumCell.setCellStyle(numberStyle); // 컴마 포맷 같이 지정

        // 파일 저장 로직
        String fileName = "Cafe_Order_" + vendorName + "_" + LocalDate.now() + ".xlsx";

        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
            return fileName;
        } catch (Exception e) {
            log.error("❌ [엑셀 엔진 오류] 파일 생성 실패: {}", e.getMessage());
            return null;
        } finally {
            try { workbook.close(); } catch (Exception e) { log.error(e.getMessage()); }
        }
    }
}