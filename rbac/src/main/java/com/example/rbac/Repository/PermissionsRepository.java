package com.example.rbac.Repository;


import com.example.rbac.Model.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionsRepository extends JpaRepository<Permissions, Long> {
    Permissions findByPermissionEquals(@Param("permission") String permission);
//    Permissions findByPathEquals(String path);
    // Repository method for finding all pattern routes
//    @Query("SELECT r FROM Routes r WHERE r.path LIKE '%/{id}%'")
//    List<Permission> findAllIdPatternPermission();
//    List<Permission> findAllByPath(String path);
}
