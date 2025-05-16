//
//package com.backend.karyanestApplication.Service;
//
//import com.backend.karyanestApplication.Exception.CustomException;
//import com.backend.karyanestApplication.Model.*;
//import com.backend.karyanestApplication.Repositry.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//public class PermissionService {
//
//    private final PermissionRepository permissionRepository;
//    private final RolePermissionRepository rolePermissionRepository;
//    private final RouteRepository routeRepository;
//    private final RoleRepository roleRepository;
//    private final UserRepo userRepo;
//    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);
//
//    public PermissionService(PermissionRepository permissionRepository, RolePermissionRepository rolePermissionRepository,
//                             RouteRepository routeRepository, RoleRepository roleRepository, UserRepo userRepo) {
//        this.permissionRepository = permissionRepository;
//        this.rolePermissionRepository = rolePermissionRepository;
//        this.routeRepository = routeRepository;
//        this.roleRepository = roleRepository;
//        this.userRepo = userRepo;
//    }
//
//    public List<Permission> getAllPermissions() {
//        return permissionRepository.findAll();
//    }
//
//    public Permission getPermissionById(Long id) {
//        return permissionRepository.findById(id)
//                .orElseThrow(() -> new CustomException("Permission not found with id: " + id));
//    }
//
//    public Permission createPermission(Permission permission) {
//        return permissionRepository.save(permission);
//    }
//
//    public Permission updatePermission(Long id, Permission updatedPermission) {
//        return permissionRepository.findById(id).map(permission -> {
//            permission.setName(updatedPermission.getName());
//            permission.setDescription(updatedPermission.getDescription());
//            return permissionRepository.save(permission);
//        }).orElseThrow(() -> new CustomException("Permission not found with id: " + id));
//    }
//
//    public void deletePermission(Long id) {
//        if (!permissionRepository.existsById(id)) {
//            throw new CustomException("Permission not found with id: " + id);
//        }
//        permissionRepository.deleteById(id);
//    }
//
//    public void debugPathIssue(String path) {
//        List<Route> routes = routeRepository.findAllByPath(path);
//        logger.info("Found {} routes for path: {}", routes.size(), path);
//
//        for (Route route : routes) {
//            logger.info("Route ID: {}, Path: {}, Name: {}", route.getId(), route.getPath(), route.getName());
//        }
//    }
//
//    @Transactional
//    @Cacheable("permissions")
//    public boolean checkPermission(String roleName, String path, String username) {
//        logger.info("Checking permission for role: {} and path: {}", roleName, path);
//             // Special case for users accessing their own profiles
//        if (("ROLE_USER".equals(roleName) || "ROLE_AGENT".equals(roleName)) && path.startsWith("/v1/users/")) {
//            String[] pathParts = path.split("/");
//            if (path.matches("/v1/users/currentUser")) {
//                return true;
//            }
//
//            if (pathParts.length != 4) {
//                logger.warn("❌ Invalid path structure: {}", path);
//                return false;
//            }
//
//            String pathId = pathParts[3]; // Extract the ID from the path
//            User user = userRepo.findByUsername(username);
//
//            if (user == null) {
//                logger.error("❌ User not found: {}", username);
//                return false;
//            }
//
//            if (!user.getId().toString().equals(pathId)) {
//                logger.warn("❌ Access denied: User ID in path does not match the logged-in user.");
//                return false;
//            }
//
//            logger.info("✅ Permission granted: User accessing own profile");
//            return true;
//        }
//        Route route = routeRepository.findByPathEquals(path);
//        debugPathIssue(path);
//
//        if (route == null) {
//            route = findMatchingPatternRoute(path);
//        }
//
//        if (route == null) {
//            logger.warn("❌ No route found for path: {}", path);
//            return false;
//        }
//
//        Permission action = permissionRepository.findByRouteId(route.getId());
//        if (action == null) {
//            logger.warn("❌ No action found for route: {}", route.getName());
//            return false;
//        }
//
//        UserRole userRole = roleRepository.findByName(roleName)
//                .orElseThrow(() -> new CustomException("❌ Role Not Found: " + roleName));
//
//        boolean hasPermission = rolePermissionRepository
//                .existsByRoleIdAndRouteIdAndPermissionId(userRole.getId(), route.getId(), action.getId());
//
//        if (!hasPermission) {
//            logger.warn("❌ Role {} does not have permission for path: {}", roleName, path);
//        } else {
//            logger.info("✅ Permission granted for role: {} on path: {}", roleName, path);
//        }
//
//        return hasPermission;
//    }
//
//    public Route findMatchingPatternRoute(String actualPath) {
//        List<Route> patternRoutes = routeRepository.findAllIdPatternRoutes();
//
//        for (Route patternRoute : patternRoutes) {
//            String routePattern = patternRoute.getPath();
//            String patternBase = routePattern.replace("{id}", "");
//            int idPosition = routePattern.indexOf("{id}");
//            String prefix = patternBase.substring(0, idPosition);
//            String suffix = idPosition + 4 < patternBase.length() ? patternBase.substring(idPosition + 4) : "";
//            String regexPattern = "^" + prefix + "[0-9]+" + suffix + "$";
//
//            if (actualPath.matches(regexPattern)) {
//                return patternRoute;
//            }
//        }
//        return null;
//    }
//}
