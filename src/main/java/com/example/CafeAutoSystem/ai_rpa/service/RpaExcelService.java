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

    public String createOrderExcelSheet(String vendorName, List<OrderItemDto> orderList) {
        log.info("📂 [RPA 엑셀 엔진] {}용 자동 발주 명세서 파일 생성을 시작합니다.", vendorName);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("발주서_명세");

        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 4000);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        Row headerRow = sheet.createRow(0);

        String[] headers = {"발주일자", "거래처명", "요청 품목명", "발주 수량(단위)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        String todayStr = LocalDate.now().toString();

        for (OrderItemDto item : orderList) {
            Row dataRow = sheet.createRow(rowIdx++);
            dataRow.createCell(0).setCellValue(todayStr);
            dataRow.createCell(1).setCellValue(vendorName);
            dataRow.createCell(2).setCellValue(item.getIngredientName());

            // 변경: 기존 orderUnit 대신 데이터 정합성이 완료된 ingredientUnit 필드를 수거하도록 교정합니다.
            String qtyWithUnit = item.getOrderQty() + " " + (item.getIngredientUnit() != null ? item.getIngredientUnit() : "개");
            dataRow.createCell(3).setCellValue(qtyWithUnit);
        }

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