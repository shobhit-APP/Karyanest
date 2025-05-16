package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.Model.TermsAndConditions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TermsAndConditionsRepository extends JpaRepository<TermsAndConditions, Long> {
    Optional<TermsAndConditions> findByVersion(String version);
}