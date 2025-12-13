package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.MonthlyIncomeReport;
import com.tz.rental.landlord_management.application.dto.MonthlyIncomeReportEntry;
import com.tz.rental.landlord_management.application.dto.VacancyReport;
import com.tz.rental.landlord_management.application.dto.VacancyReportEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ExcelGenerationService {

    private static final String REPORTS_DIR = "reports"; // Directory to save reports

    public byte[] generateMonthlyIncomeExcel(MonthlyIncomeReport report) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Monthly Income Report");

            // Header Style
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Data Style
            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setAlignment(HorizontalAlignment.LEFT);

            // Currency Style
            CellStyle currencyCellStyle = workbook.createCellStyle();
            currencyCellStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
            currencyCellStyle.setAlignment(HorizontalAlignment.RIGHT);

            // Title Row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Monthly Income Report for " + report.getReportMonth().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            titleCell.setCellStyle(headerCellStyle); // Apply header style to title

            // Summary Rows
            AtomicInteger rowNum = new AtomicInteger(2); // Start after title and a blank row
            createSummaryRow(sheet, rowNum.getAndIncrement(), "Total Expected Income:", report.getTotalExpectedIncome(), currencyCellStyle, headerCellStyle);
            createSummaryRow(sheet, rowNum.getAndIncrement(), "Total Actual Income:", report.getTotalActualIncome(), currencyCellStyle, headerCellStyle);
            createSummaryRow(sheet, rowNum.getAndIncrement(), "Total Outstanding Balance:", report.getTotalOutstandingBalance(), currencyCellStyle, headerCellStyle);

            rowNum.getAndIncrement(); // Add a blank row before details

            // Details Header Row
            Row detailHeaderRow = sheet.createRow(rowNum.getAndIncrement());
            String[] headers = {"Lease ID", "Tenant Name", "Room Number", "House Name", "Expected Rent (TZS)", "Amount Paid (TZS)", "Balance (TZS)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = detailHeaderRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Details Data Rows
            for (MonthlyIncomeReportEntry entry : report.getEntries()) {
                Row row = sheet.createRow(rowNum.getAndIncrement());
                row.createCell(0).setCellValue(entry.getLeaseId().toString());
                row.createCell(1).setCellValue(entry.getTenantName());
                row.createCell(2).setCellValue(entry.getRoomNumber());
                row.createCell(3).setCellValue(entry.getHouseName());
                createCellAndSetValue(row, 4, entry.getExpectedRent(), currencyCellStyle);
                createCellAndSetValue(row, 5, entry.getAmountPaid(), currencyCellStyle);
                createCellAndSetValue(row, 6, entry.getBalance(), currencyCellStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generateVacancyExcel(VacancyReport report) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Vacancy Report");

            // Header Style
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Data Style
            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setAlignment(HorizontalAlignment.LEFT);

            // Title Row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Vacancy Report as of " + report.getReportDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            titleCell.setCellStyle(headerCellStyle);

            // Summary Row
            Row summaryRow = sheet.createRow(2);
            Cell summaryLabelCell = summaryRow.createCell(0);
            summaryLabelCell.setCellValue("Total Vacant Rooms:");
            summaryLabelCell.setCellStyle(headerCellStyle);
            Cell summaryValueCell = summaryRow.createCell(1);
            summaryValueCell.setCellValue(report.getTotalVacantRooms());
            summaryValueCell.setCellStyle(dataCellStyle);

            // Details Header Row
            AtomicInteger rowNum = new AtomicInteger(4);
            Row detailHeaderRow = sheet.createRow(rowNum.getAndIncrement());
            String[] headers = {"Room ID", "Room Number", "House Name", "House ID", "Description"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = detailHeaderRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Details Data Rows
            for (VacancyReportEntry entry : report.getEntries()) {
                Row row = sheet.createRow(rowNum.getAndIncrement());
                row.createCell(0).setCellValue(entry.getRoomId().toString());
                row.createCell(1).setCellValue(entry.getRoomNumber());
                row.createCell(2).setCellValue(entry.getHouseName());
                row.createCell(3).setCellValue(entry.getHouseId().toString());
                row.createCell(4).setCellValue(entry.getRoomDescription());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void createSummaryRow(Sheet sheet, int rowNum, String label, BigDecimal value, CellStyle valueStyle, CellStyle labelStyle) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);
        Cell valueCell = row.createCell(1);
        createCellAndSetValue(valueCell, value, valueStyle);
    }

    private void createCellAndSetValue(Row row, int col, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(col);
        createCellAndSetValue(cell, value, style);
    }

    private void createCellAndSetValue(Cell cell, BigDecimal value, CellStyle style) {
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        } else {
            cell.setCellValue(0.00);
        }
        cell.setCellStyle(style);
    }
}