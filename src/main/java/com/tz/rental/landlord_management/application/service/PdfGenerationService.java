package com.tz.rental.landlord_management.application.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment; // Added import
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.tz.rental.landlord_management.application.dto.MonthlyIncomeReport;
import com.tz.rental.landlord_management.application.dto.MonthlyIncomeReportEntry;
import com.tz.rental.landlord_management.application.dto.VacancyReport;
import com.tz.rental.landlord_management.application.dto.VacancyReportEntry;
import com.tz.rental.landlord_management.domain.model.aggregate.*;
import com.tz.rental.landlord_management.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate; // Added import
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGenerationService {

    private static final String REPORTS_DIR = "reports";
    private static final String CONTRACTS_DIR = "contracts";

    private final ContractTemplateRepository contractTemplateRepository;
    private final TenantRepository tenantRepository;
    private final RoomRepository roomRepository;
    private final HouseRepository houseRepository;
    private final LandlordRepository landlordRepository;

    public String generateContract(Lease lease, Tenant tenant, UUID templateId) throws IOException {
        Path contractsPath = Paths.get(CONTRACTS_DIR);
        if (!Files.exists(contractsPath)) {
            Files.createDirectories(contractsPath);
        }

        String fileName = "lease-" + lease.getId().value() + ".pdf";
        File file = new File(CONTRACTS_DIR + "/" + fileName);

        ContractTemplate template = contractTemplateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Contract template not found."));

        // Fetch all necessary data for placeholders
        Room room = roomRepository.findById(lease.getRoomId().value())
                .orElseThrow(() -> new IllegalArgumentException("Room not found for lease."));
        House house = houseRepository.findById(room.getHouseId())
                .orElseThrow(() -> new IllegalArgumentException("House not found for room."));
        // FIX: Wrap UUID in Landlord.LandlordId
        Landlord landlord = landlordRepository.findById(new Landlord.LandlordId(house.getLandlordId().value()))
                .orElseThrow(() -> new IllegalArgumentException("Landlord not found for house."));


        String populatedContent = populateTemplate(template.getContent(), lease, tenant, room, house, landlord);

        try (PdfWriter writer = new PdfWriter(file);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Add content paragraph by paragraph, assuming simple text for now
            // For more complex templates (HTML/Markdown), a parser would be needed
            for (String line : populatedContent.split("\n")) {
                document.add(new Paragraph(line));
            }
        }

        return file.getPath();
    }

    private String populateTemplate(String templateContent, Lease lease, Tenant tenant, Room room, House house, Landlord landlord) {
        String content = templateContent;

        // Tenant details
        content = content.replace("{{tenantName}}", tenant.getFirstName() + " " + tenant.getLastName());
        content = content.replace("{{tenantFirstName}}", tenant.getFirstName());
        content = content.replace("{{tenantLastName}}", tenant.getLastName());
        content = content.replace("{{tenantEmail}}", tenant.getEmail().getValue());
        content = content.replace("{{tenantPhoneNumber}}", tenant.getPhoneNumber().getValue());
        content = content.replace("{{tenantNationalId}}", tenant.getNationalId());

        // Landlord details
        content = content.replace("{{landlordName}}", landlord.getFirstName() + " " + landlord.getLastName());
        content = content.replace("{{landlordFirstName}}", landlord.getFirstName());
        content = content.replace("{{landlordLastName}}", landlord.getLastName());
        content = content.replace("{{landlordEmail}}", landlord.getEmail().getValue());
        content = content.replace("{{landlordPhoneNumber}}", landlord.getPhoneNumber().getValue());

        // House details
        content = content.replace("{{houseName}}", house.getName());
        content = content.replace("{{propertyCode}}", house.getPropertyCode());
        content = content.replace("{{houseAddress}}", house.getAddress().getFullAddress());
        content = content.replace("{{houseDistrict}}", house.getAddress().getDistrict());
        content = content.replace("{{houseRegion}}", house.getAddress().getRegion());

        // Room details
        content = content.replace("{{roomNumber}}", room.getRoomNumber());
        content = content.replace("{{roomDescription}}", room.getDescription());

        // Lease details
        content = content.replace("{{leaseId}}", lease.getId().value().toString());
        content = content.replace("{{leaseStartDate}}", lease.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        content = content.replace("{{leaseEndDate}}", lease.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        content = content.replace("{{rentAmount}}", lease.getRentAmount().toString());
        content = content.replace("{{paymentPeriod}}", lease.getPaymentPeriod().name());
        content = content.replace("{{leaseStatus}}", lease.getStatus().name());

        // Current Date
        content = content.replace("{{currentDate}}", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

        return content;
    }

    public byte[] generateMonthlyIncomePdf(MonthlyIncomeReport report) throws IOException {
        Path reportsPath = Paths.get(REPORTS_DIR);
        if (!Files.exists(reportsPath)) {
            Files.createDirectories(reportsPath);
        }

        try (PdfWriter writer = new PdfWriter(new ByteArrayOutputStream()); // Write to BAOS for byte[] return
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph("Monthly Income Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(24));
            document.add(new Paragraph("For " + report.getReportMonth().format(DateTimeFormatter.ofPattern("MMMM yyyy")))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18));
            document.add(new Paragraph("\n"));

            // Summary Table
            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
            summaryTable.setWidth(UnitValue.createPercentValue(80)).setHorizontalAlignment(HorizontalAlignment.CENTER);

            addSummaryRow(summaryTable, "Total Expected Income:", report.getTotalExpectedIncome());
            addSummaryRow(summaryTable, "Total Actual Income:", report.getTotalActualIncome());
            addSummaryRow(summaryTable, "Total Outstanding Balance:", report.getTotalOutstandingBalance());
            document.add(summaryTable);
            document.add(new Paragraph("\n"));

            // Details Table
            document.add(new Paragraph("Details:")
                    .setBold()
                    .setFontSize(14));
            Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{1, 2, 1, 2, 1, 1, 1}));
            detailsTable.setWidth(UnitValue.createPercentValue(100));

            addTableHeader(detailsTable, "Lease ID", "Tenant Name", "Room", "House", "Expected (TZS)", "Paid (TZS)", "Balance (TZS)");

            for (MonthlyIncomeReportEntry entry : report.getEntries()) {
                addTableRow(detailsTable,
                        entry.getLeaseId().toString().substring(0, 8) + "...", // Shorten UUID for table
                        entry.getTenantName(),
                        entry.getRoomNumber(),
                        entry.getHouseName(),
                        entry.getExpectedRent().toString(),
                        entry.getAmountPaid().toString(),
                        entry.getBalance().toString());
            }
            document.add(detailsTable);

            document.close();
            return ((ByteArrayOutputStream) writer.getOutputStream()).toByteArray();
        }
    }

    public byte[] generateVacancyPdf(VacancyReport report) throws IOException {
        Path reportsPath = Paths.get(REPORTS_DIR);
        if (!Files.exists(reportsPath)) {
            Files.createDirectories(reportsPath);
        }

        try (PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph("Vacancy Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(24));
            document.add(new Paragraph("As of " + report.getReportDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18));
            document.add(new Paragraph("\n"));

            // Summary
            document.add(new Paragraph("Total Vacant Rooms: " + report.getTotalVacantRooms())
                    .setBold()
                    .setFontSize(14));
            document.add(new Paragraph("\n"));

            // Details Table
            document.add(new Paragraph("Vacant Rooms Details:")
                    .setBold()
                    .setFontSize(14));
            Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 2, 1, 3}));
            detailsTable.setWidth(UnitValue.createPercentValue(100));

            addTableHeader(detailsTable, "Room ID", "Room No.", "House Name", "House ID", "Description");

            for (VacancyReportEntry entry : report.getEntries()) {
                addTableRow(detailsTable,
                        entry.getRoomId().toString().substring(0, 8) + "...",
                        entry.getRoomNumber(),
                        entry.getHouseName(),
                        entry.getHouseId().toString().substring(0, 8) + "...", // FIX: Use getHouseId()
                        entry.getRoomDescription());
            }
            document.add(detailsTable);

            document.close();
            return ((ByteArrayOutputStream) writer.getOutputStream()).toByteArray();
        }
    }

    private void addTableHeader(Table table, String... headers) {
        for (String header : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(header))
                    .setBold()
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(Border.NO_BORDER));
        }
    }

    private void addTableRow(Table table, String... cells) {
        for (String cellText : cells) {
            table.addCell(new Cell().add(new Paragraph(cellText))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setBorder(Border.NO_BORDER));
        }
    }

    private void addSummaryRow(Table table, String label, BigDecimal value) {
        table.addCell(new Cell().add(new Paragraph(label)).setBold().setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(value.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " TZS")).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
    }
}