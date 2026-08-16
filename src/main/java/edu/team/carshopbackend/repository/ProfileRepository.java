package edu.team.carshopbackend.repository;

import edu.team.carshopbackend.entity.Car;
import edu.team.carshopbackend.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    @Query("SELECT c FROM Profile p JOIN p.likedCars c WHERE p.id = :profileId")
    List<Car> findLikedCarsByProfileId(@Param("profileId") Long profileId);

    Profile findProfileByUserId(Long userId);
}