package edu.team.carshopbackend.entity;

import edu.team.carshopbackend.entity.enums.JwtTokenType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

import static org.hibernate.annotations.FetchMode.JOIN;


@Entity
@Table(name = "jwt_tokens")
@Getter
@Setter
@NoArgsConstructor
public class JwtToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true,name = "jti")
    private String jti;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "token_type")
    private JwtTokenType tokenType;

    @Column(nullable = false, name = "expired")
    private boolean expired;

    @Column(nullable = false, name = "revoked")
    private boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @Fetch(JOIN)
    private User user;
}
