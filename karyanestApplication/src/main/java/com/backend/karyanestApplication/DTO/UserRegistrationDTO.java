//package com.backend.karyanestApplication.DTO;
//
//
//import jakarta.persistence.Column;
//import jakarta.validation.constraints.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//
//import java.sql.Timestamp;
//
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class UserRegistrationDTO {
//
//    @NotNull
//    @Size(min = 1, max = 100)
//    private String fullName;;
//
//    @NotNull
//    @Email
//    private String email;
//
//    @Pattern(regexp = "^[0-9]{10,15}$")
//    private String phoneNumber;
//
//    @NotNull
//    @Size(min = 8, max = 255)
//    private String password;
//
//    private String profilePicture;
//
//    private String address;
//
//    private String city;
//
//    private String state;
//
//    private String country;
//
//    @Pattern(regexp = "^[1-9][0-9]{5}$")
//    private String pincode;
//
//    private String preferences;
//
//    @CreationTimestamp
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private Timestamp createdAt;
//
//    @UpdateTimestamp
//    @Column(name = "updated_at", nullable = false)
//    private Timestamp updatedAt;
//
//}
package com.backend.karyanestApplication.DTO;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDTO {


    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10}$", message = "Phone number should be exactly 10 digits")
    private String phoneNumber;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    private String profilePicture;
    private String address;
    private String city;
    private String state;
    private String country;
    @Pattern(regexp = "^[1-9][0-9]{5}$")
    private String pincode;
    private String preferences;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
    private String preferredVerificationMethod;

}
