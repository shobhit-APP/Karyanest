package com.backend.karyanestApplication.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ChatResponseDTO {

    private Long userId;
    private String profileImageUrl;

}

