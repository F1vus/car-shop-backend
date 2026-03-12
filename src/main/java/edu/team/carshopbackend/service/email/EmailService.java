package edu.team.carshopbackend.service.email;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;


public interface EmailService {
    /**
     * Sends an email using provided details.
     *
     * @param emailDetails details including recipient, subject and optional body
     */
    void send(EmailDetails emailDetails);

    @Builder
    @Getter
    class EmailDetails{
        @NotNull
        private String recipient;
        private String msgBody;
        @NotNull
        private String subject;
    }
}
