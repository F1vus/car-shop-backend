package edu.team.carshopbackend.dto.AuthDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDTO {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
