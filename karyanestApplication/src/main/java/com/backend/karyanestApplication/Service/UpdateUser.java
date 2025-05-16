package com.backend.karyanestApplication.Service;

import com.backend.karyanestApplication.Model.User;
import com.backend.karyanestApplication.Repositry.UserRepo;
import com.example.Authentication.DTO.UpdateUserInternalDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UpdateUser {
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public boolean updateUserInternally(UpdateUserInternalDTO dto) {
        User user = userRepository.findById(dto.getUserId()).orElse(null);
        if (user == null) return false;

        // Update password if provided
        if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getNewPassword())); // encode always
        }

        // Update status if provided
        if (dto.getStatus() != null) {
            user.setStatus(User.UserStatus.valueOf(dto.getStatus()));
        }

        // Update verification status
        if (dto.getVerificationStatus() != null) {
            user.setVerificationStatus(User.VerificationStatus.valueOf(dto.getVerificationStatus()));
        }

        userRepository.save(user);
        return true;
    }

}

