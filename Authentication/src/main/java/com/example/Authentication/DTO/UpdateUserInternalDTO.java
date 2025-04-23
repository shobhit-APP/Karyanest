package com.example.Authentication.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserInternalDTO {
    private Long userId;
    private String newPassword;
    private String status; // Should match UserStatus enum
    private String verificationStatus;
    private Timestamp lastLogin;

    // Should match VerificationStatus enum
}
