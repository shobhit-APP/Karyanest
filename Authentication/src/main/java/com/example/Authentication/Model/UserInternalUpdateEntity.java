package com.example.Authentication.Model;

import com.example.rbac.Model.Roles;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "user") // Important! Use existing table
@Data
public class UserInternalUpdateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // your original User model uses "id"
    private Long userId;

    @Column(name = "username", length = 255, unique = true)
    private String username;

    @Column(name = "fullname", length = 100)
    private String fullName;

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "phone_number", length = 15, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    private VerificationStatus verificationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_method")
    private VerificationMethod verificationMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;

    @Column(name = "last_login")
    private Timestamp lastLogin;

    // Enums must match your original User entity's enums:
    public enum VerificationStatus {
        Unverified, Verified, Rejected, Pending
    }

    public enum VerificationMethod {
        Email, Phone, Documents
    }

    public enum UserStatus {
        Active, Inactive, Deleted, Blocked
    }
}
