package com.backend.karyanestApplication.Service;

import com.example.module_b.ExceptionAndExceptionHandler.CustomException;
import com.backend.karyanestApplication.Model.UserPropertyVisit;
import com.backend.karyanestApplication.Repositry.UserPropertyVisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserPropertyVisitService {

    private final UserPropertyVisitRepository userPropertyVisitRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserPropertyVisitService(UserPropertyVisitRepository userPropertyVisitRepository) {
        this.userPropertyVisitRepository = userPropertyVisitRepository;
    }

    /**
     * Get all property visits
     * @return List of all property visits
     */
    public List<UserPropertyVisit> getAllVisits() {
        return userPropertyVisitRepository.findAll();
    }

    /**
     * Get property visit by ID
     * @param id Visit ID
     * @return UserPropertyVisit if found
     */
    public Optional<UserPropertyVisit> getVisitById(Long id) {
        return userPropertyVisitRepository.findById(id);
    }

    /**
     * Get visits by user ID
     * @param userId User ID
     * @return List of visits for the user
     */
    public List<UserPropertyVisit> getVisitsByUserId(Long userId) {
        return userPropertyVisitRepository.findByUserId(userId);
    }

    /**
     * Get visits by property ID
     * @param propertyId Property ID
     * @return List of visits for the property
     */
    public List<UserPropertyVisit> getVisitsByPropertyId(Long propertyId) {
        return userPropertyVisitRepository.findByPropertyId(propertyId);
    }

    /**
     * Record a new property visit or update existing
     * @param userId User ID
     * @param propertyId Property ID
     * @param deviceInfo Device information
     * @param locationCoords Location coordinates
     * @return Saved UserPropertyVisit object
     */
    public UserPropertyVisit recordVisit(Long userId, Long propertyId, String deviceInfo, String locationCoords) {
        // Check if user has visited this property before
        Optional<UserPropertyVisit> existingVisit = userPropertyVisitRepository
                .findByUserIdAndPropertyId(userId, propertyId);

        if (existingVisit.isPresent()) {
            // Update existing visit information
            UserPropertyVisit visit = existingVisit.get();
            visit.setVisitTime(Timestamp.from(Instant.now()));

            // Update device and location info if provided
            if (deviceInfo != null) {
                visit.setDeviceInfo(deviceInfo);
            }
            if (locationCoords != null) {
                visit.setLocationCoords(locationCoords);
            }

            return userPropertyVisitRepository.save(visit);
        } else {
            // Create new visit record
            UserPropertyVisit newVisit = new UserPropertyVisit();
            newVisit.setUserId(userId);
            newVisit.setPropertyId(propertyId);
            newVisit.setVisitTime(Timestamp.from(Instant.now()));
            newVisit.setDeviceInfo(deviceInfo);
            newVisit.setLocationCoords(locationCoords);

            return userPropertyVisitRepository.save(newVisit);
        }
    }
    /**
     * Delete a visit record
     * @param id Visit ID
     */
    public void deleteVisit(Long id) {
        if (!userPropertyVisitRepository.existsById(id)) {
            throw new CustomException("Visit record not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
        userPropertyVisitRepository.deleteById(id);
    }
//    public void recordVisitsBatch(List<UserPropertyVisit> visits) {
//        visits.stream()
//                .map(visit -> userPropertyVisitRepository
//                        .findByUserIdAndPropertyId(visit.getUserId(), visit.getPropertyId())
//                        .map(existingVisit -> {
//                            existingVisit.setVisitCount(existingVisit.getVisitCount() + 1);
//                            existingVisit.setDeviceInfo(visit.getDeviceInfo());
//                            existingVisit.setVisitTime(Timestamp.from(Instant.now()));
//                            return existingVisit;
//                        }).orElse(visit)) // If no existing visit, use the new one
//                .forEach(userPropertyVisitRepository::save);
//    }
//    public void recordVisitsBatch(List<UserPropertyVisit> visits) {
//        String sql = "INSERT INTO user_property_visits (user_id, property_id, device_info, visit_count, visit_time) " +
//                "VALUES (?, ?, ?, 1, NOW()) " +
//                "ON DUPLICATE KEY UPDATE " +
//                "visit_count = visit_count + 1, device_info = VALUES(device_info), visit_time = NOW()";
//
//        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                UserPropertyVisit visit = visits.get(i);
//                ps.setLong(1, visit.getUserId());
//                ps.setLong(2, visit.getPropertyId());
//                ps.setString(3, visit.getDeviceInfo());
//            }
//
//            @Override
//            public int getBatchSize() {
//                return visits.size();
//            }
//        });
//    }
}