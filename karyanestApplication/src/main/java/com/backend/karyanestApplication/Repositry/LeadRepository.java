package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.Model.Lead;
import com.backend.karyanestApplication.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    List<Lead> findByAgentAndIsArchivedFalse(User agent); // Fetch non-archived leads by agent

    List<Lead> findByStatusAndIsArchivedFalse(Lead.LeadStatus status); // Fetch non-archived leads by status

    List<Lead> findBySourceAndIsArchivedFalse(String source); // Fetch non-archived leads by source

    int countByAgentAndIsArchivedFalse(User agent); // Count only non-archived leads for an agent

    int countByAgent(User agent);
    List<Lead> findByPropertyId(Long id);
    List<Lead> findByAdminAndIsArchivedFalse(User admin);
    List<Lead> findByIsArchivedFalse();
}
