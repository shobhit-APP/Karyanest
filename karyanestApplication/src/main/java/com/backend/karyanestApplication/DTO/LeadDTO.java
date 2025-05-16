  package com.backend.karyanestApplication.DTO;

import com.backend.karyanestApplication.Model.LeadNote;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

  @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for Lead information")
public class LeadDTO {
    @Schema(description = "Unique identifier of the lead", example = "1")
    private Long id;

    @Schema(description = "Detailed information about the lead", example = "Interested in a 3BHK apartment in East Wing")
    private String leadDetails;

    @Schema(description = "ID of the property associated with this lead", example = "1001")
    private Long propertyId;

    @Schema(description = "Full name of the lead contact", example = "Rahul Sharma")
    private String name;

    @Schema(description = "Email address of the lead", example = "rahul.sharma@example.com")
    private String email;

    @Schema(description = "Contact phone number", example = "+91 9876543210")
    private String phone;

    @Schema(description = "Additional message or inquiry from the lead", example = "Looking for property with garden access")
    private String message;

    @Schema(description = "Current status of the lead in the sales pipeline")
    private LeadStatus status;

    @Schema(description = "ID of the agent assigned to handle this lead", example = "5")
    private Long agentId;

    @Schema(description = "Source from which the lead was generated", example = "Website Contact Form")
    private String source;

    @Schema(description = "Flag indicating if the lead has been archived", example = "false")
    private boolean isArchived;

    @Schema(description = "Additional notes about the lead", example = "Client prefers evening calls after 6 PM")
    private List<LeadNoteDTO> leadNotes;
    @Schema(description = "Possible statuses for a lead in the sales pipeline")
    public enum LeadStatus {
        @Schema(description = "Newly created lead, not yet contacted")
        NEW,

        @Schema(description = "Lead has been contacted but no clear interest established yet")
        CONTACTED,

        @Schema(description = "Lead has shown interest in proceeding further")
        INTERESTED,

        @Schema(description = "Successfully converted lead into a customer")
        CLOSED_DEAL,

        @Schema(description = "Lead did not convert and is no longer being pursued")
        LOST_LEAD
    }
}