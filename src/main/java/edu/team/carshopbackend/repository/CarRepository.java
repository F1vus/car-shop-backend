package edu.team.carshopbackend.repository;

import edu.team.carshopbackend.entity.Car;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByNameContainingIgnoreCase(String namePart);

    @EntityGraph(attributePaths = {
            "color",
            "producent",
            "petrolType",
            "owner",
            "owner.user",
            "photos"
    })
    List<Car> findAll();
}
