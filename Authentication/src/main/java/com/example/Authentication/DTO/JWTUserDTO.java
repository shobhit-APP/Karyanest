package com.example.Authentication.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class
JWTUserDTO {
    private Long userId;
    private String username;
    private String userRole;
    private String fullname;
}
