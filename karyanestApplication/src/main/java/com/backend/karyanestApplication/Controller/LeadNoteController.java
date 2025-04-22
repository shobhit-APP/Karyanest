package com.backend.karyanestApplication.Controller;

import com.backend.karyanestApplication.DTO.JWTUserDTO;
import com.backend.karyanestApplication.DTO.LeadNoteDTO;
import com.backend.karyanestApplication.DTO.LeadNoteResponseDTO;
import com.backend.karyanestApplication.DTO.MultipleNoteDtos;
import com.example.module_b.ExceptionAndExceptionHandler.CustomException;
import com.example.Authentication.UTIL.JwtUtil;
import com.backend.karyanestApplication.Model.Lead;
import com.backend.karyanestApplication.Model.LeadNote;
import com.backend.karyanestApplication.Service.LeadNoteService;
import com.backend.karyanestApplication.Service.LeadService;
import com.backend.karyanestApplication.Service.UserService;
import com.example.Authentication.Component.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for Lead Note operations.
 */
@RestController
@RequestMapping("/v1/lead-notes") // Base path with API prefix and version
@Tag(name = "Lead Notes", description = "Operations related to lead notes")
public class LeadNoteController {
    private static final Logger logger = LoggerFactory.getLogger(LeadsController.class);

    private final LeadNoteService leadNoteService;
    private  final LeadService leadService;
    @Autowired
    public LeadNoteController(LeadNoteService leadNoteService, LeadService leadService) {
        this.leadNoteService = leadNoteService;
        this.leadService = leadService;
    }

    @Operation(summary = "Get notes by lead ID", description = "Retrieve all notes for a specific lead")
    @GetMapping("/note/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_AGENT') or hasAuthority('leadNote_getNoteByLeadID')")
    public ResponseEntity<List<LeadNoteResponseDTO>> getNotesByLeadId(@PathVariable("leadId") Long leadId) {
        try {
            List<LeadNote> notes = leadNoteService.getAllNotesByleadId(leadId);
            if (notes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(leadNoteService.convertToDTOList(notes));
        } catch (Exception e) {
            throw new CustomException("Error retrieving notes for lead ID: " + leadId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get lead note by note ID", description = "Retrieve a specific lead note by its ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_AGENT') or hasAuthority('leadNote_getLeadNoteByID)")
    public ResponseEntity<LeadNoteResponseDTO> getLeadNoteById(@PathVariable("id") Long noteId) {
        try {
            Optional<LeadNote> note = leadNoteService.getLeadNoteById(noteId);
            return note.map(n -> ResponseEntity.ok(leadNoteService.convertToDTO(n)))
                    .orElseThrow(() -> new CustomException(
                            "Lead note not found with ID: " + noteId, HttpStatus.NOT_FOUND));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Error retrieving lead note: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_AGENT') or hasAuthority('leadNote_add')")
    public ResponseEntity<?> addLeadNote(
            @PathVariable Long id,
            @Valid @RequestBody MultipleNoteDtos multipleNoteDtos,
            HttpServletRequest request) {
        try {
            JWTUserDTO user = (JWTUserDTO) request.getAttribute("user");
            if (user == null) {
                throw new CustomException("User not found", HttpStatus.NOT_FOUND);
            }

            // Validate lead information
            if (multipleNoteDtos == null) {
                throw new CustomException("Lead note cannot be null", HttpStatus.BAD_REQUEST);
            }

            // Use leadId from path variable
            Lead lead = leadService.getLeadById(id);
            if (lead == null) {
                throw new CustomException("Lead not found with ID: " + id, HttpStatus.NOT_FOUND);
            }

            // Check if the leadNoteDTO contains multiple notes or just one
            if (multipleNoteDtos.getNotes() != null && !multipleNoteDtos.getNotes().isEmpty()) {
                // Handle multiple notes
                List<LeadNote> savedNotes = leadNoteService.addMultipleNotes(multipleNoteDtos.getNotes(), lead, user);

                List<LeadNoteResponseDTO> responseDTOs = leadNoteService.convertToDTOList(savedNotes);

                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(responseDTOs);
            } else {
                // Handle single note
                LeadNote savedNote = leadNoteService.addLeadNote(multipleNoteDtos.getNote(), lead, user);
                LeadNoteResponseDTO responseDTO = leadNoteService.convertToDTO(savedNote);

                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(responseDTO);
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while creating lead note", e);
            throw new CustomException("Error creating lead note: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Operation(summary = "Update lead note", description = "Update an existing lead note by its ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_AGENT') or hasAuthority('leadNote_update')")
    public ResponseEntity<LeadNoteResponseDTO> updateLeadNote(
            @PathVariable("id") Long id,
            @Valid @RequestBody LeadNoteDTO leadNoteDTO) {
        try {
            // Check if the note exists
            Optional<LeadNote> existingNote = leadNoteService.getLeadNoteById(id);
            if (existingNote.isEmpty()) {
                throw new CustomException("Lead note not found with ID: " + id, HttpStatus.NOT_FOUND);
            }

            // Get the existing note data
            LeadNote existingLeadNote = existingNote.get();
            // Update only the note content, keeping the existing leadId and userId
            LeadNote updatedNote = leadNoteService.updateLeadNoteContent(
                    id,
                    leadNoteDTO.getNote(),
                    existingLeadNote.getLead().getId()
            );

            return ResponseEntity.ok(leadNoteService.convertToDTO(updatedNote));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Error updating lead note: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Operation(summary = "Delete lead note", description = "Delete a lead note by its ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("(hasRole('ROLE_ADMIN') or hasAuthority('leadNote_delete)")
    public ResponseEntity<String> deleteLeadNote(@PathVariable("id") Long noteId) {
        try {
            // Check if the note exists
            Optional<LeadNote> existingNote = leadNoteService.getLeadNoteById(noteId);
            if (existingNote.isEmpty()) {
                throw new CustomException("Lead note not found with ID: " + noteId,  HttpStatus.NOT_FOUND);
            }

            leadNoteService.deleteLeadNote(noteId);
            return ResponseEntity.ok("Message deleted successfully");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Error deleting lead note: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}