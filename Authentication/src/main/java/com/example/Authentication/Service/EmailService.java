package com.example.Authentication.Service;


import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
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
            // 🔒 Basic custom check to reject likely fake/demo/test emails
            if (email == null || !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                return "Invalid email format: " + email;
            }
            if (email.contains("demo") || email.contains("test") || email.endsWith("@example.com")) {
                return "This email looks fake or is for testing. Please use a valid email.";
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Password Reset");

            String htmlContent = "<p>Please click the link below to reset your password:</p>"
                    + "<p><a href='" + resetLink + "'>Reset Password</a></p>"
                    + "<p><strong>Token :</strong> " + resetToken + "</p>"
                    + "<p>If you did not request a password reset, please ignore this email.</p>";

            helper.setText(htmlContent, true);

            mailSender.send(message);
            return "Password reset email sent successfully!";

        } catch (MailSendException mse) {
            if (mse.getCause() instanceof SendFailedException) {
                return "Invalid email address: " + email;
            }
            return "Failed to send password reset email: " + mse.getMessage();

        } catch (MessagingException e) {
            return "Failed to send password reset email: " + e.getMessage();

        } catch (Exception ex) {
            return "Unexpected error occurred while sending email: " + ex.getMessage();
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
