package com.example.rbac.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionResponseDTO { 
    private Long id;
    private String name;
    private String permission;
    private String description;
    private  Long permissionId;
    private Long roleId;
    private String roleName;
}
