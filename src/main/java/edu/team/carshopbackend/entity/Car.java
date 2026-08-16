package edu.team.carshopbackend.entity;

import edu.team.carshopbackend.entity.enums.CarState;
import edu.team.carshopbackend.entity.enums.converter.CarStateConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.annotations.FetchMode.JOIN;

@Entity
@Table(name = "cars")
@Getter
@Setter
@NoArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mark_id", referencedColumnName = "id")
    @Fetch(JOIN)
    private CarProducent producent;

    @Column(name = "price")
    private Long price;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", referencedColumnName = "id")
    @Fetch(JOIN)
    private Color color;

    @Column(name = "mileage")
    private Long mileage;

    @Column(name = "car_status")
    @Convert(converter = CarStateConverter.class)
    private CarState carState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petrol_type_id", referencedColumnName = "id")
    @Fetch(JOIN)
    private Petrol petrolType;

    @Column(name = "engine_capacity")
    private Integer engineCapacity;

    @Column(name = "power")
    private Integer power;

    @Column(name = "manufacture_year")
    private Integer year;

    @Column(name = "had_accidents", nullable = false)
    private Boolean hadAccidents = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_profiles_id", referencedColumnName = "id")
    @Fetch(JOIN)
    private Profile owner;

    @ManyToMany(mappedBy = "likedCars", fetch = FetchType.LAZY)
    private List<Profile> likedByProfiles = new ArrayList<>();

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(JOIN)
    private List<Photo> photos = new ArrayList<>();
}
