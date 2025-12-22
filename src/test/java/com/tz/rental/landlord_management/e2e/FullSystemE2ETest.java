package com.tz.rental.landlord_management.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tz.rental.landlord_management.api.rest.dto.ApiResponse;
import com.tz.rental.landlord_management.application.dto.*;
import com.tz.rental.landlord_management.domain.model.valueobject.HouseType;
import com.tz.rental.landlord_management.domain.model.valueobject.PaymentPeriod;
import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
public class FullSystemE2ETest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_pass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // Ensure Hibernate creates the schema for the test DB
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    // Store IDs for subsequent requests
    private UUID houseId;
    private UUID roomId;
    private UUID tenantId;
    private UUID leaseId;
    private UUID paymentId;
    private UUID contractTemplateId;
    private String contractDocumentUrl;

    @BeforeAll
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @Transactional
    @WithMockUser(username = "landlord", roles = "LANDLORD") // Run the test as the default 'landlord' user
    void testFullRentalManagementFlow() throws Exception {
        // 1. Create House (Landlord is derived from security context)
        createHouse();

        // 2. Create Room
        createRoom();

        // 3. Create Tenant
        createTenant();

        // 4. Create Lease
        createLease();

        // 5. Create Contract Template
        createContractTemplate();

        // 6. Generate PDF Contract with Template
        generatePdfContractWithTemplate();

        // 7. Record Payment
        recordPayment();

        // 8. Verify Room Status
        verifyRoomStatus();

        // 9. Verify PDF File Existence
        verifyPdfFileExistence();
    }

    private void createHouse() throws Exception {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        CreateHouseRequest houseRequest = new CreateHouseRequest();
        houseRequest.setPropertyCode("TEST-HOUSE-" + uniqueId);
        houseRequest.setName("Test House");
        houseRequest.setDescription("A house for E2E testing.");
        houseRequest.setHouseType(HouseType.STANDALONE);
        // landlordId is now removed from the request
        houseRequest.setStreetAddress("123 Test Street");
        houseRequest.setDistrict("Test District");
        houseRequest.setRegion("Dar es Salaam");
        houseRequest.setCountry("Tanzania");
        houseRequest.setTotalFloors(2);
        houseRequest.setYearBuilt(2020);
        houseRequest.setHasParking(true);
        houseRequest.setHasSecurity(false);
        houseRequest.setMonthlyCommonCharges(BigDecimal.valueOf(50000));

        MvcResult result = mockMvc.perform(post("/api/v1/houses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(houseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Test House"))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        ApiResponse<HouseResponse> apiResponse = objectMapper.readValue(responseString, new com.fasterxml.jackson.core.type.TypeReference<ApiResponse<HouseResponse>>() {});
        houseId = apiResponse.getData().getId();
        assertNotNull(houseId);
        System.out.println("Created House with ID: " + houseId);
    }

    private void createRoom() throws Exception {
        CreateRoomRequest roomRequest = new CreateRoomRequest();
        roomRequest.setHouseId(houseId);
        roomRequest.setRoomNumber("R101");
        roomRequest.setDescription("First floor room.");
        roomRequest.setMonthlyRent(BigDecimal.valueOf(250000));

        MvcResult result = mockMvc.perform(post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roomNumber").value("R101"))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        ApiResponse<RoomResponse> apiResponse = objectMapper.readValue(responseString, new com.fasterxml.jackson.core.type.TypeReference<ApiResponse<RoomResponse>>() {});
        roomId = apiResponse.getData().getId();
        assertNotNull(roomId);
        System.out.println("Created Room with ID: " + roomId);
    }

    private void createTenant() throws Exception {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String nineDigitNumber = UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 9);

        CreateTenantRequest tenantRequest = new CreateTenantRequest();
        tenantRequest.setFirstName("Test");
        tenantRequest.setLastName("Tenant");
        tenantRequest.setEmail("test.tenant." + uniqueId + "@example.com");
        tenantRequest.setPhoneNumber("+255" + nineDigitNumber);
        tenantRequest.setNationalId("TN" + nineDigitNumber + "10");
        tenantRequest.setEmergencyContactName("Emergency Contact");
        tenantRequest.setEmergencyContactPhone("+255" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 9));

        MvcResult result = mockMvc.perform(post("/api/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tenantRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.firstName").value("Test"))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        ApiResponse<TenantResponse> apiResponse = objectMapper.readValue(responseString, new com.fasterxml.jackson.core.type.TypeReference<ApiResponse<TenantResponse>>() {});
        tenantId = apiResponse.getData().getId();
        assertNotNull(tenantId);
        System.out.println("Created Tenant with ID: " + tenantId);
    }

    private void createLease() throws Exception {
        CreateLeaseRequest leaseRequest = new CreateLeaseRequest();
        leaseRequest.setTenantId(tenantId);
        leaseRequest.setRoomId(roomId);
        leaseRequest.setStartDate(LocalDate.now().plusDays(1));
        leaseRequest.setEndDate(LocalDate.now().plusYears(1).plusDays(1));
        leaseRequest.setRentAmount(BigDecimal.valueOf(250000));
        leaseRequest.setPaymentPeriod(PaymentPeriod.MONTHLY);

        MvcResult result = mockMvc.perform(post("/api/v1/leases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leaseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tenantId").value(tenantId.toString()))
                .andExpect(jsonPath("$.data.roomId").value(roomId.toString()))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        ApiResponse<LeaseResponse> apiResponse = objectMapper.readValue(responseString, new com.fasterxml.jackson.core.type.TypeReference<ApiResponse<LeaseResponse>>() {});
        leaseId = apiResponse.getData().getId();
        assertNotNull(leaseId);
        System.out.println("Created Lease with ID: " + leaseId);
    }

    private void createContractTemplate() throws Exception {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        CreateContractTemplateRequest templateRequest = new CreateContractTemplateRequest();
        templateRequest.setName("Standard Lease Template " + uniqueId);
        templateRequest.setContent("This Lease Agreement is made between {{landlordName}} and {{tenantName}} for the property at {{houseAddress}}, Room {{roomNumber}}.\n" +
                "Lease Period: From {{leaseStartDate}} to {{leaseEndDate}}.\n" +
                "Monthly Rent: {{rentAmount}} TZS.\n\n" +
                "Signatures:\nLandlord: {{landlordName}}\nTenant: {{tenantName}}\nDate: {{currentDate}}");
        templateRequest.setDescription("A standard template for residential lease agreements.");

        MvcResult result = mockMvc.perform(post("/api/v1/contract-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(templateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Standard Lease Template " + uniqueId))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        ApiResponse<ContractTemplateResponse> apiResponse = objectMapper.readValue(responseString, new com.fasterxml.jackson.core.type.TypeReference<ApiResponse<ContractTemplateResponse>>() {});
        contractTemplateId = apiResponse.getData().getId();
        assertNotNull(contractTemplateId);
        System.out.println("Created Contract Template with ID: " + contractTemplateId);
    }

    private void generatePdfContractWithTemplate() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/leases/" + leaseId + "/generate-contract")
                        .param("templateId", contractTemplateId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.contractDocumentUrl").exists())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        ApiResponse<LeaseResponse> apiResponse = objectMapper.readValue(responseString, new com.fasterxml.jackson.core.type.TypeReference<ApiResponse<LeaseResponse>>() {});
        contractDocumentUrl = apiResponse.getData().getContractDocumentUrl();
        assertNotNull(contractDocumentUrl);
        System.out.println("Generated PDF contract with template at: " + contractDocumentUrl);
    }

    private void recordPayment() throws Exception {
        CreatePaymentRequest paymentRequest = new CreatePaymentRequest();
        paymentRequest.setLeaseId(leaseId);
        paymentRequest.setAmountPaid(BigDecimal.valueOf(250000));
        paymentRequest.setPaymentDate(LocalDate.now());
        paymentRequest.setTransactionReference("PAYREF-" + UUID.randomUUID().toString().substring(0, 8));

        MvcResult result = mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.leaseId").value(leaseId.toString()))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        ApiResponse<PaymentResponse> apiResponse = objectMapper.readValue(responseString, new com.fasterxml.jackson.core.type.TypeReference<ApiResponse<PaymentResponse>>() {});
        paymentId = apiResponse.getData().getId();
        assertNotNull(paymentId);
        System.out.println("Recorded Payment with ID: " + paymentId);
    }

    private void verifyRoomStatus() throws Exception {
        mockMvc.perform(get("/api/v1/rooms/" + roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value(RoomStatus.OCCUPIED.name()));
        System.out.println("Verified Room status is OCCUPIED.");
    }

    private void verifyPdfFileExistence() {
        File pdfFile = new File(contractDocumentUrl);
        assertTrue(pdfFile.exists(), "Generated PDF file should exist at " + contractDocumentUrl);
        assertTrue(pdfFile.length() > 0, "Generated PDF file should not be empty.");
        System.out.println("Verified PDF file exists and is not empty.");
    }
}