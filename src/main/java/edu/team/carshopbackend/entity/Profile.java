package edu.team.carshopbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.annotations.FetchMode.JOIN;

@Entity
@Table(name = "users_profiles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "profile_name")
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true, name = "user_id", nullable = false, referencedColumnName = "id")
    @Fetch(JOIN)
    private User user;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Car> cars = new ArrayList<>();

    @Column(name = "rating")
    private double rating = 0.0;

    @Column(name = "rating_count")
    private int ratingCount = 0;

    @Column(name = "profile_phone_number")
    private String phoneNumber;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    @PrePersist
    private void prePersist() {
        this.registrationDate = LocalDateTime.now();
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "profile_liked_cars",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "car_id")
    )
    private List<Car> likedCars = new ArrayList<>();
}
