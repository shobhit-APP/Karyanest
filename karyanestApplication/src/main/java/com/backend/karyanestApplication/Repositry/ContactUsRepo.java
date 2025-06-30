package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.Model.ContactUs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ContactUsRepo extends JpaRepository<ContactUs,Long> {
    List<ContactUs> findByEmail(String email);
}
