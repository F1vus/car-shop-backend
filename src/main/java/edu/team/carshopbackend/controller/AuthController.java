package edu.team.carshopbackend.controller;

import edu.team.carshopbackend.dto.AuthDTO.*;
import edu.team.carshopbackend.entity.User;
import edu.team.carshopbackend.entity.impl.UserDetailsImpl;
import edu.team.carshopbackend.service.AuthenticationService;
import edu.team.carshopbackend.service.EmailVerificationTokenService;
import edu.team.carshopbackend.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final EmailVerificationTokenService emailVerificationTokenService;

    /**
     * Authenticate user and return access/refresh tokens.
     *
     * @param loginDTO credentials for login
     * @return authentication response DTO
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "login of user with(email,password), and return AuthenticationResponseDTO")
    public AuthenticationResponseDTO login(@Valid @RequestBody LoginDTO loginDTO) {
        return authenticationService.authenticate(loginDTO);
    }

    /**
     * Registers a new user account.
     *
     * @param signupDTO signup information
     * @return created response with success message
     */
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "registers of new user with (username,email, and password), and return string-success")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupDTO signupDTO){
        String registerResult  = authenticationService.register(signupDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerResult);
    }

    /**
     * Verifies email using provided token and email.
     *
     * @param req verification request containing token and email
     * @return OK when verification succeeds, or bad request for expired token
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verify(@Valid @RequestBody VerifyRequestDTO req) {
        var token = emailVerificationTokenService.getToken(req.getToken(), userService.getUserByEmail(req.getEmail()));
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Expired code");
        }

        User user = token.getUser();
        user.setEnabled(true);

        userService.updateUser(user);
        emailVerificationTokenService.deleteToken(token);

        return ResponseEntity.ok("Email confirmed!");
    }

    /**
     * Requests a new verification token to be sent to the given email.
     *
     * @param req request containing email to resend token
     * @return OK when token is sent
     */
    @PostMapping("/reset-verify")
    public ResponseEntity<String> resetVerify(@Valid @RequestBody ResetVerifyRequestDTO req) {
        emailVerificationTokenService.resetVerificationToken(userService.getUserByEmail(req.getEmail()));
        return ResponseEntity.ok("New token sent");
    }

    /**
     * Refreshes access token using refresh token provided in Authorization header.
     *
     * @param request HTTP servlet request
     * @return new authentication response
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponseDTO> refresh(HttpServletRequest request) {
        AuthenticationResponseDTO dto = authenticationService.refreshToken(request);
        return ResponseEntity.ok(dto);
    }

    /**
     * Changes password for authenticated user.
     *
     * @param principal authenticated principal
     * @param dto DTO containing old and new password
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public void changePassword(@AuthenticationPrincipal UserDetailsImpl principal,
                               @Valid @RequestBody ChangePasswordRequestDTO dto)  {
        authenticationService.changePassword(principal.getId(), dto);
    }

    /**
     * Changes email for authenticated user.
     *
     * @param principal authenticated principal
     * @param dto DTO containing the new email
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-email")
    public void changeEmail(@AuthenticationPrincipal UserDetailsImpl principal,
                            @Valid @RequestBody UpdateEmailRequestDTO dto)  {
        authenticationService.changeEmail(principal.getId(), dto);
    }
}
