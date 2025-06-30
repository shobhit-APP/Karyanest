package com.backend.karyanestApplication.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity(name="contact_us")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactUs {

  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  String fullName;

    @Enumerated(EnumType.STRING)
    private ContactType type;

    private String email;

    @Column(columnDefinition = "TEXT")
    private String query;

    private ZonedDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
    }
    public enum ContactType {
        PROPERTY_INQUIRY,    // Questions about listed properties
        SELLER_SUPPORT,      // Help for sellers listing their properties
        BUYER_SUPPORT,       // Help for buyers searching properties
        AGENT_SUPPORT,       // Help for real estate agents
        PAYMENT_ISSUE,       // Payment-related queries
        TECHNICAL_ISSUE,     // Bug or error reporting
        GENERAL_FEEDBACK,    // Suggestions or feedback
        OTHER                // Anything that doesn't fit the above
    }
}
