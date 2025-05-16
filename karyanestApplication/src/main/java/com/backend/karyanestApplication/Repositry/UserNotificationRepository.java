package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.Model.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
}
