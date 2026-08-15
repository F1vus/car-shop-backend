package edu.team.carshopbackend.controller;

import edu.team.carshopbackend.dto.CarDTO;
import edu.team.carshopbackend.dto.CarSuggestionDTO;
import edu.team.carshopbackend.dto.request.CreateCarRequest;
import edu.team.carshopbackend.dto.PhotoDTO;
import edu.team.carshopbackend.dto.request.UpdateCarRequest;
import edu.team.carshopbackend.entity.Car;
import edu.team.carshopbackend.entity.impl.UserDetailsImpl;
import edu.team.carshopbackend.mapper.impl.CarMapper;
import edu.team.carshopbackend.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@Tag(name = "Car Controller", description = "Manages cars in the system")
public class CarController {

    private final CarService carService;
    private final CarMapper carMapper;

    /**
     * Creates a new car with optional photos for the authenticated user.
     *
     * @param carRequest car creation data
     * @param photos optional photos attached to the car
     * @param principal authenticated principal providing owner profile
     * @return created CarDTO
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/cars", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Creates a new car", description = "Creates a new car in the system based on the submitted DTO data and returns the created object")
    public CarDTO createCar(
            @Valid @RequestPart("car") CreateCarRequest carRequest,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            @AuthenticationPrincipal UserDetailsImpl principal
    ) {
        return carService.createCarWithPhotos(carRequest, photos, principal.getProfile());
    }

    /**
     * Returns DTOs for all cars.
     *
     * @return list of CarDTO
     */
    @GetMapping("/cars")
    @Operation(summary = "Retrieves a list of all cars", description = "Returns a list of all cars registered in the system.")
    public List<CarDTO> findAllAutos() {
        return carService.getAllProducts().stream()
                .map(carMapper::mapTo)
                .toList();
    }

    /**
     * Returns car by id.
     *
     * @param id car id
     * @return CarDTO for the car
     */
    @GetMapping("/cars/{id}")
    @Operation(summary = "Picks up the car by ID", description = "Returns the car with the specified ID, if it exists. If the car does not exist, returns status 404.")
    public CarDTO findAutoById(@PathVariable Long id) {
        Car car = carService.getProductById(id);
        return carMapper.mapTo(car);
    }

    /**
     * Partially updates a car identified by id using values from provided DTO.
     *
     * @param id car id
     * @param updateCarRequest DTO containing updated fields
     * @return updated CarDTO
     */
    @PatchMapping("/cars/{id}")
    @Operation(summary = "Updates the car", description = "Updates the existing car with the specified ID. Returns the updated object or throws an exception if the car does not exist.")
    public CarDTO updateCar(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCarRequest updateCarRequest,
            @AuthenticationPrincipal UserDetailsImpl principal) {

        Car carEntity = carMapper.mapFrom(updateCarRequest);
        Car updatedCar = carService.carUpdate(id, carEntity, principal);
        return carMapper.mapTo(updatedCar);
    }

    /**
     * Delete car by car id
     *
     * @param id car id
     */
    @DeleteMapping("/cars/{id}")
    @Operation(summary = "Removes the car", description = "Removes the vehicle with the specified ID from the system.")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCarById(id);
    }

    /**
     * Returns suggested cars matching the query.
     *
     * @param query search string (minimum length 2)
     * @return list of suggestions
     */
    @GetMapping("/cars/suggestions")
    @Operation(summary = "Tips for naming cars", description = "car return list")
    public List<CarSuggestionDTO> suggestionCar(@RequestParam String query) {
        if (query.length() < 2) {
            return List.of();
        }

        return carService.suggestCar(query)
                .stream()
                .map(car -> new CarSuggestionDTO(
                        car.getId(),
                        car.getName(),
                        car.getPrice(),
                        car.getPhotos()
                                .stream()
                                .map(photo -> new PhotoDTO(photo.getId(), photo.getUrl())).toList()
                        ))
                .distinct()
                .limit(10)
                .toList();
    }

}

