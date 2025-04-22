package com.example.rbac.Service;

import com.example.rbac.Model.Permissions;
import com.example.rbac.Repository.PermissionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermissionsService {

    @Autowired
    private PermissionsRepository permissionsRepository;

    // ✅ Create a new routes
    @Transactional
    public Permissions createPermission(Permissions permissions) {
        return permissionsRepository.save(permissions);
    }

    // ✅ Retrieve all routes
    @Transactional(readOnly = true)
    public List<Permissions> getAllPermissions() {
        return permissionsRepository.findAll();
    }

    // ✅ Retrieve route by ID
    @Transactional(readOnly = true)
    public Permissions getaPermissionById(Long id) {
        return permissionsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Routes not found for this ID: " + id));
    }

    // ✅ Retrieve route by path
    @Transactional(readOnly = true)
    public Permissions getPermissionByName(String permission) {
        return permissionsRepository.findByPermissionEquals(permission);
    }

    // ✅ Update route
    @Transactional
    public Permissions updatePermission(Long id, Permissions permissionsDetails) {
        Permissions permissions = getaPermissionById(id);
        permissions.setName(permissionsDetails.getName());
        permissions.setPermission(permissionsDetails.getPermission());
        return permissionsRepository.save(permissions);
    }

    // ✅ Delete route
    @Transactional
    public void deletePermission(Long id) {
        Permissions permissions = getaPermissionById(id);
        permissionsRepository.delete(permissions);
    }
}
