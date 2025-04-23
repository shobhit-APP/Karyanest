package com.backend.karyanestApplication.Service;

import com.backend.karyanestApplication.DTO.*;
import com.example.Authentication.DTO.JWTUserDTO;
import com.example.module_b.ExceptionAndExceptionHandler.CustomException;
import com.backend.karyanestApplication.Model.Lead;
import com.backend.karyanestApplication.Model.LeadNote;
import com.backend.karyanestApplication.Model.Property;
import com.backend.karyanestApplication.Model.User;
import com.backend.karyanestApplication.Repositry.LeadRepository;
import com.backend.karyanestApplication.Repositry.PropertyRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeadService {
    private static final Logger log = LoggerFactory.getLogger(LeadService.class);
    private final LeadRepository leadRepository;
    private final UserService userService;

    private final LeadNoteService leadNoteService;
    private final PropertyRepository propertyRepository;

    public LeadService(LeadRepository leadRepository, UserService userService, LeadNoteService leadNoteService, PropertyRepository propertyRepository) {
        this.leadRepository = leadRepository;
        this.userService = userService;
        this.leadNoteService = leadNoteService;
        this.propertyRepository = propertyRepository;
    }

    @Transactional
    public LeadResponseDTO createLead(LeadDTO leadDTO, HttpServletRequest request) {
        Lead lead = new Lead();
        lead.setLeadDetails(leadDTO.getLeadDetails());

        // Fetch property (Throw exception if not found)
        Property property = propertyRepository.getPropertyById(leadDTO.getPropertyId());
        JWTUserDTO user = (JWTUserDTO) request.getAttribute("user");
        lead.setProperty(property);

        lead.setName(leadDTO.getName());
        lead.setEmail(leadDTO.getEmail());
        lead.setPhone(leadDTO.getPhone());
        lead.setMessage(leadDTO.getMessage());
        lead.setSource(leadDTO.getSource());
        lead.setArchived(leadDTO.isArchived());


        if (leadDTO.getLeadNotes() != null && !leadDTO.getLeadNotes().isEmpty()) {
            if (lead.getLeadNotes() == null) {
                lead.setLeadNotes(new ArrayList<>());
            }
            for (LeadNoteDTO noteDTO : leadDTO.getLeadNotes()) {
                LeadNote leadNote = new LeadNote();
                leadNote.setNote(noteDTO.getNote());
                leadNote.setAgentId(leadDTO.getAgentId());
                leadNote.setAgentName(user.getFullname());
                leadNote.setLead(lead);// establish foreign key relation
                lead.getLeadNotes().add(leadNote);
            }
        }


        // Fetch Admin (Throw exception if not found)
        User admin = userService.findById(user.getUserId());
        lead.setAdmin(admin);
        lead.setAssignedBy(admin.getFullName());

        // Set default status
        lead.setStatus(leadDTO.getStatus() != null ? Lead.LeadStatus.valueOf(leadDTO.getStatus().name()) : Lead.LeadStatus.NEW);

//        // Auto-assign agent if not provided
//        if (leadDTO.getAgentId() == null) {
//            User autoAssignedAgent = userService.findLeastAssignedAgent();
//            if (autoAssignedAgent == null) {
//                throw new RuntimeException("No agents available for assignment.");
//            }
//            lead.setAgent(autoAssignedAgent);
//            lead.setAssignedTo(autoAssignedAgent.getFullName());
//        } else {
//            User agent = userService.findById(leadDTO.getAgentId());
//            lead.setAgent(agent);
//            lead.setAssignedTo(agent.getFullName());
//        }

        // Save lead
        Lead savedLead = leadRepository.save(lead);
        // Convert to LeadResponseDTO
        return convertToResponseDTO(savedLead);
    }
//    public LeadResponseDTO convertToResponseDTO(Lead lead, LeadNote leadNote) {
//        LeadResponseDTO responseDTO = new LeadResponseDTO();
//        responseDTO.setId(lead.getId());
//        responseDTO.setLeadDetails(lead.getLeadDetails());
//        responseDTO.setPropertyId(lead.getProperty().getId());
//        responseDTO.setName(lead.getName());
//        responseDTO.setEmail(lead.getEmail());
//        responseDTO.setPhone(lead.getPhone());
//        responseDTO.setMessage(lead.getMessage());
//        responseDTO.setAgentId(lead.getAgent() != null ? lead.getAgent().getId() : null);
//        responseDTO.setOwner_id(lead.getAdmin() != null ? lead.getAdmin().getId() : null);
//        responseDTO.setSource(lead.getSource());
//        responseDTO.setArchived(lead.isArchived());
//        responseDTO.setLeadNotesList(leadNote);
//        responseDTO.setStatus(LeadResponseDTO.LeadStatus.valueOf(lead.getStatus().name()));
//        responseDTO.setAssignedBy(lead.getAssignedBy());
//        responseDTO.setAssignedTo(userService.findById(lead.getAgent().getId()).getFullName());
//        return responseDTO;
//    }
public LeadResponseDTO convertToResponseDTO(Lead lead) {
    LeadResponseDTO responseDTO = new LeadResponseDTO();
    responseDTO.setId(lead.getId());
    responseDTO.setLeadDetails(lead.getLeadDetails());
    responseDTO.setPropertyId(lead.getProperty().getId());
    responseDTO.setName(lead.getName());
    responseDTO.setEmail(lead.getEmail());
    responseDTO.setPhone(lead.getPhone());
    responseDTO.setMessage(lead.getMessage());
    responseDTO.setAgentId(lead.getAgent() != null ? lead.getAgent().getId() : null);
    responseDTO.setOwner_id(lead.getAdmin() != null ? lead.getAdmin().getId() : null);
    responseDTO.setSource(lead.getSource());
    responseDTO.setArchived(lead.isArchived());

    // Set all notes
    List<LeadNoteResponseDTO> leadNoteResponseDTOList=leadNoteService.convertToDTOList(lead.getLeadNotes());
    responseDTO.setLeadNotesList(leadNoteResponseDTOList);
    responseDTO.setStatus(LeadResponseDTO.LeadStatus.valueOf(lead.getStatus().name()));
    responseDTO.setAssignedBy(lead.getAssignedBy());
    return responseDTO;
}
    public List<LeadResponseDTO> convertToResponseDTOList(List<Lead> leads) {
        return leads.stream()
                .map(lead -> {
                    LeadResponseDTO responseDTO = new LeadResponseDTO();
                    responseDTO.setId(lead.getId());
                    responseDTO.setLeadDetails(lead.getLeadDetails());
                    responseDTO.setPropertyId(lead.getProperty().getId());
                    responseDTO.setName(lead.getName());
                    responseDTO.setEmail(lead.getEmail());
                    responseDTO.setPhone(lead.getPhone());
                    responseDTO.setMessage(lead.getMessage());
                    responseDTO.setAgentId(lead.getAgent() != null ? lead.getAgent().getId() : null);
                    responseDTO.setOwner_id(lead.getAdmin() != null ? lead.getAdmin().getId() : null);
                    responseDTO.setSource(lead.getSource());
                    responseDTO.setArchived(lead.isArchived());
                    List<LeadNoteResponseDTO> leadNoteResponseDTOList=leadNoteService.convertToDTOList(lead.getLeadNotes());
                    responseDTO.setLeadNotesList(leadNoteResponseDTOList.isEmpty() ? null :leadNoteResponseDTOList); //All
                    responseDTO.setStatus(LeadResponseDTO.LeadStatus.valueOf(lead.getStatus().name()));
                    responseDTO.setAssignedBy(lead.getAssignedBy());
                    responseDTO.setAssignedTo(lead.getAgent() != null ?
                            userService.findById(lead.getAgent().getId()).getFullName() : null);
                    return responseDTO;
                })
                .collect(Collectors.toList());
    }
    public Lead getLeadById(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found with ID: " + id));
    }
    public List<Lead> getAllLeads() {
        return leadRepository.findByIsArchivedFalse();
    }
    @Transactional
    public Lead assignAgentToLead(Long id, String agentUsername, String adminUsername) {
        User agent = userService.getUserByUsername(agentUsername);
        User admin = userService.getUserByUsername(adminUsername);

        if (agent == null) {
            throw new RuntimeException("Agent not found");
        }
        if (admin == null || !userService.isAdmin(adminUsername)) {
            throw new RuntimeException("Invalid or unauthorized admin");
        }

        Lead lead = getLeadById(id);

        if (lead.getAgent() != null && lead.getAgent().equals(agent)) {
            throw new RuntimeException("Lead is already assigned to this agent.");
        }
        lead.setAssignedBy(adminUsername);
        lead.setAgent(agent);
        lead.setAdmin(admin);

        return leadRepository.save(lead);
    }

    @Transactional
    public Lead updateLead(Long id, Lead leadDetails) {
        Lead lead = getLeadById(id);
        lead.setName(leadDetails.getName());
        lead.setEmail(leadDetails.getEmail());
        lead.setPhone(leadDetails.getPhone());
        lead.setMessage(leadDetails.getMessage());
        lead.setStatus(leadDetails.getStatus());
        lead.setSource(leadDetails.getSource());
        lead.setAssignedBy(leadDetails.getAssignedBy());

        if (leadDetails.getAgent() != null) {
            lead.setAgent(leadDetails.getAgent());
        }
        return leadRepository.save(lead);
    }

    @Transactional
    public void archiveLead(Long id) {
        Lead lead = getLeadById(id);
        lead.setArchived(true);
        leadRepository.save(lead);
    }



    public List<Lead> getLeadsByStatus(Lead.LeadStatus status) {
        return leadRepository.findByStatusAndIsArchivedFalse(status);
    }

    public List<Lead> getLeadsBySource(String source) {
        return leadRepository.findBySourceAndIsArchivedFalse(source);
    }
    public List<Lead> getMyLeads(User user) {
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return userService.isAdmin(user.getUsername())
                ? leadRepository.findByAdminAndIsArchivedFalse(user)
                : leadRepository.findByAgentAndIsArchivedFalse(user);
    }

    public int countLeadsByAgent(String agentUsername) {
        User agent = userService.getUserByUsername(agentUsername);
        if (agent == null) {
            throw new RuntimeException("Agent not found");
        }
        return leadRepository.countByAgentAndIsArchivedFalse(agent);
    }



    public List<Lead> getLeadsByPropertyId(Long id) {
        return leadRepository.findByPropertyId(id);
    }


}