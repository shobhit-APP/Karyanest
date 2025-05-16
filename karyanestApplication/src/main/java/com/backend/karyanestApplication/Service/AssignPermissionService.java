//package com.backend.karyanestApplication.Service;
//
//import com.backend.karyanestApplication.Model.*;
//import com.backend.karyanestApplication.Repositry.*;
//import com.backend.karyanestApplication.Exception.CustomException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//public class AssignPermissionService {
//    @Autowired
//    private RouteRepository routeRepository;
//    @Autowired
//    private PermissionRepository permissionRepository;
//    @Autowired
//    private RoleService roleService;
//    @Autowired
//    private PermissionService permissionService;
//    @Autowired
//    private RolePermissionRepository rolePermissionRepository;
//
//    /**
//     * Validates if the permission ID is valid for the given route
//     * @param routeId the route ID
//     * @param permissionId the permission ID to validate
//     * @throws CustomException if the permission is invalid for the route
//     */
//    private void validateRoutePermissionMapping(Long routeId, Long permissionId) {
//        Permission validPermission = permissionRepository.findByRouteId(routeId);
//
//        if (validPermission == null) {
//            throw new CustomException("No permission defined for route ID: " + routeId);
//        }
//
//        if (!validPermission.getId().equals(permissionId)) {
//            throw new CustomException("Invalid permission ID: " + permissionId +
//                    " for route ID: " + routeId +
//                    ". Expected permission ID: " + validPermission.getId());
//        }
//    }
//
//    /**
//     * Assigns a permission to a role for a specific route
//     * @param roleId the role I'd of the role (e.g., "3->ROLE_ADMIN", "2->ROLE_USER")
//     * @param path the route path
//     * @param permissionId the permission ID to assign
//     */
//    @Transactional
//    public void assignPermissionToRole(Long roleId, String path, Long permissionId) {
//        // Find the role by name
//        UserRole role = roleService.getRoleById(roleId);
//        if (role == null) {
//            throw new CustomException("Role not found with this Id : " + roleId);
//        }
//
//        // Find the route by path
//        Route route = routeRepository.findByPathEquals(path);
//        if (route == null) {
//            permissionService.debugPathIssue(path);
//            throw new CustomException("Route not found for path: " + path);
//        }
//
//        // Find the permission by ID
//        Permission permission = permissionRepository.findById(permissionId)
//                .orElseThrow(() -> new CustomException("Permission not found with ID: " + permissionId));
//
//        // Validate if the permission ID matches expected permission for this route
//        validateRoutePermissionMapping(route.getId(), permissionId);
//
//        // Check if this combination already exists (role-route-permission)
//        boolean exists = rolePermissionRepository.existsByRoleIdAndRouteIdAndPermissionId(
//                role.getId(), route.getId(), permission.getId());
//        if (exists) {
//            throw new CustomException("Permission already assigned for role: \"" + role.getName() +
//                    "\", route: \"" + path + "\", permission: \"" + permission.getName() + "\"");
//        }
//        // Create a new RolePermission object and set its attributes
//        RolePermission rolePermission = new RolePermission();
//        rolePermission.setRole(role);
//        rolePermission.setPermission(permission);
//        rolePermission.setRoute(route);
//
//        // Save the RolePermission object to the database
//        rolePermissionRepository.save(rolePermission);
//
//        System.out.println("✅ Successfully assigned permission " + permission.getName() +
//                " for route " + path + " to role " + role.getName());
//    }
//
//    /**
//     * Gets the correct permission ID for a given route path
//     * @param path the route path
//     * @return the correct permission ID
//     */
//    public Long getCorrectPermissionIdForRoute(String path) {
//        Route route = routeRepository.findByPathEquals(path);
//        if (route == null) {
//            throw new CustomException("Route not found for path: " + path);
//        }
//
//        Permission permission = permissionRepository.findByRouteId(route.getId());
//        if (permission == null) {
//            throw new CustomException("No permission defined for route: " + path);
//        }
//
//        return permission.getId();
//    }
//}