package com.tz.rental.landlord_management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tz.rental.landlord_management.application.dto.*;
import com.tz.rental.landlord_management.domain.model.valueobject.HouseType;
import com.tz.rental.landlord_management.domain.model.valueobject.RoomStatus;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LandlordEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.RoomEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.UserEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaHouseRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaLandlordRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaRoomRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "testuser")
class TanzaniaLandlordManagementSystemApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaLandlordRepository landlordRepository;

    @Autowired
    private JpaHouseRepository houseRepository;

    @Autowired
    private JpaRoomRepository roomRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private LandlordEntity testLandlord;

    @BeforeEach
    void setUp() {
        UserEntity testUser = new UserEntity();
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("password"));
        userRepository.save(testUser);

        testLandlord = new LandlordEntity();
        testLandlord.setUser(testUser);
        testLandlord.setFirstName("Test");
        testLandlord.setLastName("Landlord");
        testLandlord.setEmail("test.landlord@example.com");
        testLandlord.setPhoneNumber("+255712345678");
        testLandlord.setNationalId("12345678901234567890");
        landlordRepository.save(testLandlord);
    }

    @Test
    void testCreateHouseAndRoomFlow() throws Exception {
        // 1. Create a house
        CreateHouseRequest createHouseRequest = new CreateHouseRequest();
        createHouseRequest.setPropertyCode("PROP001");
        createHouseRequest.setName("Test House");
        createHouseRequest.setHouseType(HouseType.STANDALONE);
        createHouseRequest.setDistrict("Test District");
        createHouseRequest.setRegion("Test Region");
        createHouseRequest.setCountry("Tanzania");

        MvcResult houseResult = mockMvc.perform(post("/api/v1/houses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createHouseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.propertyCode", is("PROP001")))
                .andReturn();

        String houseId = JsonPath.read(houseResult.getResponse().getContentAsString(), "$.data.id");

        // 2. Create a room in that house
        CreateRoomRequest createRoomRequest = new CreateRoomRequest();
        createRoomRequest.setHouseId(UUID.fromString(houseId));
        createRoomRequest.setRoomNumber("R101");
        createRoomRequest.setMonthlyRent(new BigDecimal("100000"));

        mockMvc.perform(post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRoomRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.roomNumber", is("R101")));

        // 3. Verify the house now has one room
        mockMvc.perform(get("/api/v1/houses/" + houseId + "/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    void testUpdateAndDeleteFlow() throws Exception {
        // 1. Create a house and a room
        HouseEntity house = new HouseEntity();
        house.setId(UUID.randomUUID());
        house.setLandlord(testLandlord);
        house.setPropertyCode("PROP002");
        house.setName("Another House");
        house.setHouseType(HouseType.APARTMENT);
        house.setDistrict("Another District");
        house.setRegion("Another Region");
        house.setCountry("Tanzania");
        houseRepository.save(house);

        RoomEntity room = new RoomEntity();
        room.setId(UUID.randomUUID());
        room.setHouse(house);
        room.setRoomNumber("R201");
        room.setMonthlyRent(new BigDecimal("200000"));
        room.setStatus(RoomStatus.VACANT);
        roomRepository.save(room);

        // 2. Update the room
        CreateRoomRequest updateRoomRequest = new CreateRoomRequest();
        updateRoomRequest.setHouseId(house.getId());
        updateRoomRequest.setRoomNumber("R201-UPDATED");
        updateRoomRequest.setMonthlyRent(new BigDecimal("250000"));

        mockMvc.perform(put("/api/v1/rooms/" + room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRoomRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roomNumber", is("R201-UPDATED")));

        // 3. Delete the room
        mockMvc.perform(delete("/api/v1/rooms/" + room.getId()))
                .andExpect(status().isOk());

        // 4. Verify the room is deleted
        mockMvc.perform(get("/api/v1/rooms/" + room.getId()))
                .andExpect(status().isNotFound());

        // 5. Delete the house
        mockMvc.perform(delete("/api/v1/houses/" + house.getId()))
                .andExpect(status().isOk());

        // 6. Verify the house is deleted
        mockMvc.perform(get("/api/v1/houses/" + house.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFilteringAndDashboard() throws Exception {
        // 1. Create a house with two rooms (one vacant, one occupied)
        HouseEntity house = new HouseEntity();
        house.setId(UUID.randomUUID());
        house.setLandlord(testLandlord);
        house.setPropertyCode("PROP003");
        house.setName("Mixed House");
        house.setHouseType(HouseType.DUPLEX);
        house.setDistrict("Mixed District");
        house.setRegion("Mixed Region");
        house.setCountry("Tanzania");
        houseRepository.save(house);

        RoomEntity vacantRoom = new RoomEntity();
        vacantRoom.setId(UUID.randomUUID());
        vacantRoom.setHouse(house);
        vacantRoom.setRoomNumber("V1");
        vacantRoom.setMonthlyRent(new BigDecimal("300000"));
        vacantRoom.setStatus(RoomStatus.VACANT);
        roomRepository.save(vacantRoom);

        RoomEntity occupiedRoom = new RoomEntity();
        occupiedRoom.setId(UUID.randomUUID());
        occupiedRoom.setHouse(house);
        occupiedRoom.setRoomNumber("O1");
        occupiedRoom.setMonthlyRent(new BigDecimal("400000"));
        occupiedRoom.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(occupiedRoom);

        // 2. Filter rooms by status
        mockMvc.perform(get("/api/v1/houses/" + house.getId() + "/rooms?status=VACANT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].roomNumber", is("V1")));

        // 3. Filter houses by status
        mockMvc.perform(get("/api/v1/houses?status=VACANT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.houses", hasSize(1)));

        // 4. Verify dashboard stats
        mockMvc.perform(get("/api/v1/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalProperties", is(1)))
                .andExpect(jsonPath("$.data.totalRooms", is(2)))
                .andExpect(jsonPath("$.data.occupiedRooms", is(1)))
                .andExpect(jsonPath("$.data.vacantRooms", is(1)))
                .andExpect(jsonPath("$.data.expectedMonthlyIncome", is(400000)));
    }
}