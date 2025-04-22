package com.backend.karyanestApplication.Controller;

import com.backend.karyanestApplication.DTO.*;
import com.example.module_b.ExceptionAndExceptionHandler.CustomException;
import com.example.Authentication.UTIL.JwtUtil;
import com.backend.karyanestApplication.Model.Lead;
import com.backend.karyanestApplication.Model.User;
import com.backend.karyanestApplication.Service.LeadNoteService;
import com.backend.karyanestApplication.Service.LeadService;
import com.backend.karyanestApplication.Service.UserService;
import com.example.Authentication.Component.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/leads")
@Tag(name = "Lead Management", description = "APIs for managing leads in the Karyanest application")
public class LeadsController {

    @Autowired
    private LeadService leadService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserContext userContext;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LeadNoteService leadNoteService;
    private static final Logger logger = LoggerFactory.getLogger(LeadsController.class);

    @Operation(
            summary = "Create a new lead",
            description = "Creates a new lead record with the provided details. Only accessible by administrators."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Lead created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LeadDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or request processing failed",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Unauthorized access - Admin role required",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/create")
    @PreAuthorize("(hasRole('ROLE_ADMIN') or hasAuthority('leads_create')")
    public ResponseEntity<?> createLead(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Lead information to create a new record",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LeadDTO.class))
            )
            @RequestBody LeadDTO leadDTO,
            HttpServletRequest request) {
        try {
            LeadResponseDTO createdLead = leadService.createLead(leadDTO, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLead);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating lead: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Assign agent to lead",
            description = "Assigns a specific agent to handle a lead. Only accessible by administrators."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Agent assigned successfully",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters or processing failed",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Unauthorized access - Admin role required",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lead or agent not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("{id}")
    @PreAuthorize("(hasRole('ROLE_ADMIN') or hasAuthority('lead_assign')")
    public ResponseEntity<?> assignAgent(
            @Parameter(description = "ID of the lead to assign") @PathVariable Long id,
            @Parameter(description = "Username of the agent to be assigned") @RequestParam String agentUsername,
            HttpServletRequest request) {
        // Extract username from JWT token for security
        String token = userContext.extractToken(request);
        String username = jwtUtil.extractUsername(token);
        try {
            Lead updatedLead = leadService.assignAgentToLead(id, agentUsername, username);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Agent assigned successfully");
            response.put("leadId", String.valueOf(updatedLead.getId()));
            response.put("agent", updatedLead.getAgent().getUsername());
            response.put("owner", updatedLead.getAdmin().getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error assigning agent: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Get lead by ID",
            description = "Retrieves detailed information about a specific lead by its ID. Accessible by admins and the assigned agent."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lead details retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LeadDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Unauthorized access - Admin or assigned Agent role required",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lead not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("(hasRole('ROLE_ADMIN') or hasAuthority('lead_getLeadById')")
    public ResponseEntity<?> getLeadById(@Parameter(description = "ID of the lead to retrieve") @PathVariable Long id) {
        try {
            Lead lead = leadService.getLeadById(id);

//            // Fetch Lead Notes and handle empty case
//            List<LeadNote> leadNotes = leadNoteService.getNotesByleadId(id);
//            if(leadNotes==null)
//            {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NOT Found ANY NOTES.");
//            }
//           List<LeadNoteResponseDTO> leadNoteResponseDTOList=leadNoteService.convertToDTOList(leadNotes);
            LeadResponseDTO leadResponseDTO = leadService.convertToResponseDTO(lead);
            return ResponseEntity.ok(leadResponseDTO);

        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


    @Operation(
            summary = "Fetch leads assigned by or to the user",
            description = "Returns all leads where the current user is either the owner (assigner - Admin or Agent) or the agent (assignee - who received the lead)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Leads retrieved successfully",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Unauthorized access - Admin role required",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Admin user or assigned leads not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/myLeads")
    @PreAuthorize("(hasRole('ROLE_ADMIN') or hasAuthority('lead_getMyLead')")
    public ResponseEntity<?> getMyLeads(HttpServletRequest request) {
        try {
            // Extract current user from JWT
            JWTUserDTO currentUser = (JWTUserDTO) request.getAttribute("user");
            System.out.println(currentUser.getUserId() + currentUser.getUsername());
            // Get user entity from ID in token
            User loggedInUser = userService.findById(currentUser.getUserId());
            if (loggedInUser == null) {
                throw new CustomException("User not found with ID: " + currentUser.getUserId());
            }

            // Fetch leads assigned to this user (as the 'owner')
            List<Lead> leads = leadService.getMyLeads(loggedInUser);

            if (leads.isEmpty()) {
                throw new CustomException("No leads assigned to you.");
            }

            return ResponseEntity.ok(leadService.convertToResponseDTOList(leads));

        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Something went wrong."));
        }
    }



    @Operation(
            summary = "Get all leads",
            description = "Retrieves all leads from the database. Only accessible by administrators."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All leads retrieved successfully",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No leads found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Unauthorized access - Admin role required",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping
    @PreAuthorize("(hasRole('ROLE_ADMIN') or hasAuthority('leads_getAll')")
    public ResponseEntity<?> getAllLeads() {
        try {
            // Fetch all leads from the database
            List<Lead> leads = leadService.getAllLeads();
            if (leads.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Collections.singletonMap("message", "No leads found."));
            }
            return ResponseEntity.ok(leadService.convertToResponseDTOList(leads));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred."+e.getMessage()));
        }
    }

    @Operation(
            summary = "Update lead",
            description = "Updates an existing lead with new information. Only accessible by administrators."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lead updated successfully",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Unauthorized access - Admin role required",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lead not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PutMapping("{id}")
    @PreAuthorize("(hasRole('ROLE_ADMIN') or hasAuthority('lead_Update')")
    public ResponseEntity<?> updateLead(
            @Parameter(description = "ID of the lead to update") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated lead information",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Lead.class))
            )
            @RequestBody Lead updatedLead) {
        try {
            Lead lead = leadService.updateLead(id, updatedLead);
            return ResponseEntity.ok(Collections.singletonMap("message", "Lead updated successfully"));
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Lead not found with ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An error occurred while updating the lead."));
        }
    }

    @Operation(
            summary = "Delete lead",
            description = "Soft deletes (archives) a lead by its ID. Only accessible by administrators."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lead archived successfully",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Unauthorized access - Admin role required",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lead not found",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json")
            )
    })
    @DeleteMapping("{id}")
    @PreAuthorize("(hasRole('ROLE_ADMIN') or hasAuthority('lead_delete')")
    public ResponseEntity<?> deleteLead(@Parameter(description = "ID of the lead to archive") @PathVariable Long id) {
        try {
            leadService.archiveLead(id); // Using a method name that better represents soft delete
            return ResponseEntity.ok(Collections.singletonMap("message", "Lead archived successfully."));
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Lead not found with ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred while deleting the lead."));
        }
    }

    @GetMapping("/getLeadsByPropertyId/{id}")
    @PreAuthorize("(hasRole('ROLE_ADMIN') or hasAuthority('lead_getByPropertyId')")
    public ResponseEntity<?> getLeadsByPropertyId(
            @Parameter(description = "Property ID to fetch leads") @PathVariable Long id) {
        try {
            logger.info("Fetching leads for property ID: {}", id);

            List<Lead> leads = leadService.getLeadsByPropertyId(id);
            if (leads.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Collections.singletonMap("message", "No leads found."));
            }
            return ResponseEntity.ok(leadService.convertToResponseDTOList(leads));
        } catch (Exception e) {
            logger.error("Error fetching leads for property ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred."));
        }
    }
}