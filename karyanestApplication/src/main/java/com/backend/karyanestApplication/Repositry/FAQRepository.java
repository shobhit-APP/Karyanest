package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.Model.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {
}
