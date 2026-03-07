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

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/rate/{profileId}")
    public double rateProfile(@PathVariable Long profileId, @RequestBody double rating)  {
        Profile updatedProfile = profileService.rateProfile(profileId, rating);
        return updatedProfile.getRating();
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/rate/{profileId}")
    public double getProfileRate(@PathVariable Long profileId) {
            return profileService.getRating(profileId);
        }

        @PreAuthorize("isAuthenticated()")
        @GetMapping
        public ProfileDTO getProfile(@AuthenticationPrincipal UserDetailsImpl principal)  {
            Profile userProfile = profileService.getProfileByUserId(principal.getId());
            return profileMapper.mapTo(userProfile);
        }

        @PreAuthorize("isAuthenticated()")
        @PatchMapping
        public ProfileDTO updateProfile(@AuthenticationPrincipal UserDetailsImpl principal, @RequestBody ProfileDTO dto) {
            Profile updated = profileService.updateProfile(principal.getId(), dto);
            return profileMapper.mapTo(updated);
        }

        @GetMapping("/{profileId}/cars")
        public List<CarDTO> getProfileCars(@PathVariable Long profileId)  {
            return profileService.getProfileCars(profileId).stream()
                    .map(carMapper::mapTo)
                    .toList();
        }

        @PreAuthorize("isAuthenticated()")
        @PostMapping("/liked-cars/{carId}")
        public void addLikedCar(@AuthenticationPrincipal UserDetailsImpl principal,
                                @PathVariable Long carId) {
            profileService.addLikedCar(principal.getId(), carId);
        }

        @PreAuthorize("isAuthenticated()")
        @DeleteMapping("/liked-cars/{carId}")
        public void removeLikedCar(@AuthenticationPrincipal UserDetailsImpl principal,
                                   @PathVariable Long carId) {
            profileService.removeLikedCar(principal.getId(), carId);
        }

        @PreAuthorize("isAuthenticated()")
        @GetMapping("/liked-cars/{profileId}")
        public List<CarDTO> getLikedCarIdsByUserId(@PathVariable Long profileId)  {
            return profileService.findLikedByUserId(profileId);
        }
}
