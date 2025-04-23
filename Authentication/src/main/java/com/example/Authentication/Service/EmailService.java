package com.example.Authentication.Service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        mailSender.send(message);
    }
    public String sendPasswordResetEmail(String email, String resetLink, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Password Reset");

            // Corrected HTML content with token displayed separately for testing
            String htmlContent = "<p>Please click the link below to reset your password:</p>"
                    + "<p><a href='" + resetLink + "'>Reset Password</a></p>"
                    + "<p><strong>Token :</strong> " + resetToken + "</p>"
                    + "<p>If you did not request a password reset, please ignore this email.</p>";

            helper.setText(htmlContent, true);

            mailSender.send(message);
            return "Password reset email sent successfully!";
        } catch (MessagingException e) {
            return "Failed to send password reset email: " + e.getMessage();
        }
    }

    /**
     //     * Simple SMS service interface
     //     */
//    public interface SmsService {
//        /**
//         * Send an OTP via SMS
//         *
//         * @param phoneNumber The recipient phone number
//         * @param otp The one-time password
//         */
//        void sendOtp(String phoneNumber, String otp);
//    }

}
