//package com.backend.karyanestApplication.Model;
//
//import com.example.rbac.Model.RolesPermission;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.List;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "roles")
//public class UserRole {
//
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
//        private Long id;
//
//        @Column(name = "name", nullable = false, unique = true)
//        private String name;
//
//        @Column(name = "description")
//        private String description;
//
//        @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//        private List<RolesPermission> rolePermissions;
//}
