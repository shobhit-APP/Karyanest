package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.Model.User;
//import com.backend.karyanestApplication.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
        // Check if a user with the specified email exists
    boolean existsByUsername(String username);
    User findByUsername(String username);

    User findByemail(String username);

    boolean existsByemail(String email);
    // Method to find all users who have a specific role
//    List<User> findByUserRoleName(String roleName);
    List<User> findByRoleId(Long roleId);
    User findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    User getUserById(Long userId);
    // Find users by parent_code
    List<User> findByParentCode(Long parentCode);

}
