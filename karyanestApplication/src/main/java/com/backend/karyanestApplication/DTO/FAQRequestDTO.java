package com.backend.karyanestApplication.DTO;

import lombok.*;
import com.backend.karyanestApplication.Model.FAQ.Status;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FAQRequestDTO {
    private String question;
    private String answer;
    private String category;
    private Status status;
}
