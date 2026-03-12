package edu.team.carshopbackend.service;

import edu.team.carshopbackend.client.PhotoClient;
import edu.team.carshopbackend.client.UploadPhotoResponse;
import edu.team.carshopbackend.dto.CarDTO;
import edu.team.carshopbackend.dto.CreateCarRequestDTO;
import edu.team.carshopbackend.entity.*;
import edu.team.carshopbackend.error.exception.NotFoundException;
import edu.team.carshopbackend.error.exception.PhotoUploadException;
import edu.team.carshopbackend.mapper.impl.CarMapper;
import edu.team.carshopbackend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final ColorRepository colorRepository;
    private final PetrolRepository petrolRepository;
    private final CarProducerRepository carProducerRepository;
    private final PhotoRepository photoRepository;
    private final CarMapper carMapper;
    private final PhotoClient photoClient;

    /**
     * Returns all cars stored in the repository.
     *
     * @return list of all cars
     */
    public List<Car> getAllProducts() {
        return carRepository.findAll();
    }

    /**
     * Find car by id.
     *
     * @param id car id
     * @return car entity
     * @throws NotFoundException if car not found
     */
    public Car getProductById(Long id) throws NotFoundException {
        return carRepository.findById(id).orElseThrow(() -> new NotFoundException("Car not found"));
    }

    /**
     * Suggest cars by name using a case-insensitive contains search.
     *
     * @param query search term
     * @return list of matching cars
     */
    public List<Car> suggestCar(String query) {
        return carRepository.findByNameContainingIgnoreCase(query);
    }

    /**
     * Updates an existing car partially using non-null fields from the provided car object.
     *
     * @param id id of the car to update
     * @param car car object with new values
     * @return updated car
     * @throws NotFoundException when car does not exist
     */
    public Car carUpdate(Long id, Car car) throws NotFoundException {
        car.setId(id);

        return carRepository.findById(id).map(existingCar -> {
            Optional.ofNullable(car.getName()).ifPresent(existingCar::setName);
            Optional.ofNullable(car.getPrice()).ifPresent(existingCar::setPrice);
            Optional.ofNullable(car.getDescription()).ifPresent(existingCar::setDescription);
            Optional.ofNullable(car.getColor()).ifPresent(existingCar::setColor);
            Optional.ofNullable(car.getMileage()).ifPresent(existingCar::setMileage);
            Optional.ofNullable(car.getCarState()).ifPresent(existingCar::setCarState);
            Optional.ofNullable(car.getPetrolType()).ifPresent(existingCar::setPetrolType);
            Optional.ofNullable(car.getEngineCapacity()).ifPresent(existingCar::setEngineCapacity);
            Optional.ofNullable(car.getPower()).ifPresent(existingCar::setPower);
            Optional.ofNullable(car.getYear()).ifPresent(existingCar::setYear);
            Optional.ofNullable(car.getPhotos()).ifPresent(existingCar::setPhotos);
            Optional.ofNullable(car.getProducent()).ifPresent(existingCar::setProducent);
            Optional.ofNullable(car.getHadAccidents()).ifPresent(existingCar::setHadAccidents);
            return carRepository.save(existingCar);
        }).orElseThrow(() -> new NotFoundException("Car does not exist with id " + id));
    }

    /**
     * Checks whether a car exists by id.
     *
     * @param id car id
     * @return true if exists, false otherwise
     */
    public boolean isExists(Long id) {
        return carRepository.existsById(id);
    }


    /**
     * Deletes the car identified by id.
     *
     * @param id car id
     */
    public void deleteCarById(Long id) {
        carRepository.deleteById(id);
    }


    /**
     * Creates a car entity from request data and uploads provided photos.
     *
     * @param req create car request DTO
     * @param photos list of multipart photo files (optional)
     * @param owner profile of the car owner
     * @return created car DTO
     */
    @Transactional
    public CarDTO createCarWithPhotos(CreateCarRequestDTO req, List<MultipartFile> photos, Profile owner) {
        Car car = createCarEntity(req, owner);

        if (!CollectionUtils.isEmpty(photos)) {
            attachPhotos(car, photos);
        }

        return carMapper.mapTo(car);
    }



    private Car createCarEntity(CreateCarRequestDTO req, Profile owner) {

        Car car = carMapper.mapFrom(req);

        car.setColor(getOrThrow(colorRepository, req.getColor(), "Color not found"));
        car.setPetrolType(getOrThrow(petrolRepository, req.getPetrolType(), "PetrolType not found"));
        car.setProducent(getOrThrow(carProducerRepository, req.getProducent(), "Producent not found"));

        car.setOwner(owner);

        return carRepository.save(car);
    }

    private void attachPhotos(Car car, List<MultipartFile> photos) {

        List<Photo> photoEntities = photos.stream()
                .map(file -> uploadAndCreatePhoto(car, file))
                .toList();

        photoRepository.saveAll(photoEntities);
    }

    private Photo uploadAndCreatePhoto(Car car, MultipartFile file) {
        try {
            UploadPhotoResponse response = photoClient.uploadPhoto(car.getId(), file);

            Photo photo = new Photo();
            photo.setCar(car);
            photo.setUrl(response.getUrl());

            return photo;

        } catch (IOException e) {
            throw new PhotoUploadException("Failed to upload photo");
        }
    }

    private <T> T getOrThrow(JpaRepository<T, Long> repo, Long id, String message) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException(message));
    }
}
