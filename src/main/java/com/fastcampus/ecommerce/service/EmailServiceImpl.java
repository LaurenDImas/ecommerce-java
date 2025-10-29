package com.fastcampus.ecommerce.service;

import com.fastcampus.ecommerce.common.errors.ResourceNotFoundException;
import com.fastcampus.ecommerce.entity.Order;
import com.fastcampus.ecommerce.entity.User;
import com.fastcampus.ecommerce.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final UserRepository userRepository;

    @Value("${spring.mail.from}")
    private String fromEmail;

    @Value("${spring.mail.from-name}")
    private String fromName;

    @Override
    @Transactional
    @Async
    public void sendPaymentSuccess(Order order) {
        try {
            Context context = new Context();
            context.setVariable("orderNumber", order.getAwbNumber());
            context.setVariable("paymentMethod", order.getXenditPaymentMethod());
            context.setVariable("amount", order.getTotalAmount());
            context.setVariable("paymentDate", order.getUpdatedAt());

            String htmlContent = templateEngine.process("email/payment-success", context);

            User user = userRepository.findById(order.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

            sendHtmlEmail(user.getEmail(), "Pembayaran Berhasil - Pesanan # "+ order.getAwbNumber(), htmlContent);
            log.info("Payment success email sent to: {}", user.getEmail());
        }catch (Exception e) {
            log.error("Failed to send payment success email: {}", e.getMessage());
        }
    }

    /**
     * Method helper untuk kirim email HTML
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            try {
                helper.setFrom(fromEmail, fromName);
            } catch (Exception e) {
                helper.setFrom(fromEmail);
            }

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

}
