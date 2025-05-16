package com.backend.karyanestApplication.Verification;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequest {

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "verification_status", nullable = false)
    private String verificationStatus;

    @Column(name = "verification_method", nullable = false)
    private String verificationMethod;
}
