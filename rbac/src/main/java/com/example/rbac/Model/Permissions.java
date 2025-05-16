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

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name ="permission")
    private String permission;

    @Column(name = "description")
    private String description;


    @OneToMany(mappedBy = "permissions", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<RolesPermission> rolePermissions;

    public Permissions(String name, String permission, String description) {
        this.name = name;
        this.permission = permission;
        this.description = description;
    }
}
