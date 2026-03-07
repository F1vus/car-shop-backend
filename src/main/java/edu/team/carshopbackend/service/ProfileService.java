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

    public void save(Profile profile) {
        profileRepository.save(profile);
    }

    public double getRating(Long profileId) throws NotFoundException {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found by id: " + profileId));
        return profile.getRating();
    }

    public Profile getProfileByUserId(Long userId) throws NotFoundException {
        return userService.getUserById(userId).getProfile();
    }

    public List<Car> getProfileCars(Long profileId) throws NotFoundException {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found by id: " + profileId));
        return profile.getCars();
    }

    @Transactional
    public Profile rateProfile(Long profileId, double rating) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found for profileId: " + profileId));

        double newRating = ((profile.getRating() * profile.getRatingCount()) + rating) / (profile.getRatingCount() + 1);
        profile.setRating(newRating);
        profile.setRatingCount(profile.getRatingCount() + 1);

        return profileRepository.save(profile);
    }

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

    @Transactional
    public void removeLikedCar(Long profileId, Long carId) throws NotFoundException {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car not found"));

        profile.getLikedCars().remove(car);
        profileRepository.save(profile);
    }

    public List<CarDTO> findLikedByUserId(Long profileId) throws NotFoundException {
        List<Car> likedCars = profileRepository.findLikedCarsByProfileId(profileId);
        return likedCars.stream()
                .map(carMapper::mapTo)
                .toList();
    }
}