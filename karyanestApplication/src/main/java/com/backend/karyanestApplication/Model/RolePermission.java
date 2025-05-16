//package com.backend.karyanestApplication.Model;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.util.List;
//
//@Entity
//@Table(name = "role_permissions")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
//public class RolePermission {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @EqualsAndHashCode.Include
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "role_id", nullable = false)
//    @ToString.Exclude
//    private UserRole role;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "route_id", nullable = false)
//    @ToString.Exclude
//    private Route route;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "permission_id", nullable = false)
//    @ToString.Exclude
//    private Permission permission;
////
////    @ManyToOne(fetch = FetchType.LAZY)
////    @JoinColumn(name = "user_id",nullable = false)
////    @ToString.Exclude
////    private User user;
//
//}
