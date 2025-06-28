package com.example.Authentication.Service;

import com.example.Authentication.DTO.UpdateUserInternalDTO;
import com.example.Authentication.DTO.UserDTO;
import com.example.Authentication.Model.Otpdata;
import com.example.Authentication.Repositery.OtpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
public class OtpService {
    // In-memory OTP storage
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    private final Map<String, String> otpStorage = new HashMap<>();
    // In-memory registration OTP storage (separate from login OTP)
    private final Map<String, String> registrationOtpStorage = new HashMap<>();
    private final OtpRepository otpRepository;
    private final UserHandleService userHandleService;

    public OtpService(OtpRepository otpRepository, UserHandleService userHandleService) {
        this.otpRepository = otpRepository;
        this.userHandleService = userHandleService;
    }

    @Transactional
    public void deleteOtpByPhoneNumber(String phoneNumber) {
        otpRepository.deleteByPhoneNumber(phoneNumber);
    }
    /**
     * Verify OTP for user registration.
     */
    public boolean verifyRegistrationOtp(String phoneNumber, String otpEntered) {
        logger.info("Verifying registration OTP for phone number: {}", phoneNumber);

        boolean isValid = verifyOtp(phoneNumber, otpEntered, registrationOtpStorage);

        if (isValid) {
            logger.info("OTP is valid for phone number: {}", phoneNumber);

            // Fetch user details
            UserDTO user = userHandleService.getUserDetails("phoneNumber", phoneNumber);
            if (user != null) {
                logger.info("User found with ID: {}. Updating verification status to 'Verified'", user.getUserId());

                UpdateUserInternalDTO updateUserInternalDTO = new UpdateUserInternalDTO();
                updateUserInternalDTO.setUserId(user.getUserId());
                updateUserInternalDTO.setVerificationStatus("Verified");

                // Update user verification status
                userHandleService.setUserDetailsInternally(updateUserInternalDTO);

                logger.info("User verification status updated successfully for user ID: {}", user.getUserId());
            } else {
                logger.warn("No user found with phone number: {}", phoneNumber);
            }
        } else {
            logger.warn("Invalid OTP entered for phone number: {}", phoneNumber);
        }

        return isValid;
    }
    /**
     * Generic method to verify OTP for both login and registration.
     *
     * @param phoneNumber the phone number to verify OTP against
     * @param otpEntered the OTP entered by the user
     * @param otpStorage the storage map where OTPs are stored
     * @return boolean indicating if OTP is valid
     */
//    public boolean verifyOtp(String phoneNumber, String otpEntered, Map<String, String> otpStorage) {
    // Check if phone number exists in the provided OTP storage
//        if (!otpStorage.containsKey(phoneNumber)) {
//            return false;
//        }
//
//        // Get stored OTP for the phone number
//        String storedOtp = otpStorage.get(phoneNumber);
//
//        // Compare entered OTP with stored OTP
//        if (storedOtp != null && storedOtp.equals(otpEntered)) {
//            // Remove OTP from storage after successful verification
//            otpStorage.remove(phoneNumber);
//            return true;
//        }
//        return false;
//    }
    public boolean verifyOtp(String phoneNumber, String otpEntered, Map<String, String> otpStorage) {
        Otpdata otpData = otpRepository.findByPhoneNumber(phoneNumber).orElse(null);
        return otpData != null && !otpData.isExpired() && otpData.getOtp().equals(otpEntered);
    }


    /**
     * Verify OTP for user login.
     */
    public boolean verifyLoginOtp(String phoneNumber, String otpEntered) {
        return verifyOtp(phoneNumber, otpEntered, otpStorage);
    }


    /**
     * Verify OTP for forget password.
     */
    public boolean verifyOtp(String phoneNumber, String otpEntered) {
        return verifyOtp(phoneNumber, otpEntered, otpStorage);
    }

    /**
     * Generate and store OTP for login and Registration
     *
     * @param phoneNumber the user's phone number
     * @return the generated OTP
     */
    @Transactional
    public String generateAndStoreOtp(String phoneNumber) {
//        Random random = new Random();
//        String otp = String.format("%06d", random.nextInt(900000) + 100000); // Generates 6-digit OTP (100000 to 999999)

        String otp = "123456"; // Hardcoded OTP

        Otpdata otpdata = new Otpdata();
        otpdata.setOtp(otp);
        otpdata.setPhoneNumber(phoneNumber);
        otpdata.setExpiryTime(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).plus(5, ChronoUnit.MINUTES));
        otpRepository.deleteByPhoneNumber(phoneNumber);
        otpRepository.save(otpdata);
        return otp;
    }
}
