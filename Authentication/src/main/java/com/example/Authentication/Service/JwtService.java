//package com.example.Authentication.Service;
//
//import com.example.Authentication.DTO.AuthResponseDTO;
//import com.example.Authentication.DTO.UpdateUserInternalDTO;
//import com.example.Authentication.DTO.UserDTO;
//import com.example.Authentication.UTIL.JwtUtil;
//import com.example.rbac.Model.RolesPermission;
//import com.example.rbac.Repository.RolesPermissionRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
//@Service
//public class JwtService {
//    @Autowired
//    private JwtUtil jwtUtil;
//    @Autowired
//    private ReferenceTokenService referenceTokenService;
//    @Autowired
//    private Auth auth;
//    @Autowired
//    private RolesPermissionRepository rolesPermissionRepository;
//
//    /**
//     * Generate authentication response for a user
//     *
//     * @param user the user to generate authentication for
//     * @return ResponseEntity with auth response or error
//     */
//    public ResponseEntity<?> generateAuthResponseForUser(UserDTO user) {
//        // Check if user is verified
//        if (Objects.equals(user.getVerificationStatus(), "Unverified")) {
//            return auth.notifyUser(user);
//        }
//        // Generate JWT token
//        Long userRoleId = user.getRoleId();
//        String UserRole=user.getRole();
//        // Fetch permissions for the user
//        List<RolesPermission> rolePermissions = rolesPermissionRepository.findPermissionsByRoleId(userRoleId);
//        List<String> permissionList = rolePermissions.stream()
//                .map(rp -> rp.getPermissions().getPermission())
//                .collect(Collectors.toList());
//
//// Generate JWT token with permissions
//        String jwtToken = jwtUtil.generateToken(user.getUsername(), UserRole, user.getUserId(), user.getFullName(), permissionList);
//        String refreshToken = referenceTokenService.generateReferenceToken(jwtToken);
//        UpdateUserInternalDTO updateUserInternalDTO=new UpdateUserInternalDTO();
//        updateUserInternalDTO.setStatus("Active");
//        auth.setUserDetailsInternally(updateUserInternalDTO);
//        // Generate and return response
//        AuthResponseDTO authResponse = getJwtResponse(jwtToken, refreshToken, UserRole,rolePermissions);
//        return ResponseEntity.ok(authResponse);
//    }
//
//    /**
//     * Create JWT response with user details and permissions.
//     *
//     * @param jwtToken     the JWT token
//     * @param refreshToken the refresh token
////     * @param user         the user
//     * @return the authentication response DTO
//     */
//    public AuthResponseDTO getJwtResponse(String jwtToken, String refreshToken, String userRole,List<RolesPermission>rolesPermissions) {
//        // Group permissions by route path
//        List<Map<String, Object>> permissions = rolesPermissions.stream()
//                .collect(Collectors.groupingBy(
//                        rp -> rp.getPermissions().getPath(), // Get path from Permissions
//                        Collectors.mapping(rp -> rp.getPermissions().getPermission(), Collectors.toList())
//                ))
//                .entrySet()
//                .stream()
//                .map(entry -> Map.of(
//                        "path", entry.getKey(),
//                        "actions", entry.getValue()
//                ))
//                .toList();
//
////        // Convert user to DTO
////        UserResponseDTO userResponseDTO = userService.mapToDTO(user);
//
//        // Return JWT response with permissions
//        return new AuthResponseDTO(jwtToken, refreshToken, userRole,permissions);
//    }
//
//
//}
//
