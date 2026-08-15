package edu.team.carshopbackend.service;

import edu.team.carshopbackend.dto.AuthDTO.ProfileDTO;
import edu.team.carshopbackend.dto.CarDTO;
import edu.team.carshopbackend.entity.Car;
import edu.team.carshopbackend.entity.Profile;
import edu.team.carshopbackend.error.exception.NotFoundException;
import edu.team.carshopbackend.mapper.impl.CarMapper;
import edu.team.carshopbackend.repository.CarRepository;
import edu.team.carshopbackend.repository.ProfileRepository;
import edu.team.carshopbackend.service.impl.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final CarRepository carRepository;
    private final UserService userService;
    private final CarMapper carMapper;

    @Transactional
    public Profile updateProfile(Long userId, ProfileDTO dto) {
        Profile profile = getProfileByUserId(userId);

        if (dto.getName() != null) profile.setName(dto.getName());
        if (dto.getPhoneNumber() != null) profile.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getProfileImage() != null) profile.setProfileImage(dto.getProfileImage());

        return profileRepository.save(profile);
    }

    /**
     * Persist the given profile.
     *
     * @param profile profile to save
     */
    public void save(Profile profile) {
        profileRepository.save(profile);
    }

    /**
     * Returns rating value for the profile identified by given id.
     *
     * @param profileId profile id
     * @return current rating
     * @throws NotFoundException when profile is not found
     */
    public double getRating(Long profileId) throws NotFoundException {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found by id: " + profileId));
        return profile.getRating();
    }

    /**
     * Returns profile belonging to given user id.
     *
     * @param userId id of the user
     * @return profile entity
     * @throws NotFoundException when user or profile is missing
     */
    public Profile getProfileByUserId(Long userId) throws NotFoundException {
        return userService.getUserById(userId).getProfile();
    }

    /**
     * Returns list of cars owned by the profile.
     *
     * @param profileId profile id
     * @return list of cars
     * @throws NotFoundException when profile is not found
     */
    public List<Car> getProfileCars(Long profileId) throws NotFoundException {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found by id: " + profileId));
        return profile.getCars();
    }

    /**
     * Adds a rating to profile and updates average rating and count.
     *
     * @param profileId id of the profile to rate
     * @param rating rating value to add
     * @return updated profile
     */
    @Transactional
    public Profile rateProfile(Long profileId, double rating) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found for profileId: " + profileId));

        double newRating = ((profile.getRating() * profile.getRatingCount()) + rating) / (profile.getRatingCount() + 1);
        profile.setRating(newRating);
        profile.setRatingCount(profile.getRatingCount() + 1);

        return profileRepository.save(profile);
    }

    /**
     * Adds the car to profile's liked cars if not already present.
     *
     * @param profileId profile id
     * @param carId car id to add
     * @throws NotFoundException when profile or car is not found
     */
    @Transactional
    public void addLikedCar(Long profileId, Long carId) throws NotFoundException {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car not found"));

        if (!profile.getLikedCars().contains(car)) {
            profile.getLikedCars().add(car);
            profileRepository.save(profile);
        }
    }

    /**
     * Removes the car from profile's liked cars.
     *
     * @param profileId profile id
     * @param carId car id to remove
     * @throws NotFoundException when profile or car is not found
     */
    @Transactional
    public void removeLikedCar(Long profileId, Long carId) throws NotFoundException {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car not found"));

        profile.getLikedCars().remove(car);
        profileRepository.save(profile);
    }

    /**
     * Returns DTOs for cars liked by the given profile.
     *
     * @param profileId profile id
     * @return list of CarDTO objects
     * @throws NotFoundException when profile is not found
     */
    public List<CarDTO> findLikedByUserId(Long profileId) throws NotFoundException {
        List<Car> likedCars = profileRepository.findLikedCarsByProfileId(profileId);
        return likedCars.stream()
                .map(carMapper::mapTo)
                .toList();
    }
}