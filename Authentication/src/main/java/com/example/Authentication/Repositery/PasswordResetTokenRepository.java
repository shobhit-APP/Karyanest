package com.example.Authentication.Repositery;

import com.example.Authentication.Model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
     * Password Reset Token repository interface
     */
    public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
        PasswordResetToken findByToken(String token);
    }
