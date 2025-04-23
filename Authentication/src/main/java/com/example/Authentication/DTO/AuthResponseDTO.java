package com.example.Authentication.DTO;

//import com.backend.karyanestApplication.DTO.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String jwtToken;
    private String refreshToken;
    private String role;
}













































