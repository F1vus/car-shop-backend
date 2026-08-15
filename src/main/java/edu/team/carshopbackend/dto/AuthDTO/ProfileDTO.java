package edu.team.carshopbackend.dto.AuthDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {

    @Schema(description = "ID profilu")
    private Long id;

    @Schema(description = "Nazwa profilu")
    @Size(max = 100)
    private String name;

    @Schema(description = "Telefon użytkownika")
    @Pattern(regexp = "\\+?[0-9\\- ]{7,20}")
    private String phoneNumber;

    @Schema(description = "Email użytkownika")
    @Email
    private String email;

    @Schema(description = "URL zdjęcia profilowego")
    private String profileImage;

    @Schema(description = "Data rejestracji profilu")
    private LocalDateTime registrationDate;
}
