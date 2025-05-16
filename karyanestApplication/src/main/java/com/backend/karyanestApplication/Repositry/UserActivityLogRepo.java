package com.backend.karyanestApplication.Repositry;

import com.backend.karyanestApplication.Model.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface UserActivityLogRepo extends JpaRepository<UserActivityLog, Long> {
    List<UserActivityLog> findByUserId(Long userId);
    @Query("SELECT u FROM UserActivityLog u WHERE u.userId = :userId ORDER BY u.createdAt DESC")
    List<UserActivityLog> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);
    List<UserActivityLog> findByUserIdAndActivityTypeOrderByCreatedAtDesc(Long userId, String activityType);
}
