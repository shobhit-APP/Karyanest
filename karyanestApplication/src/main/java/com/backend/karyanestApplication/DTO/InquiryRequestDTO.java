package com.backend.karyanestApplication.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryRequestDTO {
    private Long userId;
    private Long propertyId;
    private String inquiryMessage;
}
