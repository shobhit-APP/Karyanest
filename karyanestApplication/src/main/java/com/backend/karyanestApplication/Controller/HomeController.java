package com.backend.karyanestApplication.Controller;

import com.backend.karyanestApplication.Service.PropertyService;
import com.backend.karyanestApplication.Service.UserPropertyVisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for home page operations.
 */
@RestController
@RequestMapping("/v1/home")
@Tag(name = "Home", description = "Home page operations")
public class HomeController {
    @Autowired
    private PropertyController propertyController;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserPropertyVisitService userPropertyVisitService;

    /**
     * Welcome message.
     *
     * @return A welcome message
     */
    @Operation(summary = "Welcome message", description = "Get a welcome message without requiring authentication")
    @GetMapping("/welcome")
    public String welcome() {
        return "🎉 Welcome to the Home Page! 🌟 Feel free to explore—no authentication required. Enjoy your stay! 😊";
    }
//
//    @GetMapping("/viewProps")
//    public ResponseEntity<List<PropertyResponseDTO>> getAllProperties(HttpServletRequest request) {
//        return ResponseEntity.ok(propertyController.getAllProperties(request).getBody());
//    }
//
//    /**
//     * View a property by ID and record anonymous visit
//     */
//    @Operation(summary = "View property details", description = "Get property details by ID and record anonymous visit")
//    @GetMapping("/viewProp/{id}")
//    public ResponseEntity<?> viewPropertyById(@PathVariable Long id, HttpServletRequest request) {
//        try {
//            // Get property details
//            PropertyResponseDTO property = propertyService.getPropertyById(id);
//
//            // Record anonymous visit
//            String deviceInfo = request.getHeader("User-Agent");
//
//            // For anonymous users (no authentication), use userId = 0 or a specific anonymous ID
//            Long anonymousUserId = 0L; // You could replace this with a specific ID reserved for anonymous users
//
//            userPropertyVisitService.recordVisit(anonymousUserId, id, deviceInfo, null);
//
//            return ResponseEntity.ok(property);
//        } catch (Exception e) {
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("error", "Error retrieving property");
//            errorResponse.put("message", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//
//    /**
//     * Get most visited properties
//     */
//    @Operation(summary = "Get most visited properties", description = "Get popular properties based on visit count")
//    @GetMapping("/popular")
//    public ResponseEntity<?> getPopularProperties() {
//        try {
//            // Implementation depends on your specific requirements
//            // This is a sample approach
//
//            // Get all property visits
//            List<UserPropertyVisit> allVisits = userPropertyVisitService.getAllVisits();
//
//            // Process the results to get top properties (simplified example)
//            // In a real application, this would likely be a database query with grouping and sorting
//            Map<Long, Integer> propertyVisitCounts = new HashMap<>();
//
//            for (UserPropertyVisit visit : allVisits) {
//                Long propertyId = visit.getPropertyId();
//                propertyVisitCounts.put(propertyId,
//                        propertyVisitCounts.getOrDefault(propertyId, 0) + visit.getVisitCount());
//            }
//
//            // Get top 5 properties by visit count (simplified example)
//            List<Long> topPropertyIds = propertyVisitCounts.entrySet().stream()
//                    .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
//                    .limit(5)
//                    .map(Map.Entry::getKey)
//                    .toList();
//
//            // Get property details for these IDs
//            List<PropertyResponseDTO> popularProperties = topPropertyIds.stream()
//                    .map(propertyService::getPropertyById)
//                    .toList();
//
//            return ResponseEntity.ok(popularProperties);
//        } catch (Exception e) {
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("error", "Error retrieving popular properties");
//            errorResponse.put("message", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
}