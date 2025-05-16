package com.backend.karyanestApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TermsAndConditionsRequestDTO {
    private String title;
    private String content;
    private String version;
    private String status;
}
