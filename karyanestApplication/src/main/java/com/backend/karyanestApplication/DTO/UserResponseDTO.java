package com.backend.karyanestApplication.DTO;

import com.backend.karyanestApplication.Model.Property;
import com.backend.karyanestApplication.Model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String profilePicture;
    private String profilePictureFileId; // Added for Backblaze B2 file ID
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private Long parent_code;
    private String referCode;
    private String preferences;

    private User.VerificationStatus verificationStatus;
    private User.VerificationMethod verificationMethod;
    private User.UserStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp lastLogin;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp registrationDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updateAt;
    private List<Long> favoritePropertyIds;
    private String userRole;
}
