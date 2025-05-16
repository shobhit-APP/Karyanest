//package com.backend.karyanestApplication.Model;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.util.List;
//
//@Entity
//@Table(name = "routes")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@ToString
//public class Route {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "name", nullable = false, unique = true)
//    private String name;
//
//    @Column(name = "path", nullable = false, unique = true)
//    private String path;
//
//
//    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    @ToString.Exclude
//    private List<RolePermission> rolePermissions;
//}
//
