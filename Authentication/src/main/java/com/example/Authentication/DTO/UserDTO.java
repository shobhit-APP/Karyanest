package com.example.Authentication.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long userId;
    private String email;
    private String Username;
    private String FullName;
    private String Password;
    private String PhoneNumber;
    private String Role;
    private Long RoleId;
    private String verificationStatus;
    private String verificationMethod;
    private String status;

}
