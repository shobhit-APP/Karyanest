package com.backend.karyanestApplication.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequest {
    private String type;      // Type of conversation (e.g., GENERAL, PROPERTY_INQUIRY)
    private Long propertyId;
    private Long propertyOwnerId;// Required if type = PROPERTY_INQUIRY
}
