package edu.team.carshopbackend.service;

import edu.team.carshopbackend.entity.EmailVerificationToken;
import edu.team.carshopbackend.entity.User;
import edu.team.carshopbackend.error.exception.NotFoundException;
import edu.team.carshopbackend.repository.TokenRepository;
import edu.team.carshopbackend.service.email.EmailAsyncFacade;
import edu.team.carshopbackend.service.email.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class EmailVerificationTokenService {

    private final TokenRepository tokenRepository;
    private final EmailAsyncFacade emailService;

    @Transactional
    public EmailVerificationToken getToken(String token, User user) throws NotFoundException {
        return tokenRepository
                .findByUserAndToken(user, token)
                .orElseThrow(() -> new NotFoundException("Invalid code"));
    }

    public EmailVerificationToken createToken(User user) throws NotFoundException {
        EmailVerificationToken token =
                tokenRepository.findByUser(user).orElse(new EmailVerificationToken());

        token.setUser(user);
        token.setToken(generateToken());
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        return tokenRepository.save(token);
    }

    public void resetVerificationToken(User user) throws IllegalStateException{
        EmailVerificationToken token = getTokenByUser(user);

        if (token != null) {
            if (LocalDateTime.now().isBefore(token.getCreatedAt().plusSeconds(15))) {
                throw new IllegalStateException("Wait 15 seconds before resetting token");
            }
        }

        EmailVerificationToken newToken = createToken(user);
        emailService.sendAsync(EmailService.EmailDetails.builder()
                .subject("Verification system CarShop")
                .recipient(user.getEmail())
                .msgBody("Your new verification token: "+newToken.getToken())
                .build()
        );
    }

    public void deleteToken(EmailVerificationToken token) {
        tokenRepository.deleteById(token.getId());
    }

    public EmailVerificationToken getTokenByUser(User user) throws NotFoundException {
        return tokenRepository.findByUser(user).orElseThrow(() -> new NotFoundException("Invalid code"));
    }

    private String generateToken() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
    }
}
