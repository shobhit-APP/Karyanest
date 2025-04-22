package com.example.rbac.DTO;

import lombok.Data;

import java.util.List;

@Data
public class AssignPermissionRequestDTO {
    private Long roleId;
    private List<Long> permissionIds;
}
