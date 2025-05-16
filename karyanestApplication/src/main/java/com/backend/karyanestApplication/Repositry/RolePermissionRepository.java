//
//package com.backend.karyanestApplication.Repositry;
//
//import com.backend.karyanestApplication.Model.RolePermission;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
//     List<RolePermission> findByRoleName(String roleName); // Notice the camel case matching the property
//    List<RolePermission> findByRoleId(Long roleId);
//
//    List<RolePermission> findPermissionsByRoleId(Long roleId);
//
////    RolePermission findByRoleIdAndRouteIdAndPermissionId(Long id, Long id1, Long id2);
//    boolean existsByRoleIdAndRouteIdAndPermissionId(Long id, Long id1, Long id2);
//}
