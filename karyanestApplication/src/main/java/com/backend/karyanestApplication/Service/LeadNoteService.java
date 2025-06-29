package com.backend.karyanestApplication.Service;

import com.example.Authentication.DTO.JWTUserDTO;
import com.backend.karyanestApplication.DTO.LeadNoteResponseDTO;
import com.example.module_b.ExceptionAndExceptionHandler.CustomException;
import com.backend.karyanestApplication.Model.Lead;
import com.backend.karyanestApplication.Model.LeadNote;
import com.backend.karyanestApplication.Repositry.LeadNoteRepository;
import com.backend.karyanestApplication.Repositry.LeadRepository;
import com.backend.karyanestApplication.Repositry.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeadNoteService {

    @Autowired
    private LeadNoteRepository leadNoteRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private LeadRepository leadRepository;

    public List<LeadNote> getAllNotesByleadId(Long Id) {

        return leadNoteRepository.findAllByleadId(Id);
    }

    public LeadNote addLeadNote(LeadNote leadNote, Lead lead ,JWTUserDTO user) {
        leadNote.setLead(lead);
        leadNote.setNoteAddedBy(user.getFullname());
        leadNote.setNoteAddedById(user.getUserId());
        return leadNoteRepository.save(leadNote);
    }

    public Optional<LeadNote> getLeadNoteById(Long noteId) {
        return leadNoteRepository.findById(noteId);
    }

    public LeadNote getNotesByleadId(Long Id) {
        return leadNoteRepository.findByleadId(Id);
    }
    /**
     * Convert LeadNote entity to LeadNoteResponseDTO
     *
     * @param leadNote The LeadNote entity
     * @return LeadNoteResponseDTO
     */
    public LeadNoteResponseDTO convertToDTO(LeadNote leadNote) {
        LeadNoteResponseDTO dto = new LeadNoteResponseDTO();
        dto.setNoteId(leadNote.getNoteId());
        dto.setLeadId(leadNote.getLead().getId());// Assuming Lead has a getName() method
        dto.setNote(leadNote.getNote());
        dto.setNoteadded_by_id(leadNote.getNoteAddedById() == null ? null : leadNote.getNoteAddedById());
        dto.setNoteadded_by(leadNote.getNoteAddedBy()== null ? null : leadNote.getNoteAddedBy());
        return dto;
    }

    /**
     * Convert a list of LeadNote entities to DTOs
     *
     * @param leadNotes List of LeadNote entities
     * @return List of LeadNoteResponseDTOs
     */
    public List<LeadNoteResponseDTO> convertToDTOList(List<LeadNote> leadNotes) {
        return leadNotes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteLeadNote(Long noteId) {
        leadNoteRepository.deleteById(noteId);
    }

    public LeadNote updateLeadNoteContent(Long noteId, String noteContent, Long leadId) {
        // Get the existing entities
        LeadNote leadNote = getLeadNoteById(noteId)
                .orElseThrow(() -> new CustomException("Lead note not found", HttpStatus.NOT_FOUND));

        // Instead of using leadService, use repository directly
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new CustomException("Lead not found", HttpStatus.NOT_FOUND));

        // Update only the note content
        leadNote.setNote(noteContent);

        // Ensure the lead and user remain the same
        leadNote.setLead(lead);

        return leadNoteRepository.save(leadNote);
    }

    private void validateUserPermissionForLead(Lead lead, JWTUserDTO user) {
        if (!(lead.getAdmin().getId().equals(user.getUserId()) || lead.getAgent().getId().equals(user.getUserId()))) {
            throw new CustomException("Sorry you can't add lead notes because you are not part of this lead", HttpStatus.FORBIDDEN);
        }
    }

    public LeadNote addLeadNote(String noteText, Lead lead, JWTUserDTO user) {
        validateUserPermissionForLead(lead, user);

        LeadNote leadNote = new LeadNote();
        leadNote.setNote(noteText);
        leadNote.setLead(lead);
        leadNote.setNoteAddedBy(user.getFullname());
        leadNote.setNoteAddedById(user.getUserId());
        return leadNoteRepository.save(leadNote);
    }

    // For multiple notes
    public List<LeadNote> addMultipleNotes(List<String> notes, Lead lead, JWTUserDTO user) {
        validateUserPermissionForLead(lead, user);

        List<LeadNote> savedNotes = new ArrayList<>();
        for (String note : notes) {
            LeadNote leadNote = new LeadNote();
            leadNote.setNote(note);
            leadNote.setLead(lead);
            leadNote.setNoteAddedBy(user.getFullname());
            leadNote.setNoteAddedById(user.getUserId());
            savedNotes.add(leadNoteRepository.save(leadNote));
        }
        return savedNotes;
    }

}

