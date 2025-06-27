package com.example.Authentication.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserInternalDTO {
    private Long userId;
    private String newPassword;
    private String status; // Should match UserStatus enum
    private String verificationStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    private ZonedDateTime lastLogin;

    // Should match VerificationStatus enum
}
