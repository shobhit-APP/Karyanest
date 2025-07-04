package com.example.Authentication.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="otpdata")
public class Otpdata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String phoneNumber;
    private String otp;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private ZonedDateTime expiryTime;

    public Otpdata(String phoneNumber, String otp) {
        this.phoneNumber = phoneNumber;
        this.otp = otp;
        this.expiryTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).plus(5, ChronoUnit.MINUTES);
    }

    public boolean isExpired() {
        return ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).isAfter(expiryTime);
    }
}

