package com.backend.karyanestApplication.Controller;

import com.backblaze.b2.client.exceptions.B2Exception;
import com.backend.karyanestApplication.DTO.*;
import com.backend.karyanestApplication.Repositry.AmenitiesRepository;
import com.backend.karyanestApplication.Service.PropertyService;
import com.example.Authentication.DTO.JWTUserDTO;
import com.example.Authentication.UTIL.JwtUtil;
import com.backend.karyanestApplication.Model.*;
import com.backend.karyanestApplication.Repositry.PropertyPriceChangeRepository;
import com.backend.karyanestApplication.Service.UserPropertyVisitService;
import com.backend.karyanestApplication.Service.UserService;
import com.example.Authentication.Component.UserContext;
import com.example.storageService.Model.FileVersion;
import com.example.storageService.Service.DynamicStorageService;
import com.example.storageService.Service.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST Controller for handling property-related operations.
 * This controller provides endpoints for creating, retrieving, updating, and deleting properties,
 * as well as handling property resources, visits, price changes, and search functionality.
 */
@RestController
@RequestMapping("/v1/props")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserContext userContext;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserPropertyVisitService userPropertyVisitService;
    @Autowired
    private PropertyPriceChangeRepository priceChangeRepository;
    @Autowired
    private DynamicStorageService storageService;
    @Autowired
    private AmenitiesRepository amenitiesRepository;

    /**
     * Retrieves all properties in the system.
     * This endpoint also records property visits for analytical purposes.
     *
     * @param request The HTTP request containing user information
     * @return ResponseEntity containing a list of PropertyDTO objects
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_getAll')")
    @GetMapping
    public ResponseEntity<List<PropertyDTO>> getAllProperties(HttpServletRequest request) {
        List<PropertyDTO> properties = propertyService.getAllProperties();
        // recordPropertyVisit(request, properties);
        return ResponseEntity.ok(properties);
    }

    /**
     * Retrieves a specific property by its ID.
     * This endpoint also records the visit to this specific property.
     *
     * @param id      The ID of the property to retrieve
     * @param request The HTTP request containing user information
     * @return ResponseEntity containing the PropertyDTO for the specified ID
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_getById')")
    @GetMapping("/{id}")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable Long id, HttpServletRequest request) {
        // Get the property details
        PropertyDTO property = propertyService.getPropertyById(id);
        JWTUserDTO user = (JWTUserDTO) request.getAttribute("user");
        String deviceInfo = request.getHeader("User-Agent");
        userPropertyVisitService.recordVisit(user.getUserId(), id, deviceInfo, null);
        return ResponseEntity.ok(property);
    }

    /**
     * Creates a new property with associated resources.
     * The property is associated with the authenticated user derived from the JWT token.
     *
     * @param propertyDTO The property data transfer object containing property details
     * @param request     The HTTP request containing the JWT token
     * @return ResponseEntity containing the created PropertyDTO with HTTP status 201 (Created)
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_create')")
    @PostMapping
    public ResponseEntity<PropertyDTO> addProperty(
            @RequestBody PropertyCreateDTO propertyDTO,
            HttpServletRequest request) {
        String Token = userContext.extractToken(request);
        String Username = jwtUtil.extractUsername(Token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(propertyService.saveOrUpdateDraft(propertyDTO, Username));
    }

    /**
     * Updates an existing property with the provided information.
     *
     * @param id          The ID of the property to update
     * @param propertyDTO The property data transfer object containing updated details
     * @return ResponseEntity containing the updated PropertyDTO
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_getById')")
    @PutMapping("/{id}")
    public ResponseEntity<PropertyDTO> updateProperty(
            @PathVariable Long id,
            @RequestBody PropertyDTO propertyDTO) {
        return ResponseEntity.ok(propertyService.updateProperty(id, propertyDTO));
    }

//
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_addResource')")
//    @PostMapping("/{id}/upload-resource")
//    public ResponseEntity<List<PropertyResourceDTO>> uploadPropertyResource(
//            @PathVariable Long id,
//            @RequestParam("files") MultipartFile[] files,
//            @RequestParam(value = "title", required = false) PropertyResource.ResourceTitle title,
//            @RequestParam(value = "description", required = false) String description) {
//        try {
//            List<PropertyResourceDTO> savedResources = new ArrayList<>();
//            for (MultipartFile file : files) {
//                FileVersion fileVersion = storageService.uploadFile(
//                        file.getOriginalFilename(),
//                        file.getInputStream(),
//                        file.getSize(),
//                        file.getContentType(),
//                        id,
//                        "property"
//                );
//
//                PropertyResourceDTO resourceDTO = new PropertyResourceDTO();
//                resourceDTO.setPropertyId(id);
//
//                // Dynamically decide resource type
//                String contentType = file.getContentType();
//                if (contentType != null && contentType.startsWith("video/")) {
//                    resourceDTO.setResourceType(PropertyResource.ResourceType.Video);
//                } else if (contentType != null && contentType.equals("application/pdf")) {
//                    resourceDTO.setResourceType(PropertyResource.ResourceType.Document);
//                } else {
//                    resourceDTO.setResourceType(PropertyResource.ResourceType.Image);
//                }
//                resourceDTO.setTitle(title != null ? title : PropertyResource.ResourceTitle.Front);
//                resourceDTO.setUrl(fileVersion.getFileName());
//                resourceDTO.setFileId(fileVersion.getFileId());
//                resourceDTO.setDescription(description);
//
//                PropertyResourceDTO savedResource = propertyService.addPropertyResource(id, resourceDTO);
//                savedResources.add(savedResource);
//            }
//            if (savedResources.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body(Collections.emptyList());
//            }
//            return ResponseEntity.ok(savedResources);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Collections.emptyList());
//        }
//    }
//
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_deleteResource')")
//    @DeleteMapping("/{id}/delete-resource/{resourceId}")
//    public ResponseEntity<Map<String, String>> deletePropertyResource(
//            @PathVariable Long id,
//            @PathVariable Long resourceId) {
//        try {
//
//            PropertyResourceDTO resourceDTO = propertyService.getPropertyResourceById(id, resourceId);
//            if (resourceDTO == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(Map.of("error", "Resource not found"));
//            }
//            String fullPath = resourceDTO.getUrl();
//            String fileName = fullPath.substring(fullPath.indexOf("nestero-rootfolder/") + "nestero-rootfolder/".length());
//
//            storageService.deleteFile(
//                    fileName,
//                    resourceDTO.getFileId()
//            );
//
//            propertyService.deletePropertyResource(id, resourceId);
//
//            return ResponseEntity.ok(Map.of("message",resourceDTO.getUrl()));
//        } catch (B2Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Delete failed: " + e.getMessage()));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
@PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_addResource')")
@PostMapping("/{id}/upload-resource")
public ResponseEntity<List<PropertyResourceDTO>> uploadPropertyResource(
        @PathVariable Long id,
        @RequestParam("files") MultipartFile[] files,
        @RequestParam(value = "title", required = false) PropertyResource.ResourceTitle title,
        @RequestParam(value = "description", required = false) String description) {
    try {
        List<PropertyResourceDTO> savedResources = new ArrayList<>();

        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            String context;

            if (contentType != null && contentType.startsWith("video/")) {
                context = "video";
            } else if ("application/pdf".equals(contentType)) {
                context = "document";
            } else if (contentType != null && contentType.startsWith("image/")) {
                context = "property";
            } else {
                context = "property"; // fallback
            }

            FileVersion fileVersion = storageService.uploadFile(
                    file.getOriginalFilename(),
                    file.getInputStream(),
                    file.getSize(),
                    contentType,
                    id,
                    context
            );

            PropertyResourceDTO resourceDTO = new PropertyResourceDTO();
            resourceDTO.setPropertyId(id);

            // Set resourceType based on contentType
            if (contentType != null && contentType.startsWith("video/")) {
                resourceDTO.setResourceType(PropertyResource.ResourceType.Video);
            } else if ("application/pdf".equals(contentType)) {
                resourceDTO.setResourceType(PropertyResource.ResourceType.Document);
            } else {
                resourceDTO.setResourceType(PropertyResource.ResourceType.Image);
            }

            resourceDTO.setTitle(title != null ? title : PropertyResource.ResourceTitle.Front);
            resourceDTO.setUrl(fileVersion.getFileName());
            resourceDTO.setFileId(fileVersion.getFileId());
            resourceDTO.setDescription(description);

            PropertyResourceDTO savedResource = propertyService.addPropertyResource(id, resourceDTO);
            savedResources.add(savedResource);
        }

        if (savedResources.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }

        return ResponseEntity.ok(savedResources);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
}
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_deleteResource')")
    @DeleteMapping("/{id}/delete-resource/{resourceId}")
    public ResponseEntity<Map<String, String>> deletePropertyResource(
            @PathVariable Long id,
            @PathVariable Long resourceId) {
        try {
            PropertyResourceDTO resourceDTO = propertyService.getPropertyResourceById(id, resourceId);
            if (resourceDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Resource not found"));
            }

            String fullPath = resourceDTO.getUrl();
            String fileName = fullPath.substring(fullPath.indexOf("nestaro-rootfolder/") + "nestaro-rootfolder/".length());

            storageService.deleteFile(fileName, resourceDTO.getFileId());
            propertyService.deletePropertyResource(id, resourceId);

            return ResponseEntity.ok(Map.of("message", resourceDTO.getUrl()));
        } catch (B2Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Delete failed: " + e.getMessage()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_getResources')")
    @GetMapping("/{id}/resources")
    public ResponseEntity<List<PropertyResourceDTO>> getPropertyResources(@PathVariable Long id) {
        try {
            List<PropertyResourceDTO> resources = propertyService.getPropertyResourcesByPropertyId(id);
            if (resources.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    /**
     * Adds a new resource to an existing property.
     *
     * @param id                  The ID of the property to add the resource to
     * @param propertyResourceDTO The resource details to add
     * @return ResponseEntity containing the created PropertyResourceDTO
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_addResource')")
    @PostMapping("/resources/{id}")
    public ResponseEntity<PropertyResourceDTO> addPropertyResource(@PathVariable Long id, @RequestBody PropertyResourceDTO propertyResourceDTO) {
        return ResponseEntity.ok(propertyService.addPropertyResource(id, propertyResourceDTO));
    }

    /**
     * Updates an existing property resource or creates a new one if it doesn't exist.
     *
     * @param id                  The ID of the property
     * @param resourcesId         The ID of the resource to update
     * @param propertyResourceDTO The updated resource details
     * @return ResponseEntity containing the updated PropertyResourceDTO
     */
    @PutMapping("/resources/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_UpdateResource')")
    public ResponseEntity<PropertyResourceDTO> updatePropertyResource(@PathVariable Long id, @RequestParam Long resourcesId, @RequestBody PropertyResourceDTO propertyResourceDTO) {
        return ResponseEntity.ok(propertyService.updateOrCreatePropertyResource(id, resourcesId, propertyResourceDTO));
    }

    /**
     * Deletes a property by its ID.
     *
     * @param id The ID of the property to delete
     * @return ResponseEntity containing a success message
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_deleteProps')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProperty(@PathVariable Long id) {
        propertyService.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Property with ID " + id + " has been successfully deleted");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all properties associated with a specific user ID.
     * This endpoint also records property visits for analytical purposes.
     *
     * @param id      The user ID whose properties to retrieve
     * @param request The HTTP request containing user information
     * @return ResponseEntity containing a list of PropertyDTO objects
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_getByUserId')")
    @GetMapping("/user/{id}")
    public ResponseEntity<List<PropertyDTO>> getPropertiesByUserId(@PathVariable Long id, HttpServletRequest request) {
        List<PropertyDTO> properties = propertyService.getPropertiesByUserId(id);
        // recordPropertyVisit(request, properties);
        return ResponseEntity.ok(propertyService.getPropertiesByUserId(id));
    }

    /**
     * Retrieves all properties associated with the authenticated user.
     * This endpoint extracts user information from the JWT token in the request
     * and records property visits for analytical purposes.
     *
     * @param request The HTTP request containing the JWT token
     * @return ResponseEntity containing a list of PropertyDTO objects or an error message
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_currentUser')")
    @GetMapping("/currentUserProperty")
    public ResponseEntity<?> getAllProps(HttpServletRequest request) {

        JWTUserDTO user = (JWTUserDTO) request.getAttribute("user");
        // Fetch properties by user ID
        List<PropertyDTO> properties;
        try {
            properties = propertyService.getPropertiesByUserId(user.getUserId());
            if (properties.isEmpty()) {
                // Return empty list with 200 status if no properties found
                return ResponseEntity.ok(properties);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving properties", "details", e.getMessage()));
        }

        // Return properties with success status
        // recordPropertyVisit(request, properties);
        return ResponseEntity.ok(properties);

    }

    /**
     * Retrieves all visit records for a specific property.
     *
     * @param id The ID of the property to get visits for
     * @return ResponseEntity containing a list of UserPropertyVisit objects or an error message
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('property_visitByID')")
    @GetMapping("/visits/{id}")
    public ResponseEntity<?> getPropertyVisits(@PathVariable Long id) {
        try {
            List<UserPropertyVisit> visits = userPropertyVisitService.getVisitsByPropertyId(id);
            if (visits.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(visits);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving property visits", "details", e.getMessage()));
        }
    }

    /**
     * Retrieves visit statistics for a specific property, including total visits and unique visitors.
     *
     * @param id The ID of the property to get visit statistics for
     * @return ResponseEntity containing visit statistics or an error message
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('property_visitCount')")
    @GetMapping("/visits_count/{id}")
    public ResponseEntity<?> getPropertyVisitCount(@PathVariable Long id) {
        try {
            List<UserPropertyVisit> visits = userPropertyVisitService.getVisitsByPropertyId(id);

            // Calculate total visits (sum of all visit counts)
//            int totalVisits = visits.stream()
//                    .mapToInt(UserPropertyVisit::getVisitCount)
//                    .sum();

            // Count unique visitors
            int uniqueVisitors = visits.size();

            Map<String, Object> response = new HashMap<>();
            response.put("propertyId", id);
//            response.put("totalVisits", totalVisits);
            response.put("uniqueVisitors", uniqueVisitors);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error calculating property visit statistics", "details", e.getMessage()));
        }
    }

    /**
     * Manually records a visit to a property with additional metadata.
     * This endpoint requires authentication via a JWT token.
     *
     * @param id        The ID of the property being visited
     * @param visitData Additional metadata about the visit (device info, location)
     * @param request   The HTTP request containing the JWT token
     * @return ResponseEntity containing the created UserPropertyVisit or an error message
     */

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/record-visit/{id}")
    public ResponseEntity<?> manuallyRecordVisit(
            @PathVariable Long id,
            @RequestBody Map<String, Object> visitData,
            HttpServletRequest request) {
        try {
            JWTUserDTO user = (JWTUserDTO) request.getAttribute("user");

            // Get optional visit data
            String deviceInfo = visitData.containsKey("deviceInfo") ?
                    visitData.get("deviceInfo").toString() : null;
            String locationCoords = visitData.containsKey("locationCoords") ?
                    visitData.get("locationCoords").toString() : null;

            // Record the visit
            UserPropertyVisit visit = userPropertyVisitService.recordVisit(user.getUserId(), id, deviceInfo, locationCoords);

            return ResponseEntity.status(HttpStatus.CREATED).body(visit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error recording property visit", "details", e.getMessage()));
        }
    }

    /**
     * Updates the price of a property and records the price change history.
     * This endpoint requires authentication via a JWT token.
     *
     * @param id       The ID of the property to update
     * @param priceMap Map containing the new price
     * @param request  The HTTP request containing the JWT token
     * @return ResponseEntity containing the updated PropertyDTO or an error message
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('property_changePrice')")
    @PatchMapping("/ChangePrice/{id}")
    public ResponseEntity<?> updatePrice(@PathVariable Long id, @RequestBody Map<String, BigDecimal> priceMap, HttpServletRequest request) {
        try {
            // Extract user from JWT token
            String token = userContext.extractToken(request);
            String username = jwtUtil.extractUsername(token);
            User user = userService.getUserByUsername(username);

            // Validate input
            BigDecimal newPrice = priceMap.get("price");
            if (newPrice == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Price is required"));
            }

            // Get the property
            Property property = propertyService.findPropertyEntityById(id);
            if (property == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Property not found with id: " + id));
            }

            // Store the old price before updating
            BigDecimal oldPrice = property.getPrice();

            // Check if old price exists and if it differs from the new price
            boolean priceChanged = (oldPrice == null) || !oldPrice.equals(newPrice);

            if (priceChanged) {
                // Update property through service (which should handle price change recording)
                PropertyDTO updatedPropertyDTO = propertyService.updatePropertyPriceOnly(newPrice, property, oldPrice, user);
                return ResponseEntity.ok(updatedPropertyDTO);
            }

            return ResponseEntity.ok(propertyService.convertToResponseDTO(property));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update property price: " + e.getMessage()));
        }
    }


    /**
     * Retrieves the price change history for all properties.
     *
     * @return ResponseEntity containing a list of price change records
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('property_priceHistory')")
    @GetMapping("/price_history")
    public ResponseEntity<List<Map<String, Object>>> priceChangeHistory() {
        try {
            List<PropertyPriceChange> propertyPriceChanges = priceChangeRepository.findAll();

            if (propertyPriceChanges.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
            }

            // Convert to list of maps with only specific attributes
            List<Map<String, Object>> result = propertyPriceChanges.stream()
                    .map(change -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", change.getId());
                        item.put("created_at", change.getCreatedAt());
                        item.put("new_price", change.getNewPrice());
                        item.put("old_price", change.getOldPrice());
                        item.put("property_id", change.getProperty().getId());
                        item.put("Price changed By", change.getUser().getFullName());
                        // Just get the ID
                        return item;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    /**
     * Retrieves a specific price change record by its ID.
     *
     * @param id      The ID of the price change record to retrieve
     * @param request The HTTP request
     * @return ResponseEntity containing the price change record or null if not found
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('property_priceHistoryByID')")
    @GetMapping("/price_history/{id}")
    public ResponseEntity<Map<String, Object>> priceChangeHistoryById(@PathVariable Long id, HttpServletRequest request) {
        try {
            return priceChangeRepository.findByPropertyId(id)
                    .map(change -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", change.getId());
                        item.put("created_at", change.getCreatedAt());
                        item.put("new_price", change.getNewPrice());
                        item.put("old_price", change.getOldPrice());
                        item.put("property_id", change.getProperty().getId());
                        item.put("Price changed By", change.getUser().getFullName());
                        // Just get the ID

                        return ResponseEntity.ok(item);
                    })
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Searches for properties based on the given search criteria.
     *
     * @param searchRequest The search criteria DTO containing filter parameters
     * @return ResponseEntity containing a list of PropertyDTO objects matching the search criteria
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('property_search')")
    @PostMapping("/search")
    public ResponseEntity<List<PropertyDTO>> searchProperties(@RequestBody PropertySearchRequestDTO searchRequest) {
        List<PropertyDTO> properties = propertyService.searchProperties(searchRequest);
        return ResponseEntity.ok(properties);
    }

    /**
     * @param id The ID of the property
     * @return ResponseEntity containing the amenities text or error message
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_getAmenities')")
    @GetMapping("/{id}/amenities")
    public ResponseEntity<?> getPropertyAmenities(@PathVariable Long id) {
        try {
            PropertyDTO property = propertyService.getPropertyById(id);
            if (property == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Property not found with id: " + id));
            }
            List<AmenitiesResponseDTO> response = property.getAmenitiesResponseDTOS();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving amenities", "details", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_addAmenities')")
    @PostMapping("/{id}/add-amenities-only")
    public ResponseEntity<?> addAmenitiesOnly(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestData) {
        try {
            String amenities = requestData.get("amenities");
            if (amenities == null || amenities.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Amenities cannot be empty"));
            }

            return ResponseEntity.ok(propertyService.processAmenities(id, amenities.trim(), false));

        } catch (RuntimeException e) {
            return handleAmenityError(e);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_updateAmenities')")
    @PutMapping("/{id}/amenities")
    public ResponseEntity<?> updatePropertyAmenities(
            @PathVariable Long id,
            @RequestBody Map<String, String> amenitiesData) {
        try {
            String amenities = amenitiesData.get("amenities");
            if (amenities == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Amenities field is required"));
            }

            return ResponseEntity.ok(propertyService.processAmenities(id, amenities.trim(), true));

        } catch (RuntimeException e) {
            return handleAmenityError(e);
        }
    }

    private ResponseEntity<Map<String, String>> handleAmenityError(RuntimeException e) {
        if (e.getMessage().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error processing amenities", "details", e.getMessage()));
    }
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('props_deleteAmenities')")
    @DeleteMapping("/amenities/{amenities_id}")
    public ResponseEntity<?> deleteAmenityById(@PathVariable Long amenities_id) {
        try {
            AmenitiesResponseDTO deleted = propertyService.deleteAmenityById(amenities_id);
            return ResponseEntity.ok(Map.of(
                    "message", "Amenity deleted successfully",
                    "deletedAmenity", deleted
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error deleting amenity", "details", e.getMessage()));
        }
    }

}