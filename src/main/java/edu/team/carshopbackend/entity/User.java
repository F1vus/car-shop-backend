package edu.team.carshopbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "email", unique = true)
    private String email;

    @Column(nullable = false, name="password")
    private String password;

    @Column(nullable = false, name = "email_verified")
    private Boolean enabled = false;

    @OneToOne(mappedBy = "user")
    private Profile profile;

    @OneToMany(mappedBy = "user")
    private List<JwtToken> jwtTokens;
}
