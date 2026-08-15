package edu.team.carshopbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.team.carshopbackend.config.jwtConfig.JwtCore;
import edu.team.carshopbackend.dto.CarDTO;
import edu.team.carshopbackend.dto.request.CreateCarRequest;
import edu.team.carshopbackend.entity.Car;
import edu.team.carshopbackend.entity.Photo;
import edu.team.carshopbackend.entity.Profile;
import edu.team.carshopbackend.entity.impl.UserDetailsImpl;
import edu.team.carshopbackend.mapper.impl.CarMapper;
import edu.team.carshopbackend.repository.JwtTokenRepository;
import edu.team.carshopbackend.service.CarService;
import edu.team.carshopbackend.service.impl.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
@AutoConfigureMockMvc(addFilters = false)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtCore jwtCore;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CarService carService;

    @MockitoBean
    private CarMapper carMapper;

    @MockitoBean
    private JwtTokenRepository jwtTokenRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateCarSuccessfully() throws Exception {

        Profile profile = new Profile();

        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getProfile()).thenReturn(profile);
        when(userDetails.getAuthorities()).thenReturn(List.of());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        CreateCarRequest requestDto = new CreateCarRequest();
        requestDto.setName("Audi");

        CarDTO responseDto = new CarDTO();
        responseDto.setId(1L);
        responseDto.setName("Audi");

        when(carService.createCarWithPhotos(any(), any(), eq(profile)))
                .thenReturn(responseDto);

        MockMultipartFile carPart = new MockMultipartFile(
                "car",
                "car.json",
                "application/json",
                objectMapper.writeValueAsBytes(requestDto)
        );

        mockMvc.perform(multipart("/api/v1/cars")
                        .file(carPart))
                .andExpect(status().isOk());
    }



    @Test
    void shouldUpdateCarSuccessfully() throws Exception {
        Long id = 1L;

        CarDTO requestDto = new CarDTO();
        requestDto.setName("Updated Car");

        Car entity = new Car();
        entity.setId(id);
        entity.setName("Updated Car");

        Car updatedEntity = new Car();
        updatedEntity.setId(id);
        updatedEntity.setName("Updated Car");

        CarDTO responseDto = new CarDTO();
        responseDto.setId(id);
        responseDto.setName("Updated Car");

        when(carService.isExists(id)).thenReturn(true);
        when(carMapper.mapFrom(Mockito.any(CarDTO.class))).thenReturn(entity);
        when(carService.carUpdate(Mockito.eq(id), Mockito.any(Car.class))).thenReturn(updatedEntity);
        when(carMapper.mapTo(Mockito.any(Car.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/api/v1/cars/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Car")));
    }

    @Test
    void shouldDeleteCarSuccessfully() throws Exception {
        Long id = 1L;
        Mockito.doNothing().when(carService).deleteCarById(id);

        mockMvc.perform(delete("/api/v1/cars/{id}", id))
                .andExpect(status().isOk());

        Mockito.verify(carService, Mockito.times(1)).deleteCarById(id);
    }

    @Test
    void shouldSuggestCarSuccessfully() throws Exception {
        Car car1 = new Car();
        car1.setName("Toyota");
        car1.setPhotos(List.of(new Photo()));

        Car car2 = new Car();
        car2.setName("Toro");
        car2.setPhotos(List.of(new Photo()));

        when(carService.suggestCar("to")).thenReturn(List.of(car1,car2));
        mockMvc.perform(get("/api/v1/cars/suggestions")
                        .param("query","to")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].name").value("Toyota"))
                .andExpect(jsonPath("$[1].name").value("Toro"));

        verify(carService, Mockito.times(1)).suggestCar("to");
    }



    @Test
    void shouldSuggestionEmptyList() throws Exception {


        when(carService.suggestCar("to")).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/v1/cars/suggestions")
                        .param("query","to")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", is(0)));


        verify(carService, Mockito.times(1)).suggestCar("to");


    }

    @Test
    void shouldSuggestionWithSpecialCharacters() throws Exception {

        Car car1 = new Car();
        car1.setName("@!Tojota");
        car1.setPhotos(new ArrayList<>());

        Car car2 = new Car();
        car2.setName("-L");
        car2.setPhotos(new ArrayList<>());

        Car car3 = new Car();
        car3.setName("#");
        car3.setPhotos(new ArrayList<>());

        Car car4 = new Car();
        car4.setName("$%Tes");
        car4.setPhotos(new ArrayList<>());

        Car car5 = new Car();
        car5.setName("^Au");
        car5.setPhotos(new ArrayList<>());

        Car car6 = new Car();
        car6.setName("^Au*&*");
        car6.setPhotos(new ArrayList<>());

        Car car7 = new Car();
        car7.setName("(BM)");
        car7.setPhotos(new ArrayList<>());

        when(carService.suggestCar("@#$%^&*()")).thenReturn(List.of(car1,car2,car3,car4,car5,car6,car7));
        mockMvc.perform(get("/api/v1/cars/suggestions")
                        .param("query", "@#$%^&*()")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", is(7)))
                .andExpect(jsonPath("$[0].name").value("@!Tojota"))
                .andExpect(jsonPath("$[1].name").value("-L"))
                .andExpect(jsonPath("$[2].name").value("#"))
                .andExpect(jsonPath("$[3].name").value("$%Tes"))
                .andExpect(jsonPath("$[4].name").value("^Au"))
                .andExpect(jsonPath("$[5].name").value("^Au*&*"))
                .andExpect(jsonPath("$[6].name").value("(BM)"));




        verify(carService, Mockito.times(1)).suggestCar("@#$%^&*()");



    }


    @Test
    void shouldSuggestiontCarsIgnoringCase() throws Exception {
        Car car1 = new Car();
        car1.setName("Tojota");
        car1.setPhotos(new ArrayList<>());

        Car car2 = new Car();
        car2.setName("TOJO");
        car2.setPhotos(new ArrayList<>());

        Car car3 = new Car();
        car3.setName("TOro");
        car3.setPhotos(new ArrayList<>());

        Car car4 = new Car();
        car4.setName("toDO");
        car4.setPhotos(new ArrayList<>());


        when(carService.suggestCar("to")).thenReturn(List.of(car1,car2,car3,car4));
        mockMvc.perform(get("/api/v1/cars/suggestions")
                        .param("query", "to")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", is(4)))
                .andExpect(jsonPath("$[0].name").value("Tojota"))
                .andExpect(jsonPath("$[1].name").value("TOJO"))
                .andExpect(jsonPath("$[2].name").value("TOro"))
                .andExpect(jsonPath("$[3].name").value("toDO"));

        verify(carService, Mockito.times(1)).suggestCar("to");


    }

}
