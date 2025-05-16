//package com.backend.karyanestApplication.Model;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.util.List;
//
//@Entity
//@Table(name = "routes_action")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class Permission {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "name", nullable = false)
//    private String name;
//
//    @ManyToOne
//    @JoinColumn(name = "routes_id", nullable = false)
//    private Route route;
//
//    @Column(name = "description")
//    private String description;
//
//    @OneToMany(mappedBy = "permission")
//    private List<RolePermission> rolePermissions;
//
//
//}
