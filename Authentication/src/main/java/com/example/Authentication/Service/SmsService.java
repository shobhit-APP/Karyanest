package com.example.Authentication.Service;

import com.example.module_b.ExceptionAndExceptionHandler.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Service class for handling authentication-related operations, including OTP sending via MSG91 API.
 */
@Service
public class SmsService {

    // Initialize SLF4J Logger
    private static final Logger logger = LoggerFactory.getLogger(Auth.class);

    /**
     * Sends an OTP to the specified mobile number using the MSG91 API.
     *
     * @param mobile     The mobile number to send the OTP to (without country code).
     * @param otp        The OTP to send.
          The MSG91 authentication key.
     * @return A success message if the OTP is sent successfully.
     * @throws CustomException If there is an error sending the OTP or invalid input.
     */
    public void sendOtp(String mobile, String otp) throws CustomException {
        logger.info("Attempting to send OTP to mobile: {}", mobile);

        // Validate inputs
        String templateId="685e9f5cd6fc0531b1624bc1";
        String authKey="457930AUOcTtlnyn685ea113P1";
        validateInputs(mobile, otp, templateId, authKey);

        HttpURLConnection connection = null;
        try {
            // Construct the MSG91 API URL
            String url = String.format(
                    "https://api.msg91.com/api/v5/otp?template_id=%s&mobile=91%s&authkey=%s&otp=%s",
                    URLEncoder.encode(templateId, StandardCharsets.UTF_8),
                    URLEncoder.encode(mobile, StandardCharsets.UTF_8),
                    URLEncoder.encode(authKey, StandardCharsets.UTF_8),
                    URLEncoder.encode(otp, StandardCharsets.UTF_8)
            );

            // Set up the HTTP connection
            URL obj = new URL(url);
            connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Cache-Control", "no-cache");

            // Get the response code
            int responseCode = connection.getResponseCode();
            logger.debug("MSG91 API response code: {}", responseCode);

            if (responseCode == 200) {
                String response = readResponse(connection);
                logger.info("OTP sent successfully to mobile: {}. Response: {}", mobile, response);
            } else {
                String errorResponse = readErrorResponse(connection);
                logger.error("Failed to send OTP to mobile: {}. Response code: {}. Error: {}",
                        mobile, responseCode, errorResponse);
                throw new CustomException("Error sending OTP: " + errorResponse);
            }
        } catch (IOException e) {
            logger.error("IO error while sending OTP to mobile: {}. Error: {}", mobile, e.getMessage(), e);
            throw new CustomException("IO error while sending OTP: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while sending OTP to mobile: {}. Error: {}", mobile, e.getMessage(), e);
            throw new CustomException("Unexpected error while sending OTP: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
                logger.debug("Disconnected HTTP connection for mobile: {}", mobile);
            }
        }
    }

    /**
     * Validates the input parameters for sending an OTP.
     *
     * @param mobile     The mobile number.
     * @param otp        The OTP.
     * @param templateId The MSG91 template ID.
     * @param authKey    The MSG91 authentication key.
     * @throws CustomException If any input is invalid.
     */
    private void validateInputs(String mobile, String otp, String templateId, String authKey) throws CustomException {
        logger.debug("Validating inputs for OTP sending");
        if (mobile == null || mobile.trim().isEmpty() || !mobile.matches("\\d{10}")) {
            logger.error("Invalid mobile number: {}", mobile);
            throw new CustomException("Mobile number must be a valid 10-digit number");
        }
        if (otp == null || otp.trim().isEmpty() || !otp.matches("\\d{4,6}")) {
            logger.error("Invalid OTP: {}", otp);
            throw new CustomException("OTP must be a 4-6 digit number");
        }
        if (templateId == null || templateId.trim().isEmpty()) {
            logger.error("Template ID is null or empty");
            throw new CustomException("Template ID cannot be null or empty");
        }
        if (authKey == null || authKey.trim().isEmpty()) {
            logger.error("Auth key is null or empty");
            throw new CustomException("Auth key cannot be null or empty");
        }
    }

    /**
     * Reads the response from a successful HTTP connection.
     *
     * @param connection The HTTP connection.
     * @return The response string.
     * @throws IOException If an error occurs while reading the response.
     */
    private String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        }
    }

    /**
     * Reads the error response from a failed HTTP connection.
     *
     * @param connection The HTTP connection.
     * @return The error response string or "Unknown error" if reading fails.
     */
    private String readErrorResponse(HttpURLConnection connection) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        } catch (IOException e) {
            logger.error("Could not read error response: {}", e.getMessage(), e);
            return "Unknown error";
        }
    }
}