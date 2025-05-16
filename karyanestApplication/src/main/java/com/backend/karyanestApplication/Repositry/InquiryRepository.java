package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.Model.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByUserId(Long userId);
    List<Inquiry> findByPropertyId(Long propertyId);
}
