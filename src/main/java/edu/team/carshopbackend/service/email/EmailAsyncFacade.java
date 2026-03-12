package edu.team.carshopbackend.service.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailAsyncFacade {

    private final EmailService emailService;
    private final ExecutorService virtualThreadExecutor;

    public void sendAsync(EmailService.EmailDetails details) {
        virtualThreadExecutor.submit(() -> {
            try {
                emailService.send(details);
            } catch (Exception e) {
                log.error("Async mail failed", e);
            }
        });
    }
}
