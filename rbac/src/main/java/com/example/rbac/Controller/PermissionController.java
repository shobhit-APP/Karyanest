package com.example.rbac.Controller;

import com.example.rbac.DTO.AssignPermissionRequestDTO;
import com.example.rbac.DTO.PermissionDTO;
import com.example.rbac.DTO.PermissionResponseDTO;
import com.example.rbac.DTO.RolePermissionResponseDTO;
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
    @Autowired
    private AssignPermissionsService assignPermissionsService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('create_permission')")
    public ResponseEntity<Map<String, Object>> createPermission(@RequestBody Permissions permissions) {
        Permissions createdPermissions = permissionsService.createPermission(permissions);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Routes Created Successfully", "routes", createdPermissions));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('view_permission')")
    @GetMapping
    public ResponseEntity<PermissionResponseDTO> getAllPermissions() {
        List<Permissions> permissionList = permissionsService.getAllPermissions(); // from DB
        List<PermissionDTO> dtoList = permissionList.stream()
                .map(p -> new PermissionDTO(
                        p.getId(),
                        p.getDescription(),
                        p.getName(),
                        p.getPermission()))
                .toList();

        return ResponseEntity.ok(new PermissionResponseDTO(dtoList));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('view_permission')")
    public ResponseEntity<Map<String, Object>> getPermissionById(@PathVariable Long id) {
        Optional<Permissions> permission = permissionsService.getPermissionById(id);
        return permission.map(permissions -> ResponseEntity.ok()
                .body(Map.of("message", "Permission Retrieved Successfully", "permission", permissions))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Permission not found with id: " + id)));
    }
    /**
     * Assigns a permission to a role. Only accessible to users with ROLE_ADMIN role and assign_permission_to_role authority.
     * @param request DTO containing roleId and permission
     * @return ResponseEntity with success message
     */
    @PostMapping("/assign_permission")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('assign_PermissionToRole')")
    public ResponseEntity<Map<String, Object>> assignPermissionToRole(@RequestBody AssignPermissionRequestDTO request) {
        assignPermissionsService.assignPermissionToRole(request.getRoleId(), request.getPermissionIds());
        return ResponseEntity.ok(Map.of("message", "Permission assigned to role successfully"));
    }
    /**
     * Assigns a permission to a role. Only accessible to users with ROLE_ADMIN role and assign_permission_to_role authority.
     * @param request DTO containing roleId and permission
     * @return ResponseEntity with success message
     *
     */
    @PutMapping("/update_permission")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('update_PermissionToRole')")
    public ResponseEntity<Map<String, Object>> replaceAllPermission(@RequestBody AssignPermissionRequestDTO request) {
        assignPermissionsService.replaceAllPermissionsForRole(request.getRoleId(), request.getPermissionIds());
        return ResponseEntity.ok(Map.of("message", "Permission assigned to role successfully"));
    }
    @GetMapping("/role_permission/{role_id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('getAllAssignPermissionByRoleId')")
    public ResponseEntity<Map<String, Object>> allAssignedPermissionsByRoleId(@PathVariable Long role_id) {
        List<RolePermissionResponseDTO> assignedPermissions = assignPermissionsService.getAllPermissionsForRoleId(role_id);

        Map<String, Object> response = new HashMap<>();
        response.put("permissions", assignedPermissions);
        response.put("count", assignedPermissions.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/role_permission")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('getAllAssignedPermission')")
    public ResponseEntity<Map<String, Object>> allAssignedPermissions() {
        List<RolePermissionResponseDTO> assignedPermissions = assignPermissionsService.getAllAssignedPermissions();

        Map<String, Object> response = new HashMap<>();
        response.put("permissions", assignedPermissions);
        response.put("count", assignedPermissions.size());

        return ResponseEntity.ok(response);
    }


}
