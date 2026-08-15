package edu.team.carshopbackend.controller;

import edu.team.carshopbackend.dto.AuthDTO.ProfileDTO;
import edu.team.carshopbackend.dto.CarDTO;
import edu.team.carshopbackend.entity.Profile;
import edu.team.carshopbackend.entity.impl.UserDetailsImpl;
import edu.team.carshopbackend.mapper.impl.CarMapper;
import edu.team.carshopbackend.mapper.impl.ProfileMapper;
import edu.team.carshopbackend.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final CarMapper carMapper;
    private final ProfileMapper profileMapper;

    /**
     * Rates the profile and returns updated average rating.
     *
     * @param profileId id of the profile to rate
     * @param rating rating value
     * @return updated average rating
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/rate/{profileId}")
    public double rateProfile(@PathVariable Long profileId, @RequestBody double rating)  {
        Profile updatedProfile = profileService.rateProfile(profileId, rating);
        return updatedProfile.getRating();
    }

    /**
     * Returns profile rating for given profile id.
     *
     * @param profileId profile id
     * @return current rating
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/rate/{profileId}")
    public double getProfileRate(@PathVariable Long profileId) {
            return profileService.getRating(profileId);
    }

    /**
     * Returns profile data for authenticated user.
     *
     * @param principal authenticated principal
     * @return profile DTO
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ProfileDTO getProfile(@AuthenticationPrincipal UserDetailsImpl principal) {
        Profile userProfile = profileService.getProfileByUserId(principal.getId());
        return profileMapper.mapTo(userProfile);
    }

    /**
     * Updates profile fields for authenticated user.
     *
     * @param principal authenticated principal
     * @param dto profile DTO with fields to update
     * @return updated ProfileDTO
     */
    @PreAuthorize("isAuthenticated()")
    @PatchMapping
    public ProfileDTO updateProfile(@AuthenticationPrincipal UserDetailsImpl principal, @RequestBody ProfileDTO dto) {
        Profile updated = profileService.updateProfile(principal.getId(), dto);
        return profileMapper.mapTo(updated);
    }

    /**
     * Returns list of CarDTOs owned by the profile.
     *
     * @param profileId profile id
     * @return list of cars
     */
    @GetMapping("/{profileId}/cars")
    public List<CarDTO> getProfileCars(@PathVariable Long profileId)  {
        return profileService.getProfileCars(profileId).stream()
            .map(carMapper::mapTo)
            .toList();
    }

    /**
     * Adds a car to the authenticated user's liked list.
     *
     * @param principal authenticated principal
     * @param carId id of car to like
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/liked-cars/{carId}")
    public void addLikedCar(@AuthenticationPrincipal UserDetailsImpl principal,
                                @PathVariable Long carId) {
            profileService.addLikedCar(principal.getId(), carId);
    }

    /**
     * Removes a car from the authenticated user's liked list.
     *
     * @param principal authenticated principal
     * @param carId id of car to remove
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/liked-cars/{carId}")
    public void removeLikedCar(@AuthenticationPrincipal UserDetailsImpl principal,
                                   @PathVariable Long carId) {
        profileService.removeLikedCar(principal.getId(), carId);
    }

    /**
     * Returns list of cars liked by the specified profile.
     *
     * @param profileId profile id
     * @return list of CarDTOs
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/liked-cars/{profileId}")
    public List<CarDTO> getLikedCarIdsByUserId(@PathVariable Long profileId)  {
        return profileService.findLikedByUserId(profileId);
    }
}
