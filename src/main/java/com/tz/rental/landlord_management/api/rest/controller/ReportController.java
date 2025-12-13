package com.tz.rental.landlord_management.api.rest.controller;

import com.tz.rental.landlord_management.api.rest.dto.ApiResponse;
import com.tz.rental.landlord_management.application.dto.MonthlyIncomeReport;
import com.tz.rental.landlord_management.application.dto.VacancyReport;
import com.tz.rental.landlord_management.application.service.ExcelGenerationService;
import com.tz.rental.landlord_management.application.service.PdfGenerationService;
import com.tz.rental.landlord_management.application.service.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Endpoints for generating various financial and operational reports")
public class ReportController {

    private final ReportingService reportingService;
    private final PdfGenerationService pdfGenerationService;
    private final ExcelGenerationService excelGenerationService;

    @GetMapping("/income/monthly")
    @Operation(summary = "Generate Monthly Income Report",
            description = "Generates a detailed report of expected, actual, and outstanding income for a given month.")
    public ResponseEntity<ApiResponse<MonthlyIncomeReport>> getMonthlyIncomeReport(
            @Parameter(description = "Year for the report (e.g., 2025)", example = "2025")
            @RequestParam int year,
            @Parameter(description = "Month for the report (1-12, e.g., 1 for January)", example = "1")
            @RequestParam int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        MonthlyIncomeReport report = reportingService.generateMonthlyIncomeReport(yearMonth);
        return ResponseEntity.ok(ApiResponse.success("Monthly income report generated successfully", report));
    }

    @GetMapping("/income/monthly/pdf")
    @Operation(summary = "Export Monthly Income Report as PDF",
            description = "Exports the detailed monthly income report as a PDF file.")
    public ResponseEntity<byte[]> exportMonthlyIncomeReportPdf(
            @Parameter(description = "Year for the report (e.g., 2025)", example = "2025")
            @RequestParam int year,
            @Parameter(description = "Month for the report (1-12, e.g., 1 for January)", example = "1")
            @RequestParam int month) throws IOException {
        YearMonth yearMonth = YearMonth.of(year, month);
        MonthlyIncomeReport report = reportingService.generateMonthlyIncomeReport(yearMonth);
        byte[] pdfBytes = pdfGenerationService.generateMonthlyIncomePdf(report);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "monthly_income_report_" + yearMonth.format(DateTimeFormatter.ofPattern("yyyy_MM")) + ".pdf";
        headers.setContentDispositionFormData("attachment", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);
    }

    @GetMapping("/income/monthly/excel")
    @Operation(summary = "Export Monthly Income Report as Excel",
            description = "Exports the detailed monthly income report as an Excel (XLSX) file.")
    public ResponseEntity<byte[]> exportMonthlyIncomeReportExcel(
            @Parameter(description = "Year for the report (e.g., 2025)", example = "2025")
            @RequestParam int year,
            @Parameter(description = "Month for the report (1-12, e.g., 1 for January)", example = "1")
            @RequestParam int month) throws IOException {
        YearMonth yearMonth = YearMonth.of(year, month);
        MonthlyIncomeReport report = reportingService.generateMonthlyIncomeReport(yearMonth);
        byte[] excelBytes = excelGenerationService.generateMonthlyIncomeExcel(report);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        String filename = "monthly_income_report_" + yearMonth.format(DateTimeFormatter.ofPattern("yyyy_MM")) + ".xlsx";
        headers.setContentDispositionFormData("attachment", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(excelBytes, headers, org.springframework.http.HttpStatus.OK);
    }

    @GetMapping("/vacancies")
    @Operation(summary = "Generate Vacancy Report",
            description = "Generates a report detailing all currently vacant rooms.")
    public ResponseEntity<ApiResponse<VacancyReport>> getVacancyReport() {
        VacancyReport report = reportingService.generateVacancyReport();
        return ResponseEntity.ok(ApiResponse.success("Vacancy report generated successfully", report));
    }

    @GetMapping("/vacancies/pdf")
    @Operation(summary = "Export Vacancy Report as PDF",
            description = "Exports the vacancy report as a PDF file.")
    public ResponseEntity<byte[]> exportVacancyReportPdf() throws IOException {
        VacancyReport report = reportingService.generateVacancyReport();
        byte[] pdfBytes = pdfGenerationService.generateVacancyPdf(report);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "vacancy_report_" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".pdf";
        headers.setContentDispositionFormData("attachment", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);
    }

    @GetMapping("/vacancies/excel")
    @Operation(summary = "Export Vacancy Report as Excel",
            description = "Exports the vacancy report as an Excel (XLSX) file.")
    public ResponseEntity<byte[]> exportVacancyReportExcel() throws IOException {
        VacancyReport report = reportingService.generateVacancyReport();
        byte[] excelBytes = excelGenerationService.generateVacancyExcel(report);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        String filename = "vacancy_report_" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".xlsx";
        headers.setContentDispositionFormData("attachment", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(excelBytes, headers, org.springframework.http.HttpStatus.OK);
    }
}