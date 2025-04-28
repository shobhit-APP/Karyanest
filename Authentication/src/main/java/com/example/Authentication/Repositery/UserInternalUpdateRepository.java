package com.example.Authentication.Repositery;

import com.example.Authentication.Model.UserInternalUpdateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInternalUpdateRepository extends JpaRepository<UserInternalUpdateEntity, Long> {
    UserInternalUpdateEntity findByUserId(Long userId);

    UserInternalUpdateEntity findByUsername(String username);

    UserInternalUpdateEntity findByEmail(String email);

    UserInternalUpdateEntity findByPhoneNumber(String phoneNumber);
}
