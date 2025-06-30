package com.backend.karyanestApplication.DTO;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContactUsRequestDTO {

    @NotBlank(message = "Contact type is required")
    private String type;

    @NotBlank(message = "Query cannot be empty")
    private String query;
}
