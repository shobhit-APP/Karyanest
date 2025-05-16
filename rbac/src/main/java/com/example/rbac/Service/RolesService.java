package com.example.rbac.Service;

import com.example.rbac.Model.Roles;
import com.example.rbac.Repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RolesService {

    @Autowired
    private final RolesRepository roleRepository;

    public RolesService(RolesRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // ✅ Get all roles
    @Transactional(readOnly = true)
    public List<Roles> getAllRoles() {
        return roleRepository.findAll();
    }

    // ✅ Get role by ID
    @Transactional(readOnly = true)
    public Roles getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found for this ID: " + id));
    }

    // ✅ Create a new role
    @Transactional
    public Roles createRole(Roles role) {
        return roleRepository.save(role);
    }


    // ✅ Delete role
    @Transactional
    public void deleteRole(Long id) {
        Roles role = getRoleById(id); // Ensuring the role exists before deleting
        roleRepository.delete(role);
    }

    public Roles getRoleByName(String roleName) {
        return roleRepository.findByName(roleName).orElseThrow(()->new IllegalArgumentException("Role not found"));
    }

    public Optional<Roles> findByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

}
