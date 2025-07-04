package com.backend.karyanestApplication.Model;

import com.example.rbac.Model.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "username", length = 255, unique = true)
    private String username;

    @Size(min = 1, max = 100)
    @Column(name = "fullname", length = 100)
    private String fullName;

    @Email
    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^[0-9]{10,15}$")
    @Column(name = "phone_number", length = 15, unique = true)
    private String phoneNumber;

    @Size(min = 8, max = 255)
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "profile_picture", length = 255)
    private String profilePicture;

    @Column(name = "profile_picture_file_id", length = 255)
    private String profilePictureFileId;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "country", length = 100)
    private String country;

    @Pattern(regexp = "^[1-9][0-9]{5}$")
    @Column(name = "pincode", length = 10)
    private String pincode;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    private VerificationStatus verificationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_method")
    private VerificationMethod verificationMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @Column(name = "last_login")
    private ZonedDateTime lastLogin;

    @Column(name = "registration_date", nullable = false, updatable = false)
    private ZonedDateTime registrationDate;

    @Column(name = "preferences", columnDefinition = "JSON")
    private String preferences;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Property> properties;

    @Column(name = "parent_code")
    private Long parentCode;

    @Column(name = "refer_code")
    private String referCode;
    @PrePersist
    protected void onCreate() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        System.out.println("PrePersist setting timestamps to: " + now);
        this.registrationDate = now;
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

    public User(String fullName, String phoneNumber, String profilePicture, String address, String city,
                String state, String country, String pincode, String preferences) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.profilePicture = profilePicture;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
        this.preferences = preferences;
    }

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