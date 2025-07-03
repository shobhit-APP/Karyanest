package com.example.rbac.Service;

import com.example.rbac.DTO.RolePermissionResponseDTO;
import com.example.rbac.Model.RolesPermission;
import com.example.rbac.Model.Permissions;
import com.example.rbac.Model.Roles;
import com.example.rbac.Repository.RolesPermissionRepository;
import com.example.rbac.Repository.PermissionsRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AssignPermissionsService {

    @Autowired
    private PermissionsRepository permissionsRepository;

    @Autowired
    private RolesService roleService;

    @Autowired
    private RolesPermissionRepository rolesPermissionRepository;

//    /**
//     * Assigns a permission to a role for a specific route
//     * @param roleId the role I'd of the role (e.g., "3->ROLE_ADMIN", "2->ROLE_USER")
//     * @param permission the permission to assign
//     */
//    @Transactional
//    public void assignPermissionToRole(Long roleId, List<Integer> permissionIds) {
//        // Find the role by ID
//        Roles role = roleService.getRoleById(roleId);
//        if (role == null) {
//            throw new IllegalArgumentException("Role not found with this ID: " + roleId);
//        }
//        // Find the permission by name
//        Permissions foundPermission = permissionsRepository.findByPermissionEquals(permission);
//        if (foundPermission == null) {
//            throw new IllegalArgumentException("Permission not found in records: " + permission);
//        }
//        // Fetch the RolesPermission entity directly
//        RolesPermission existingRolePermission = rolesPermissionRepository
//                .findByRoleAndPermissions(role, foundPermission);
//        if (existingRolePermission != null) {
//            throw new IllegalArgumentException("Role is already assigned to the Permission");
//        }
//        // Create a new RolesPermission object and set its attributes
//        RolesPermission rolePermission = new RolesPermission();
//        rolePermission.setRole(role);
//        rolePermission.setPermissions(foundPermission);
//        // Save the RolesPermission object to the database
//        rolesPermissionRepository.save(rolePermission);
//        System.out.println("✅ Successfully assigned role " + role.getName() + " to route " + permission);
//    }
@Transactional
private Pair<Roles, List<Permissions>> validateAndFetchEntities(Long roleId, List<Long> permissionIds) {
    Roles role = roleService.getRoleById(roleId);
    if (role == null) {
        throw new IllegalArgumentException("❌ Role not found with ID: " + roleId);
    }

    List<Permissions> permissions = permissionsRepository.findAllById(permissionIds);
    if (permissions.size() != permissionIds.size()) {
        throw new IllegalArgumentException("❌ Some permission IDs are invalid or not found.");
    }

    return Pair.of(role, permissions);
}
    @Transactional
    public void assignPermissionToRole(Long roleId, List<Long> permissionIds) {
        Pair<Roles, List<Permissions>> result = validateAndFetchEntities(roleId, permissionIds);
        Roles role = result.getLeft();
        List<Permissions> permissions = result.getRight();

        List<RolesPermission> existingMappings = rolesPermissionRepository.findAllByRole(role);
        Set<Long> existingIds = existingMappings.stream()
                .map(rp -> rp.getPermissions().getId())
                .collect(Collectors.toSet());

        List<RolesPermission> newMappings = permissions.stream()
                .filter(p -> !existingIds.contains(p.getId()))
                .map(p -> new RolesPermission(role, p))
                .toList();

        rolesPermissionRepository.saveAll(newMappings);

        System.out.println("✅ Assigned " + newMappings.size() + " new permissions to role: " + role.getName());
    }
    @Transactional
    public void replaceAllPermissionsForRole(Long roleId, List<Long> permissionIds) {
        Pair<Roles, List<Permissions>> result = validateAndFetchEntities(roleId, permissionIds);
        Roles role = result.getLeft();
        List<Permissions> newPermissions = result.getRight();

        // Remove old permissions
        rolesPermissionRepository.deleteAllByRole(role);

        // Add all new mappings
        List<RolesPermission> newMappings = newPermissions.stream()
                .map(p -> new RolesPermission(role, p))
                .toList();

        rolesPermissionRepository.saveAll(newMappings);

        System.out.println("🔁 Replaced all permissions for role: " + role.getName());
    }

    public List<RolePermissionResponseDTO> getAllPermissionsForRoleId(Long roleId) {
        Roles role = roleService.getRoleById(roleId);
        if (role == null) {
            throw new IllegalArgumentException("Role not found with ID: " + roleId);
        }

        List<RolesPermission> mappings = rolesPermissionRepository.findAllByRole(role);

        return mappings.stream()
                .map(rp -> {
                    Permissions p = rp.getPermissions();
                    return new RolePermissionResponseDTO(p.getId(), p.getName(), p.getPermission(), p.getDescription(),rp.getId());
                })
                .collect(Collectors.toList());
    }

    public List<RolePermissionResponseDTO> getAllAssignedPermissions() {
        List<RolesPermission> mappings = rolesPermissionRepository.findAll();

        return mappings.stream()
                .map(rp -> {
                    Permissions p = rp.getPermissions();
                    return new RolePermissionResponseDTO(p.getId(), p.getName(), p.getPermission(), p.getDescription(),rp.getId());
                })
                .collect(Collectors.toList());
    }



//public void assignPermissionToRole(Long roleId, List<Long> permissionIds) {
//    // 1. Fetch the role by ID
//    Roles role = roleService.getRoleById(roleId);
//    if (role == null) {
//        throw new IllegalArgumentException("Role not found with ID: " + roleId);
//    }
//
//    // 2. Fetch all permissions in one go using IN clause
//    List<Permissions> permissions = permissionsRepository.findAllById(permissionIds);
//    if (permissions.size() != permissionIds.size()) {
//        throw new IllegalArgumentException("Some permission IDs are invalid or not found.");
//    }
//
//    // 3. Fetch all existing role-permission mappings in one DB call
//    List<RolesPermission> existingMappings = rolesPermissionRepository.findAllByRole(role);
//
//    // 4. Extract already assigned permission IDs to skip them
//    Set<Long> alreadyAssignedPermissionIds = existingMappings.stream()
//            .map(rp -> rp.getPermissions().getId())
//            .collect(Collectors.toSet());
//
//    // 5. Create new mappings for only unassigned permissions
//    List<RolesPermission> newMappings = permissions.stream()
//            .filter(permission -> !alreadyAssignedPermissionIds.contains(permission.getId()))
//            .map(permission -> {
//                RolesPermission rp = new RolesPermission();
//                rp.setRole(role);
//                rp.setPermissions(permission);
//                return rp;
//            })
//            .collect(Collectors.toList());
//
//    // 6. Bulk save all new mappings in one go
//    rolesPermissionRepository.saveAll(newMappings);
//
//    System.out.println("✅ Assigned " + newMappings.size() + " new permissions to role: " + role.getName());
//}
//    public void replaceAllPermissionsForRole(Long roleId, List<Long> permissionIds) {
//        // 1. Fetch the role by ID
//        Roles role = roleService.getRoleById(roleId);
//        if (role == null) {
//            throw new IllegalArgumentException("Role not found with ID: " + roleId);
//        }
//
//        // 2. Fetch all permissions in one go using IN clause
//        List<Permissions> permissions = permissionsRepository.findAllById(permissionIds);
//        if (permissions.size() != permissionIds.size()) {
//            throw new IllegalArgumentException("Some permission IDs are invalid or not found.");
//        }
//        // 3. Fetch all existing role-permission mappings in one DB call
//        List<RolesPermission> existingMappings = rolesPermissionRepository.findAllByRole(role);
//        // 4. Extract already assigned permission IDs to skip them
//        Set<Long> alreadyAssignedPermissionIds = existingMappings.stream()
//                .map(rp -> rp.getPermissions().getId())
//                .collect(Collectors.toSet());
//
//        // 5. Create new mappings for only unassigned permissions
//        List<RolesPermission> newMappings = permissions.stream()
//                .filter(permission -> !alreadyAssignedPermissionIds.contains(permission.getId()))
//                .map(permission -> {
//                    RolesPermission rp = new RolesPermission();
//                    rp.setRole(role);
//                    rp.setPermissions(permission);
//                    return rp;
//                })
//                .toList();
//        // 1. Remove all existing permissions
//        role.getPermissions().clear();
//
//        // 2. Fetch new permissions by ID
//        List<Permission> newPermissions = permissionRepository.findAllById(permissionIds);
//
//        // 3. Set new permissions
//        role.setPermissions(newPermissions);
//
//        // 4. Save updated role
//        roleRepository.save(role);
//    }

}
