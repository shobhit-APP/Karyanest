package com.example.Authentication.Repositery;

import com.example.Authentication.Model.Otpdata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;


@Repository
public interface OtpRepository extends JpaRepository<Otpdata, Long> {
    Optional<Otpdata> findByPhoneNumber(String phoneNumber);
}

