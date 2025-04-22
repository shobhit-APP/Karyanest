package com.example.rbac.Controller;

import com.example.rbac.DTO.AssignPermissionRequestDTO;
import com.example.rbac.Model.Permissions;
import com.example.rbac.Model.Roles;
import com.example.rbac.Service.AssignPermissionsService;
import com.example.rbac.Service.PermissionsService;
import com.example.rbac.Service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for RBAC operations.
 *
 * Always Authorize to the ADMIN role
 */

@RestController
@RequestMapping("/v1/rbac/permission")
public class PermissionController {

    @Autowired
    private PermissionsService permissionsService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('create_permission')")
    public ResponseEntity<Map<String, Object>> createPermission(@RequestBody Permissions permissions) {
        Permissions createdPermissions = permissionsService.createPermission(permissions);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Routes Created Successfully", "routes", createdPermissions));
    }
}
