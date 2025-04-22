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
@RestController
@RequestMapping("/v1/rbac/roles")
public class RolesController {

    @Autowired
    private RolesService roleService;

    @Autowired
    private AssignPermissionsService assignPermissionsService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('roles_get')")
    public ResponseEntity<Map<String, List<String>>> getAllRoles() {
        List<Roles> roles = roleService.getAllRoles();
        List<String> roleNames = roles.stream()
                .map(Roles::getName)
                .toList();
        return ResponseEntity.ok(Map.of("roleNames", roleNames));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('roles_create')")
    public ResponseEntity<Map<String, String>> createRole(@RequestBody Map<String, Object> requestBody) {
        Map<String, String> response = new HashMap<>();
        String roleName = (String) requestBody.get("name");
        String roleDescription = (String) requestBody.get("description");

        Optional<Roles> existingRole = roleService.findByName(roleName);
        if (existingRole.isPresent()) {
            response.put("message", "Role already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Roles role = new Roles();
        role.setName(roleName);
        role.setDescription(roleDescription);
        Roles createdRole = roleService.createRole(role);

        response.put("message", "Role created successfully");
        response.put("role name", createdRole.getName());
        response.put("role description", createdRole.getDescription());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/permission")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('assignPermissionToRole')")
    public ResponseEntity<Map<String, Object>> assignPermissionToRole(@RequestBody AssignPermissionRequestDTO request) {
        assignPermissionsService.assignPermissionToRole(request.getRoleId(), request.getPermissionIds());
        return ResponseEntity.ok(Map.of("message", "Permission assigned to role successfully"));
    }

}
