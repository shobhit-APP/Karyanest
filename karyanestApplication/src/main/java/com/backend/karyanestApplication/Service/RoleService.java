//package com.backend.karyanestApplication.Service;
//
//import com.backend.karyanestApplication.Exception.CustomException;
//import com.backend.karyanestApplication.Model.UserRole;
//import com.backend.karyanestApplication.Repositry.RoleRepository;
//import com.backend.karyanestApplication.Repositry.UserRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class RoleService {
//
//    @Autowired
//    private RoleRepository roleRepository;
//    @Autowired
//    private UserRepo userRepo;
////    public RoleService(RoleRepository roleRepository) {
////        this.roleRepository = roleRepository;
////    }
//
//    // ✅ Get all roles
//    @Transactional(readOnly = true)
//    public List<UserRole> getAllRoles() {
//        return roleRepository.findAll();
//    }
//
//    // ✅ Get role by ID
//    @Transactional(readOnly = true)
//    public UserRole getRoleById(Long id) {
//        return roleRepository.findById(id)
//                .orElseThrow(() -> new CustomException("Role not found for this ID: " + id));
//    }
//
//    // ✅ Create a new role
//    @Transactional
//    public UserRole createRole(UserRole role) {
//        return roleRepository.save(role);
//    }
//
//
//    // ✅ Delete role
//    @Transactional
//    public void deleteRole(Long id) {
//        UserRole role = getRoleById(id); // Ensuring the role exists before deleting
//        roleRepository.delete(role);
//    }
//
//    public UserRole getRoleByName(String roleName) {
//        return roleRepository.findByName(roleName).orElseThrow(()->new CustomException("Role not found"));
//    }
//
//    public Optional<UserRole> findByName(String roleName) {
//        return roleRepository.findByName(roleName);
//    }
//}
