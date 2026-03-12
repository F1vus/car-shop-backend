package edu.team.carshopbackend.service.email;

import edu.team.carshopbackend.error.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;


@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;

    private final String sender;

    public EmailServiceImpl(final JavaMailSender javaMailSender, @Value("${spring.mail.username}") final String sender) {
        this.javaMailSender = javaMailSender;
        this.sender = sender;
    }

    /**
     * Sends an email using JavaMail.
     *
     * @param details email details (recipient, subject, body)
     */
    @Override
    public void send(EmailDetails details) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(sender);
            helper.setTo(details.getRecipient());
            helper.setSubject(details.getSubject());
            helper.setText(details.getMsgBody() == null ? "" : details.getMsgBody(), false);

            javaMailSender.send(message);
        } catch (MessagingException | RuntimeException e) {
            throw new EmailSendingException("Exception when trying sent email!");
        }
    }
}
