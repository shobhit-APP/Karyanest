package com.backend.karyanestApplication.DTO;

import com.backend.karyanestApplication.Model.Inquiry;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryResponseDTO {
    private Long id;
    private Long userId;
    private Long propertyId;
    private String inquiryMessage;
    private Inquiry.InquiryStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
