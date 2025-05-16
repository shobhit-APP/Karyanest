package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.Model.LeadNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadNoteRepository extends JpaRepository<LeadNote, Long> {
    LeadNote findByleadId(Long id);
    List<LeadNote> findAllByleadId(Long id);
}
