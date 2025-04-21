package com.example.rbac.Model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Permissions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "path", unique = true)
    private String path;

    @Column(name ="permission")
    private String permission;


    @OneToMany(mappedBy = "permissions", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<RolesPermission> rolePermissions;

    public Permissions(String name, String permission) {
        this.name = name;
        this.permission = permission;
    }
}
