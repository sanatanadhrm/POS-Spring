package com.example.cashier.infrastructures.notification.email;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.example.cashier.applications.dto.notification.NotificationPayload;
import com.example.cashier.applications.port.output.NotificationChannelSender;
import com.example.cashier.commons.properties.SecretProperties;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component

public class EmailNotificationGateway implements NotificationChannelSender {

    private final Executor notificationExecutor;
    private final JavaMailSender mailSender;
    private final SecretProperties secretProperties;

    public EmailNotificationGateway(@Qualifier("notificationExecutor") Executor notificationExecutor,
            JavaMailSender mailSender,
            SecretProperties secretProperties) {
        this.notificationExecutor = notificationExecutor;
        this.mailSender = mailSender;
        this.secretProperties = secretProperties;
    }

    @Override
    public boolean isApplicable(NotificationPayload<?> payload) {
        return payload.getRecipientEmail() != null && !payload.getRecipientEmail().isBlank();
    }

    @Override
    public CompletableFuture<Void> sendAsync(NotificationPayload<?> payload) {
        return CompletableFuture.runAsync(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

                helper.setFrom(secretProperties.getEmail(), "Warung Kasir");
                helper.setTo(payload.getRecipientEmail());
                helper.setSubject(payload.getSubject());
                String htmlContent = """
                        <h3>Halo %s,</h3>
                        <p>Terima kasih telah berbelanja di toko kami!</p>
                        <p>Struk pembayaran digital Anda telah terlampir pada email ini.</p>
                        <br/>
                        <p>Salam hangat,<br/><b>Tim Kasir Warung</b></p>
                        """.formatted(payload.getRecipientName());
                helper.setText(htmlContent, true);

                if (payload.getAttachment() != null && payload.getAttachment().length > 0) {
                    String fileName = payload.getAttachmentFileName() != null
                            ? payload.getAttachmentFileName()
                            : "receipt.pdf";
                    helper.addAttachment(fileName,
                            new ByteArrayResource(payload.getAttachment()));
                }
                mailSender.send(message);
                log.info("[EMAIL GATEWAY] Email berhasil dikirim ke {}", payload.getRecipientEmail());
            } catch (Exception e) {
                log.error("[EMAIL GATEWAY] Gagal mengirim email ke {}: {}",
                        payload.getRecipientEmail(), e.getMessage(), e);
                throw new RuntimeException("Gagal mengirim email: " + e.getMessage(), e);
            }
        }, this.notificationExecutor);
    }

}
