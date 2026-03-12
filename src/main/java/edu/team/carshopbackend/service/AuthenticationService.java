package edu.team.carshopbackend.service;

import edu.team.carshopbackend.config.jwtConfig.JwtCore;
import edu.team.carshopbackend.dto.AuthDTO.*;
import edu.team.carshopbackend.entity.JwtToken;
import edu.team.carshopbackend.entity.Profile;
import edu.team.carshopbackend.entity.User;
import edu.team.carshopbackend.entity.enums.JwtTokenType;
import edu.team.carshopbackend.entity.impl.UserDetailsImpl;
import edu.team.carshopbackend.error.exception.ChangePasswordException;
import edu.team.carshopbackend.error.exception.NotFoundException;
import edu.team.carshopbackend.error.exception.RefreshTokenException;
import edu.team.carshopbackend.repository.JwtTokenRepository;
import edu.team.carshopbackend.service.email.EmailAsyncFacade;
import edu.team.carshopbackend.service.email.EmailService;
import edu.team.carshopbackend.service.impl.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtCore jwtCore;
    private final JwtTokenRepository jwtTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ProfileService profileService;
    private final EmailAsyncFacade emailService;
    private final EmailVerificationTokenService emailVerificationTokenService;


    @Transactional
    public AuthenticationResponseDTO authenticate(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        var user = userService.getUserById(userDetails.getId());

        String accessJwt = jwtCore.generateToken(userDetails);
        String refreshJwt = jwtCore.generateRefreshToken(userDetails);

        saveUserJwtToken(user, accessJwt);
        log.info("User logged by email: {}", authentication.getName());
        return AuthenticationResponseDTO.builder()
                .accessToken(accessJwt)
                .refreshToken(refreshJwt)
                .build();
    }

    @Transactional
    public String register(SignupDTO signupDTO) {
        User user = new User();

        user.setEmail(signupDTO.getEmail());
        user.setPassword(passwordEncoder.encode(signupDTO.getPassword()));

        User savedUser = userService.save(user);

        Profile profile = new Profile();
        profile.setUser(savedUser);
        profile.setName(signupDTO.getUsername());
        profileService.save(profile);

       var token = emailVerificationTokenService.createToken(user);

        emailService.sendAsync(EmailService.EmailDetails.builder()
                .subject("Verification system CarShop")
                .recipient(user.getEmail())
                .msgBody("Your verification token: "+token.getToken())
                .build()
        );

        log.info("Registered new user  Id: {}, Email: {}", user.getId(), user.getEmail());
        return "User registered successfully";
    }

    @Transactional
    public void resetPassword(String email) throws NotFoundException {
        //TODO
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequestDTO dto)
            throws ChangePasswordException, NotFoundException {

        User user = userService.getUserById(userId);

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new ChangePasswordException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userService.updateUser(user);
    }

    @Transactional
    public void changeEmail(Long userId, UpdateEmailRequestDTO dto) throws NotFoundException {
        User user = userService.getUserById(userId);
        user.setEmail(dto.getNewEmail());
        userService.updateUser(user);
    }

    public AuthenticationResponseDTO refreshToken(HttpServletRequest request) {
        String refreshToken = null;
        String email;

        try{
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                refreshToken = authHeader.substring(7);
                if(!jwtCore.isRefreshToken(refreshToken)){
                    throw new RefreshTokenException("Invalid refresh token");
                }
            }
            if(refreshToken != null ) {
                try{
                    email = jwtCore.getEmailFromToken(refreshToken);
                }catch (ExpiredJwtException e){
                    throw new RefreshTokenException("Expired JWT token");
                }catch (JwtException e){
                    throw new RefreshTokenException("Invalid JWT token");
                }
                if(email != null) {
                    User user = userService.getUserByEmail(email);

                    String accessJwt = jwtCore.generateToken(UserDetailsImpl.build(user));

                    saveUserJwtToken(user, accessJwt);

                    return AuthenticationResponseDTO.builder()
                            .accessToken(accessJwt)
                            .refreshToken(refreshToken)
                            .build();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void saveUserJwtToken(User user, String accessToken) {
        String jti = jwtCore.getJti(accessToken);

        var token = new JwtToken();
        token.setUser(user);
        token.setJti(jti);
        token.setTokenType(JwtTokenType.BEARER);
        token.setRevoked(false);
        token.setExpired(false);
        jwtTokenRepository.save(token);
    }
}
