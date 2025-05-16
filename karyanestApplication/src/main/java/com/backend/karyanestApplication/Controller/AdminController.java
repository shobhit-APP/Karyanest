package com.backend.karyanestApplication.Controller;

import com.example.module_b.ExceptionAndExceptionHandler.CustomException;
//import com.backend.karyanestApplication.Model.Permission;
//import com.backend.karyanestApplication.Model.Route;
//import com.backend.karyanestApplication.Model.User;
//import com.backend.karyanestApplication.Model.UserRole;
import com.backend.karyanestApplication.Service.*;
//import com.example.rbac.Model.Roles;
//import com.example.rbac.Service.RolesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for Admin operations.
 */
@RestController
@RequestMapping("/v1/admin")
@Tag(name = "Admin", description = "Admin operations")
public class AdminController {

    @Autowired
    private UserService userService;
    //    @Autowired
//    private RolesService roleService;
//    private final PermissionService permissionService;
//    private final RouteService routeService;
//    private final AssignPermissionService assignPermissionService;

//    public AdminController(
//            RolesService roleService,
////            PermissionService permissionService,
//            UserService userService
////            RouteService routeService,
////             AssignPermissionService assignPermissionService
//    ) {
//        this.roleService = roleService;
////        this.permissionService = permissionService;
//        this.userService = userService;
////        this.routeService = routeService;
////        this.assignPermissionService = assignPermissionService;
//    }

    /**
     * Update a user's role.
     *
     * @param request Map containing username and new role ID
     * @return ResponseEntity with update message
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasAuthority('userRole_update')")
    @Operation(summary = "Update user role", description = "Update the role of an existing user")
    @PatchMapping("/update_user_role")
    public ResponseEntity<Map<String, String>> updateUserRole(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        Long newRoleId;

        try {
            newRoleId = Long.parseLong(request.get("role_id"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid role ID format"));
        }

        try {
            userService.updateRoleId(newRoleId, username);
            return ResponseEntity.ok(Map.of(
                    "message", "User role updated successfully",
                    "username", username,
                    "new_role_id", String.valueOf(newRoleId)
            ));
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }


//    /**
//     * Create a new role.
//     *
//     * @param requestBody Map containing role details
//     * @return ResponseEntity with creation message and role details
//     */
//    @Operation(summary = "Create a new role", description = "Creates a new role with the provided details")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Role created successfully"),
//            @ApiResponse(responseCode = "400", description = "Role already exists")
//    })
//    @PostMapping("/create_roles")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<Map<String, String>> createRole(@RequestBody Map<String, Object> requestBody) {
//        Map<String, String> response = new HashMap<>();
//
//        String roleName = (String) requestBody.get("name");
//        String roleDescription = (String) requestBody.get("description");
//
//        // Check if role already exists
//        Optional<Roles> existingRole = roleService.findByName(roleName);
//        if (existingRole.isPresent()) {
//            response.put("message", "Role already exists");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//
//        // Create new role
//        Roles role = new Roles();
//        role.setName(roleName);
//        role.setDescription(roleDescription);
//
//        Roles createdRole = roleService.createRole(role);
//        response.put("message", "Role created successfully");
//        response.put("role name", createdRole.getName());
//        response.put("role description", createdRole.getDescription());
//
//        return ResponseEntity.ok(response);
//    }

//    /**
//     * Get all roles.
//     *
//     * @return ResponseEntity with a list of all role names
//     */
//    @Operation(summary = "Get all roles", description = "Retrieves a list of all roles")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Roles retrieved successfully")
//    })
//    @GetMapping("/get_roles")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<Map<String, List<String>>> getAllRoles() {
//        List<Roles> roles = roleService.getAllRoles();
//
//        List<String> roleNames = roles.stream()
//                .map(Roles::getName)
//                .toList();
//
//        return ResponseEntity.ok(Map.of("roleNames", roleNames));
//    }
//
//    /**
//     * Create a new permission.
//     *
//     * @param requestBody Map containing permission details
//     * @return ResponseEntity with creation message and permission details
//     */
//    @Operation(summary = "Create a new permission", description = "Creates a new permission with the provided details")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "Permission created successfully"),
//            @ApiResponse(responseCode = "404", description = "Route not found")
//    })
//    @PostMapping("/create_permits")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<Map<String, Object>> createPermission(@RequestBody Map<String, Object> requestBody) {
//        Route route = routeService.getRouteByPath((String) requestBody.get("path"));
//
//        if (route == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("error", "Route not found"));
//        }
//
//        Permission permission = new Permission();
//        permission.setName((String) requestBody.get("name"));
//        permission.setDescription((String) requestBody.get("description"));
//        permission.setRoute(route);
//
//        Permission createdPermission = permissionService.createPermission(permission);
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(Map.of("message", "Permission Created Successfully", "permission", createdPermission));
//    }
//
//    /**
//     * Get all permissions.
//     *
//     * @return ResponseEntity with a list of all permissions
//     */
//    @Operation(summary = "Get all permissions", description = "Retrieves a list of all permissions")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Permissions retrieved successfully")
//    })
//    @GetMapping("/get_permits")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<Map<String, Object>> getAllPermissions() {
//        List<Permission> permissions = permissionService.getAllPermissions();
//
//        return ResponseEntity.ok(Map.of("message", "Permissions retrieved successfully", "permissions", permissions));
//    }

//    /**
//     * Create a new route.
//     *
//     * @param route Route object containing route details
//     * @return ResponseEntity with creation message and route details
//     */
//    @Operation(summary = "Create a new route", description = "Creates a new route with the provided details")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "Route created successfully"),
//            @ApiResponse(responseCode = "400", description = "Invalid route details provided")
//    })
//    @PostMapping("/create_routes")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<Map<String, Object>> createRoutes(@RequestBody Route route) {
//        Route createdRoute = routeService.createRoute(route);
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(Map.of("message", "Route Created Successfully", "route", createdRoute));
//    }
//
//    /**
//     * Assign a permission to a role.
//     *
//     * @param requestBody Map containing username, route path, and permission ID
//     * @return ResponseEntity with assignment message
//     */
//    @Operation(summary = "Assign a permission to a role", description = "Assigns permission to a role")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Permission assigned to role successfully"),
//            @ApiResponse(responseCode = "404", description = "User or Route not found")
//    })
//    @PostMapping("/assign_permission_to_role")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<Map<String, Object>> assignPermissionToRole(@RequestBody Map<String, String> requestBody) {
//        Long roleId = Long.valueOf(requestBody.get("roleId"));
//        String path = requestBody.get("path");
//        Long permissionId = Long.valueOf(requestBody.get("permission_id"));
//
//        Route route = routeService.getRouteByPath(path);
//        if (route == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("error", "User or Route not found"));
//        }
//
//        assignPermissionService.assignPermissionToRole(roleId, path, permissionId);
//
//        return ResponseEntity.ok(Map.of("message", "Permission assigned to role successfully"));
//    }
}

