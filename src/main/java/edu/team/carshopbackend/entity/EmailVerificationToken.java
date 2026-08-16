package edu.team.carshopbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

import java.time.LocalDateTime;

import static org.hibernate.annotations.FetchMode.JOIN;

@Entity
@Table(name = "email_verification_token")
@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(unique = true, name = "user_id", nullable = false, referencedColumnName = "id")
    @Fetch(JOIN)
    private User user;

    @Column(nullable = false, name = "token")
    private String token;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "expires_at")
    private LocalDateTime expiresAt;

    @PrePersist
    private void init(){
        createdAt = LocalDateTime.now();
    }
}
