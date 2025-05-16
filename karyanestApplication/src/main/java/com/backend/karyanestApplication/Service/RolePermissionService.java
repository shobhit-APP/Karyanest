//package com.backend.karyanestApplication.Service;
//
//import com.backend.karyanestApplication.Exception.CustomException;
//import com.backend.karyanestApplication.Model.RolePermission;
//import com.backend.karyanestApplication.Repositry.RolePermissionRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//
//@Service
//public class RolePermissionService {
//
//    private final RolePermissionRepository rolePermissionRepository;
//
//    public RolePermissionService(RolePermissionRepository rolePermissionRepository) {
//        this.rolePermissionRepository = rolePermissionRepository;
//    }
//
//    // ✅ Create a new RolePermission
//    public RolePermission createRolePermission(RolePermission rolePermission) {
//        return rolePermissionRepository.save(rolePermission);
//    }
//
//    // ✅ Retrieve all RolePermissions
//    public List<RolePermission> getAllRolePermissions() {
//        return rolePermissionRepository.findAll();
//    }
//
//    // ✅ Retrieve RolePermissions by Role Name
//    public List<RolePermission> getRolePermissionsByRoleName(String roleName) {
//        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleName(roleName);
//        if (rolePermissions.isEmpty()) {
//            throw new CustomException("No permissions found for role: " + roleName);
//        }
//        return rolePermissions;
//    }
//
//    // ✅ Retrieve RolePermissions by Role ID
//    public List<RolePermission> getRolePermissionsByRoleId(Long roleId) {
//        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleId(roleId);
//        if (rolePermissions.isEmpty()) {
//            throw new CustomException("No permissions found for role ID: " + roleId);
//        }
//        return rolePermissions;
//    }
//
//    // ✅ Retrieve RolePermission by ID
//    public RolePermission getRolePermissionById(Long id) {
//        return rolePermissionRepository.findById(id)
//                .orElseThrow(() -> new CustomException("RolePermission not found for this id: " + id));
//    }
//
//    // ✅ Update RolePermission
//    public RolePermission updateRolePermission(Long id, RolePermission rolePermissionDetails) {
//        RolePermission rolePermission = getRolePermissionById(id);
//
//        rolePermission.setRole(rolePermissionDetails.getRole());
//        rolePermission.setRoute(rolePermissionDetails.getRoute());
//        rolePermission.setPermission(rolePermissionDetails.getPermission());
//
//        return rolePermissionRepository.save(rolePermission);
//    }
//
//    // ✅ Delete RolePermission
//    public void deleteRolePermission(Long id) {
//        RolePermission rolePermission = getRolePermissionById(id);
//        rolePermissionRepository.delete(rolePermission);
//    }
//}
