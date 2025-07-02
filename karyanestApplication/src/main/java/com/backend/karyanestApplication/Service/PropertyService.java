package com.backend.karyanestApplication.Service;

import com.backend.karyanestApplication.DTO.*;
import com.backend.karyanestApplication.Model.*;
import com.backend.karyanestApplication.Repositry.*;
import com.example.module_b.ExceptionAndExceptionHandler.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class PropertyService {
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PropertyPriceChangeRepository priceChangeRepository;
    @Autowired
    private PropertyResourcesRepository propertyResourcesRepository;
    @Autowired
    private AmenitiesRepository amenitiesRepository;

    @Transactional
    public List<PropertyDTO> getAllProperties() {
        List<com.backend.karyanestApplication.Model.Property> properties = propertyRepository.findAll();
        List<PropertyResource> propertyResources = propertyResourcesRepository.findAll();
        List<Amenities> amenities = amenitiesRepository.findAll();
        return convertToResponseDTOList(properties, propertyResources, amenities);
    }

    @Transactional
    public PropertyDTO updateProperty(Long propertyId, PropertyDTO propertyDTO) {
        Property existingProperty = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new CustomException("Property not found."));
        updatePropertyFields(existingProperty, propertyDTO);
        Property savedProperty = propertyRepository.save(existingProperty);

        // Store amenities string in extraFields if provided
        PropertyDTO responseDTO = new PropertyDTO(savedProperty);
        if (propertyDTO.getAmenities() != null && !propertyDTO.getAmenities().isEmpty()) {
            responseDTO.set("amenities", propertyDTO.getAmenities());
        }

        if (propertyDTO.getPropertyResources() != null) {
            propertyResourcesRepository.deleteByPropertyId(propertyId);
            savePropertyResources(savedProperty, propertyDTO.getPropertyResources());
        }

        if (propertyDTO.getAmenitiesResponseDTOS() != null) {
            amenitiesRepository.deleteByPropertyId(propertyId);
            saveAmenitiesFromDTO(propertyDTO, savedProperty);
        }

        return convertToResponseDTO(savedProperty);
    }

    @Transactional
    public PropertyDTO saveOrUpdateDraft(PropertyCreateDTO propertyDTO, String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new CustomException("User not found. Cannot save draft.");
        }
        Property property;
        Long draftId = propertyDTO.getDraftId();

        if (draftId != null) {
            property = propertyRepository.findById(draftId)
                    .orElseThrow(() -> new CustomException("Draft not found with id: " + draftId));
            updateNonNullFields(propertyDTO, property);
        } else {
            property = new Property();
            mapPropertyDTOToEntity(propertyDTO, property);
            property.setStatus(Property.Status.DRAFT);
            property.setUser(user);
            property.setCurrency("INR");
            property.setCountry("India");
        }

        Property savedProperty = propertyRepository.save(property);
        saveAmenities(propertyDTO, savedProperty);

        // Store amenities string in extraFields if provided
        PropertyDTO responseDTO = new PropertyDTO(savedProperty);
        if (propertyDTO.getAmenities() != null && !propertyDTO.getAmenities().isEmpty()) {
            responseDTO.set("amenities", propertyDTO.getAmenities());
        }

        return convertToResponseDTO(savedProperty);
    }

    @Transactional(readOnly = true)
    public PropertyDTO convertToResponseDTO(Property property) {
        PropertyDTO responseDTO = new PropertyDTO(property);

        // Attach Property Resources
        List<PropertyResource> resources = propertyResourcesRepository.findByPropertyId(property.getId());
        responseDTO.setPropertyResources(
                resources.stream().map(PropertyResourceDTO::new).collect(Collectors.toList())
        );

        // Attach Amenities as amenitiesResponseDTOS
        List<Amenities> amenities = amenitiesRepository.findByPropertyId(property.getId());
        List<AmenitiesResponseDTO> amenitiesResponseDTOS = amenities.stream()
                .map(AmenitiesResponseDTO::convertToResponseDTO)
                .collect(Collectors.toList());
        responseDTO.setAmenitiesResponseDTOS(amenitiesResponseDTOS);

        // Set amenities field as comma-separated string from amenitiesResponseDTOS
        if (!amenitiesResponseDTOS.isEmpty()) {
            String amenitiesString = amenitiesResponseDTOS.stream()
                    .map(AmenitiesResponseDTO::getAmenities)
                    .filter(amenity -> amenity != null && !amenity.isEmpty())
                    .collect(Collectors.joining(","));
            responseDTO.setAmenities(amenitiesString.isEmpty() ? null : amenitiesString);
            responseDTO.set("amenities", amenitiesString.isEmpty() ? null : amenitiesString);
        } else if (responseDTO.getExtraFields().containsKey("amenities")) {
            // Use amenities from extraFields if no amenitiesResponseDTOS
            responseDTO.setAmenities((String) responseDTO.getExtraFields().get("amenities"));
        } else {
            responseDTO.setAmenities(null);
        }

        // Show owner details only if property is AVAILABLE or RENTED
        if (property.getStatus() == Property.Status.AVAILABLE || property.getStatus() == Property.Status.RENTED) {
            Optional<User> userOpt = userRepo.findById(property.getUser().getId());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                responseDTO.set("ownerId", user.getId());
                responseDTO.set("propertyOwner", user.getFullName());
                responseDTO.set("profileImageUrl", user.getProfilePicture());
            } else {
                responseDTO.set("ownerId", null);
                responseDTO.set("propertyOwner", "Owner details are hidden.");
                responseDTO.set("profileImageUrl", null);
            }
        } else {
            responseDTO.set("ownerId", null);
            responseDTO.set("propertyOwner", "Owner details are hidden for draft properties.");
            responseDTO.set("profileImageUrl", null);
        }

        return responseDTO;
    }

    /**
     * Helper method to save property resources.
     */
    private void savePropertyResources(com.backend.karyanestApplication.Model.Property property, List<PropertyResourceDTO> resourceDTOs) {
        if (resourceDTOs != null && !resourceDTOs.isEmpty()) {
            for (PropertyResourceDTO resourceDTO : resourceDTOs) {
                PropertyResource resource = new PropertyResource();
                resource.setPropertyId(property.getId());
                resource.setResourceType(resourceDTO.getResourceType());
                resource.setTitle(resourceDTO.getTitle());
                resource.setUrl(resourceDTO.getUrl());
                resource.setFileId(resourceDTO.getFileId());
                resource.setDescription(resourceDTO.getDescription());
                resource.setFileSize(resourceDTO.getFileSize());
                resource.setFileType(resourceDTO.getFileType());
                resource.setSortOrder(resourceDTO.getSortOrder());

                propertyResourcesRepository.save(resource);
            }
        }
    }

    private void saveAmenitiesFromDTO(PropertyDTO propertyDTO, Property property) {
        if (propertyDTO.getAmenitiesResponseDTOS() != null && !propertyDTO.getAmenitiesResponseDTOS().isEmpty()) {
//            amenitiesRepository.deleteByPropertyId(property.getId());
            for (AmenitiesResponseDTO amenitiesDTO : propertyDTO.getAmenitiesResponseDTOS()) {
                Amenities amenities = new Amenities();
                amenities.setPropertyId(property.getId());
                amenities.setAmenities(amenitiesDTO.getAmenities());
                amenitiesRepository.save(amenities);
            }
        }
    }

    private void saveAmenities(PropertyCreateDTO propertyDTO, Property property) {
        if (propertyDTO == null || property == null || property.getId() == null) {
            return;
        }

//        amenitiesRepository.deleteByPropertyId(property.getId());
        String amenitiesString = propertyDTO.getAmenities();
        if (amenitiesString != null && !amenitiesString.trim().isEmpty()) {
            List<String> amenitiesList = Arrays.stream(amenitiesString.split("\\s*,\\s*"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            List<Amenities> amenitiesToSave = amenitiesList.stream()
                    .map(amenity -> {
                        Amenities newAmenity = new Amenities();
                        newAmenity.setPropertyId(property.getId());
                        newAmenity.setAmenities(amenity);
                        return newAmenity;
                    })
                    .collect(Collectors.toList());

            amenitiesRepository.saveAll(amenitiesToSave);
        }
    }

    private void updatePropertyFields(Property property, PropertyDTO requestDTO) {
        if (requestDTO.getTitle() != null) property.setTitle(requestDTO.getTitle());
        if (requestDTO.getDescription() != null) property.setDescription(requestDTO.getDescription());
        if (requestDTO.getPropertyType() != null) property.setPropertyType(requestDTO.getPropertyType());
        if (requestDTO.getStatus() != null) property.setStatus(requestDTO.getStatus());
        if (requestDTO.getListingType() != null) property.setListingType(requestDTO.getListingType());
        if (requestDTO.getPrice() != null) property.setPrice(requestDTO.getPrice());
        if (requestDTO.getCurrency() != null) property.setCurrency(requestDTO.getCurrency());
        if (requestDTO.getAreaSize() != null) property.setAreaSize(requestDTO.getAreaSize());
        if (requestDTO.getAreaUnit() != null) property.setAreaUnit(requestDTO.getAreaUnit());
        if (requestDTO.getBedrooms() != null) property.setBedrooms(requestDTO.getBedrooms());
        if (requestDTO.getBathrooms() != null) property.setBathrooms(requestDTO.getBathrooms());
        if (requestDTO.getBalconies() != null) property.setBalconies(requestDTO.getBalconies());
        if (requestDTO.getFurnishedStatus() != null) property.setFurnishedStatus(requestDTO.getFurnishedStatus());
        if (requestDTO.getParkingSpaces() != null) property.setParkingSpaces(requestDTO.getParkingSpaces());
        if (requestDTO.getFloorNumber() != null) property.setFloorNumber(requestDTO.getFloorNumber());
        if (requestDTO.getTotalFloors() != null) property.setTotalFloors(requestDTO.getTotalFloors());
        if (requestDTO.getOwnershipType() != null) property.setOwnershipType(requestDTO.getOwnershipType());
        if (requestDTO.getAgeOfProperty() != null) property.setAgeOfProperty(requestDTO.getAgeOfProperty());
        if (requestDTO.getConstructionStatus() != null)
            property.setConstructionStatus(requestDTO.getConstructionStatus());
        if (requestDTO.getFacingDirection() != null) property.setFacingDirection(requestDTO.getFacingDirection());
        if (requestDTO.getRoadWidth() != null) property.setRoadWidth(requestDTO.getRoadWidth());
        if (requestDTO.getWaterAvailability() != null) property.setWaterAvailability(requestDTO.getWaterAvailability());
        if (requestDTO.getElectricityAvailability() != null)
            property.setElectricityAvailability(requestDTO.getElectricityAvailability());
        if (requestDTO.getSecurityFeatures() != null) property.setSecurityFeatures(requestDTO.getSecurityFeatures());
        if (requestDTO.getNearbyLandmarks() != null) property.setNearbyLandmarks(requestDTO.getNearbyLandmarks());
        if (requestDTO.getLocationAddress() != null) property.setLocationAddress(requestDTO.getLocationAddress());
        if (requestDTO.getCity() != null) property.setCity(requestDTO.getCity());
        if (requestDTO.getState() != null) property.setState(requestDTO.getState());
        if (requestDTO.getCountry() != null) property.setCountry(requestDTO.getCountry());
        if (requestDTO.getPincode() != null) property.setPincode(requestDTO.getPincode());
        if (requestDTO.getLatitude() != null) property.setLatitude(requestDTO.getLatitude());
        if (requestDTO.getLongitude() != null) property.setLongitude(requestDTO.getLongitude());
        if (requestDTO.getVerificationStatus() != null) property.setVerificationStatus(requestDTO.getVerificationStatus());
        if (requestDTO.getElectricityAvailability() != null) property.setElectricityAvailability(requestDTO.getElectricityAvailability());
        if (requestDTO.getInternetAvailability() != null) property.setInternetAvailability(requestDTO.getInternetAvailability());
        if (requestDTO.getGasAvailability() != null) property.setGasAvailability(requestDTO.getGasAvailability());
        if (requestDTO.getSewageAvailability() != null) property.setSewageAvailability(requestDTO.getSewageAvailability());
        if (requestDTO.getPublicTransportAvailable() != null) property.setPublicTransportAvailable(requestDTO.getPublicTransportAvailable());
        if (requestDTO.getHighFootTraffic() != null) property.setHighFootTraffic(requestDTO.getHighFootTraffic());
        if (requestDTO.getAvailableLeaseArea() != null) property.setAvailableLeaseArea(requestDTO.getAvailableLeaseArea());
        if (requestDTO.getStreetFrontage() != null) property.setStreetFrontage(requestDTO.getStreetFrontage());
        if (requestDTO.getZoningClassification() != null) property.setZoningClassification(requestDTO.getZoningClassification());
        if (requestDTO.getAccessibilityFeatures() != null) property.setAccessibilityFeatures(requestDTO.getAccessibilityFeatures());

    }

    private void mapPropertyDTOToEntity(PropertyCreateDTO dto, Property property) {
        if (dto.getTitle() != null) property.setTitle(dto.getTitle());
        if (dto.getPropertyType() != null) property.setPropertyType(dto.getPropertyType());
        if (dto.getLocationAddress() != null) property.setLocationAddress(dto.getLocationAddress());
        if (dto.getCity() != null) property.setCity(dto.getCity());
        if (dto.getState() != null) property.setState(dto.getState());
        if (dto.getCountry() != null) property.setCountry(dto.getCountry());
        if (dto.getPincode() != null) property.setPincode(dto.getPincode());
        if (dto.getPrice() != null) property.setPrice(dto.getPrice());
        if (dto.getAreaSize() != null) property.setAreaSize(dto.getAreaSize());
        if (dto.getAreaUnit() != null) property.setAreaUnit(dto.getAreaUnit());
        if (dto.getBedrooms() != null) property.setBedrooms(dto.getBedrooms());
        if (dto.getBathrooms() != null) property.setBathrooms(dto.getBathrooms());
        if (dto.getDescription() != null) property.setDescription(dto.getDescription());
        if (dto.getAgeOfProperty() != null) property.setAgeOfProperty(dto.getAgeOfProperty());
        if (dto.getFloorNumber() != null) property.setFloorNumber(dto.getFloorNumber());
        if (dto.getTotalFloors() != null) property.setTotalFloors(dto.getTotalFloors());
        if (dto.getFurnishedStatus() != null) property.setFurnishedStatus(dto.getFurnishedStatus());
        if (dto.getFacingDirection() != null) property.setFacingDirection(dto.getFacingDirection());
        if (dto.getNearbyLandmarks() != null) property.setNearbyLandmarks(dto.getNearbyLandmarks());
        if (dto.getOwnershipType() != null) property.setOwnershipType(dto.getOwnershipType());
        if (dto.getConstructionStatus() != null) property.setConstructionStatus(dto.getConstructionStatus());
        // ✅ Additional fields
        if (dto.getRoadWidth() != null) property.setRoadWidth(dto.getRoadWidth());
        if (dto.getWaterAvailability() != null) property.setWaterAvailability(dto.getWaterAvailability());
        if (dto.getElectricityAvailability() != null) property.setElectricityAvailability(dto.getElectricityAvailability());
        if (dto.getInternetAvailability() != null) property.setInternetAvailability(dto.getInternetAvailability());
        if (dto.getGasAvailability() != null) property.setGasAvailability(dto.getGasAvailability());
        if (dto.getSewageAvailability() != null) property.setSewageAvailability(dto.getSewageAvailability());
        if (dto.getPublicTransportAvailable() != null) property.setPublicTransportAvailable(dto.getPublicTransportAvailable());
        if (dto.getHighFootTraffic() != null) property.setHighFootTraffic(dto.getHighFootTraffic());
        if (dto.getAvailableLeaseArea() != null) property.setAvailableLeaseArea(dto.getAvailableLeaseArea());
        if (dto.getStreetFrontage() != null) property.setStreetFrontage(dto.getStreetFrontage());
        if (dto.getZoningClassification() != null) property.setZoningClassification(dto.getZoningClassification());
        if (dto.getAccessibilityFeatures() != null) property.setAccessibilityFeatures(dto.getAccessibilityFeatures());
    }


    private void updateNonNullFields(PropertyCreateDTO dto, Property property) {
        if (dto.getDraftId() != null) property.setId(dto.getDraftId());
        if (dto.getTitle() != null) property.setTitle(dto.getTitle());
        if (dto.getPropertyType() != null) property.setPropertyType(dto.getPropertyType());
        if (dto.getLocationAddress() != null) property.setLocationAddress(dto.getLocationAddress());
        if (dto.getCity() != null) property.setCity(dto.getCity());
        if (dto.getState() != null) property.setState(dto.getState());
        if (dto.getCountry() != null) property.setCountry(dto.getCountry());
        if (dto.getPincode() != null) property.setPincode(dto.getPincode());
        if (dto.getPrice() != null) property.setPrice(dto.getPrice());
        if (dto.getAreaSize() != null) property.setAreaSize(dto.getAreaSize());
        if (dto.getAreaUnit() != null) property.setAreaUnit(dto.getAreaUnit());
        if (dto.getBedrooms() != null) property.setBedrooms(dto.getBedrooms());
        if (dto.getBathrooms() != null) property.setBathrooms(dto.getBathrooms());
        if (dto.getDescription() != null) property.setDescription(dto.getDescription());
        if (dto.getAgeOfProperty() != null) property.setAgeOfProperty(dto.getAgeOfProperty());
        if (dto.getFloorNumber() != null) property.setFloorNumber(dto.getFloorNumber());
        if (dto.getTotalFloors() != null) property.setTotalFloors(dto.getTotalFloors());
        if (dto.getFurnishedStatus() != null) property.setFurnishedStatus(dto.getFurnishedStatus());
        if (dto.getFacingDirection() != null) property.setFacingDirection(dto.getFacingDirection());
        if (dto.getNearbyLandmarks() != null) property.setNearbyLandmarks(dto.getNearbyLandmarks());
        if (dto.getOwnershipType() != null) property.setOwnershipType(dto.getOwnershipType());
        if (dto.getConstructionStatus() != null) property.setConstructionStatus(dto.getConstructionStatus());
        if (dto.getRoadWidth() != null) property.setRoadWidth(dto.getRoadWidth());
        if (dto.getWaterAvailability() != null) property.setWaterAvailability(dto.getWaterAvailability());
        if (dto.getElectricityAvailability() != null) property.setElectricityAvailability(dto.getElectricityAvailability());
        if (dto.getInternetAvailability() != null) property.setInternetAvailability(dto.getInternetAvailability());
        if (dto.getGasAvailability() != null) property.setGasAvailability(dto.getGasAvailability());
        if (dto.getSewageAvailability() != null) property.setSewageAvailability(dto.getSewageAvailability());
        if (dto.getPublicTransportAvailable() != null) property.setPublicTransportAvailable(dto.getPublicTransportAvailable());
        if (dto.getHighFootTraffic() != null) property.setHighFootTraffic(dto.getHighFootTraffic());
        if (dto.getAvailableLeaseArea() != null) property.setAvailableLeaseArea(dto.getAvailableLeaseArea());
        if (dto.getStreetFrontage() != null) property.setStreetFrontage(dto.getStreetFrontage());
        if (dto.getZoningClassification() != null) property.setZoningClassification(dto.getZoningClassification());
        if (dto.getAccessibilityFeatures() != null) property.setAccessibilityFeatures(dto.getAccessibilityFeatures());

    }

    @Transactional(readOnly = true)
    public PropertyDTO getPropertyById(Long propertyId) {
        com.backend.karyanestApplication.Model.Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new CustomException("Property Not Found"));

        return convertToResponseDTO(property);
    }

    @Transactional
    public void deleteById(Long id) {
        com.backend.karyanestApplication.Model.Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new CustomException("Property Not Found With This Id:"));
        property.setStatus(com.backend.karyanestApplication.Model.Property.Status.DELETED);
        propertyRepository.save(property);
    }

    // ✅ Update property price and record price change
    @Transactional
    public PropertyDTO updatePropertyPriceOnly(BigDecimal newPrice, com.backend.karyanestApplication.Model.Property property, BigDecimal oldPrice, User user) {
        property.setPrice(newPrice);
        com.backend.karyanestApplication.Model.Property property1 = propertyRepository.save(property);
        PropertyDTO updatedPropertyDTO = convertToResponseDTO(property1);
        // Record price change
        PropertyPriceChange priceChange = new PropertyPriceChange();
        priceChange.setProperty(property);
        priceChange.setOldPrice(oldPrice != null ? oldPrice : BigDecimal.ZERO);
        priceChange.setNewPrice(newPrice);
        priceChange.setUser(user);
        priceChangeRepository.save(priceChange);
        return updatedPropertyDTO;
    }

    @Transactional
    public PropertyResourceDTO addPropertyResource(Long propertyId, PropertyResourceDTO resourceDTO) {
        // Fetch property or throw an exception
        com.backend.karyanestApplication.Model.Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new CustomException("Property Not Found"));

        // Create new property resource
        PropertyResource resource = new PropertyResource();
        resource.setPropertyId(property.getId());
        resource.setResourceType(resourceDTO.getResourceType());
        resource.setTitle(resourceDTO.getTitle());
        resource.setUrl(resourceDTO.getUrl());
        resource.setFileId(resourceDTO.getFileId());
        resource.setDescription(resourceDTO.getDescription());
        resource.setFileSize(resourceDTO.getFileSize());
        resource.setFileType(resourceDTO.getFileType());
        resource.setSortOrder(resourceDTO.getSortOrder());

        // Save the resource
        PropertyResource savedResource = propertyResourcesRepository.save(resource);

        return convertToResourceResponseDTO(savedResource);
    }

    @Transactional
    public List<PropertyResourceDTO> getPropertyResourcesByPropertyId(Long propertyId) {
        List<PropertyResource> resources = propertyResourcesRepository.findByPropertyId(propertyId);
        return resources.stream()
                .map(PropertyResourceDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePropertyResource(Long propertyId, Long resourceId) {
        PropertyResource resource = propertyResourcesRepository.findByIdAndPropertyId(resourceId, propertyId)
                .orElseThrow(() -> new CustomException("Resource not found for property id: " + propertyId));
        propertyResourcesRepository.delete(resource);
    }

    private PropertyResourceDTO convertToResourceResponseDTO(PropertyResource updatedResource) {
        // Map property resources
        return new PropertyResourceDTO(updatedResource);
    }

    @Transactional
    public PropertyResourceDTO updateOrCreatePropertyResource(Long propertyId, Long resourceId, PropertyResourceDTO resourceDTO) {
        // ✅ Check if property exists
        com.backend.karyanestApplication.Model.Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new CustomException("No property found for the given property ID, so we cannot update resources."));

        // ✅ Try to fetch existing resource
        PropertyResource existingResource = propertyResourcesRepository.findById(resourceId).orElse(null);

        // ✅ If resource not found, create a new one
        if (existingResource == null) {
            PropertyResource newResource = new PropertyResource();
            newResource.setPropertyId(property.getId());
            newResource.setResourceType(resourceDTO.getResourceType());
            newResource.setTitle(resourceDTO.getTitle());
            newResource.setUrl(resourceDTO.getUrl());
            newResource.setFileId(resourceDTO.getFileId());
            newResource.setDescription(resourceDTO.getDescription());
            newResource.setFileSize(resourceDTO.getFileSize());
            newResource.setFileType(resourceDTO.getFileType());
            newResource.setSortOrder(resourceDTO.getSortOrder());

            PropertyResource savedResource = propertyResourcesRepository.save(newResource);
            return convertToResourceResponseDTO(savedResource);
        }


        // ✅ Ensure resource belongs to the correct property
        if (!existingResource.getPropertyId().equals(property.getId())) {
            throw new CustomException("Resource does not belong to the specified property");
        }

        // ✅ Update only non-null fields
        if (resourceDTO.getResourceType() != null) {
            existingResource.setResourceType(resourceDTO.getResourceType());
        }
        if (resourceDTO.getTitle() != null) {
            existingResource.setTitle(resourceDTO.getTitle());
        }
        if (resourceDTO.getUrl() != null) {
            existingResource.setUrl(resourceDTO.getUrl());
        }
        if (resourceDTO.getFileId() != null) {
            existingResource.setFileId(resourceDTO.getFileId());
        }
        if (resourceDTO.getDescription() != null) {
            existingResource.setDescription(resourceDTO.getDescription());
        }
        if (resourceDTO.getFileSize() != null) {
            existingResource.setFileSize(resourceDTO.getFileSize());
        }
        if (resourceDTO.getFileType() != null) {
            existingResource.setFileType(resourceDTO.getFileType());
        }
        if (resourceDTO.getSortOrder() != null) {
            existingResource.setSortOrder(resourceDTO.getSortOrder());
        }
        // ✅ Save updated resource
        PropertyResource updatedResource = propertyResourcesRepository.save(existingResource);
        return convertToResourceResponseDTO(updatedResource);
    }

    @Transactional
    public PropertyResourceDTO getPropertyResourceById(Long propertyId, Long resourceId) {
        PropertyResource resource = propertyResourcesRepository.findByIdAndPropertyId(resourceId, propertyId)
                .orElseThrow(() -> new CustomException("Resource not found for property id: " + propertyId));
        return new PropertyResourceDTO(resource);
    }

    @Transactional(readOnly = true)
    public com.backend.karyanestApplication.Model.Property findPropertyEntityById(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new CustomException("Property Not Found"));
    }

    @Transactional(readOnly = true)
    public List<PropertyDTO> getPropertiesByUserId(Long userId) {
        List<com.backend.karyanestApplication.Model.Property> properties = propertyRepository.findByUserId(userId);
        if (properties.isEmpty()) {
            throw new CustomException("No properties found for this user.");
        }
        return convertToResponseDTOList(properties, propertyResourcesRepository.findAll(), amenitiesRepository.findAll());
    }

    @Transactional
    public boolean existsById(Long id) {
        return propertyRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public List<PropertyDTO> convertToResponseDTOList(List<Property> properties,
                                                      List<PropertyResource> propertyResources,
                                                      List<Amenities> amenities) {
        if (properties == null) {
            return Collections.emptyList();
        }

        return properties.stream()
                .filter(property -> property != null &&
                        (property.getStatus() == Property.Status.AVAILABLE ||
                                property.getStatus() == Property.Status.RENTED))
                .map(property -> {
                    PropertyDTO responseDTO = convertToResponseDTO(property);

                    // Filter and attach matching property resources
                    List<PropertyResourceDTO> matchedResources = (propertyResources == null)
                            ? Collections.emptyList()
                            : propertyResources.stream()
                            .filter(resource -> resource != null &&
                                    resource.getPropertyId() != null &&
                                    resource.getPropertyId().equals(property.getId()))
                            .map(PropertyResourceDTO::new)
                            .collect(Collectors.toList());
                    responseDTO.setPropertyResources(matchedResources);

                    // Filter and attach matching amenities
                    List<AmenitiesResponseDTO> matchedAmenities = (amenities == null)
                            ? Collections.emptyList()
                            : amenities.stream()
                            .filter(amenity -> amenity != null &&
                                    amenity.getPropertyId() != null &&
                                    amenity.getPropertyId().equals(property.getId()))
                            .map(AmenitiesResponseDTO::convertToResponseDTO)
                            .collect(Collectors.toList());
                    responseDTO.setAmenitiesResponseDTOS(matchedAmenities);

                    return responseDTO;
                })
                .collect(Collectors.toList());
    }

    // ✅ Search properties based on criteria
    @Transactional
    public List<PropertyDTO> searchProperties(PropertySearchRequestDTO dto) {
        List<com.backend.karyanestApplication.Model.Property> properties = propertyRepository.searchProperties(
                dto.getMinPrice(),
                dto.getMaxPrice(),
                dto.getPropertyType() != null ? dto.getPropertyType().name() : null,
                dto.getListingType() != null ? dto.getListingType().name() : null,
                dto.getLocationAddress(),
                dto.getAmenities(),
                dto.getBedrooms(),
                dto.getBathrooms()
        );
        return convertToResponseDTOList(properties, propertyResourcesRepository.findAll(), amenitiesRepository.findAll());
    }
    @Transactional
    public List<AmenitiesResponseDTO> processAmenities(Long propertyId, String amenitiesString, boolean replaceAll) {
        if (propertyId == null || amenitiesString == null || amenitiesString.trim().isEmpty()) {
            throw new IllegalArgumentException("Property ID and amenities must not be null or empty");
        }

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property with ID " + propertyId + " not found"));

        List<String> amenitiesList = Arrays.stream(amenitiesString.split("\\s*,\\s*"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        if (replaceAll) {
            // 🧹 Replace mode: delete all previous
            amenitiesRepository.deleteByPropertyId(propertyId);
        }

        // 📌 Get current after possible delete
        Set<String> existing = amenitiesRepository.findByPropertyId(propertyId).stream()
                .map(Amenities::getAmenities)
                .collect(Collectors.toSet());

        List<Amenities> toSave = amenitiesList.stream()
                .filter(amenity -> replaceAll || !existing.contains(amenity))
                .map(amenity -> {
                    Amenities a = new Amenities();
                    a.setPropertyId(propertyId);
                    a.setAmenities(amenity);
                    return a;
                })
                .toList();

        if (!toSave.isEmpty()) {
            amenitiesRepository.saveAll(toSave);
        }

        return AmenitiesResponseDTO.convertToResponseDTOList(
                amenitiesRepository.findByPropertyId(propertyId)
        );
    }
    @Transactional
    public AmenitiesResponseDTO deleteAmenityById(Long id) {
        Amenities amenity = amenitiesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Amenity with ID " + id + " not found"));

        amenitiesRepository.delete(amenity);

        return AmenitiesResponseDTO.convertToResponseDTO(amenity);
    }


}
