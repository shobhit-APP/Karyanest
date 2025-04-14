
package com.backend.karyanestApplication.Service;
import com.backend.karyanestApplication.DTO.PropertyResourceDTO;
import com.backend.karyanestApplication.DTO.PropertyDTO;
import com.backend.karyanestApplication.DTO.PropertySearchRequestDTO;
import com.backend.karyanestApplication.Exception.CustomException;
import com.backend.karyanestApplication.Model.Property;
import com.backend.karyanestApplication.Model.PropertyPriceChange;
import com.backend.karyanestApplication.Model.PropertyResource;
import com.backend.karyanestApplication.Model.User;
import com.backend.karyanestApplication.Repositry.PropertyPriceChangeRepository;
import com.backend.karyanestApplication.Repositry.PropertyRepository;
import com.backend.karyanestApplication.Repositry.PropertyResourcesRepository;
import com.backend.karyanestApplication.Repositry.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

    // ✅ Get all properties along with resources
    @Transactional
    public List<PropertyDTO> getAllProperties() {
        List<Property> properties = propertyRepository.findAll();
        List<PropertyResource> propertyResources = propertyResourcesRepository.findAll();
        return convertToResponseDTOList(properties, propertyResources);
    }
    @Transactional
    public PropertyDTO addProperty(PropertyDTO propertyDTO, String username) {
        // Fetch the user based on the provided username
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new CustomException("User not found In The Your Record So we Can Not Add The Property");
        }

        // ✅ Create new Property object
        Property property = new Property();

        // ✅ Use helper method to map fields
        mapPropertyFields(property, propertyDTO);

        // ✅ Associate property with user
        property.setUser(user);

        // ✅ Save property first
        Property savedProperty = propertyRepository.save(property);

        // ✅ Save property resources if provided
        savePropertyResources(savedProperty, propertyDTO.getPropertyResources());

        // ✅ Convert saved Property to Response DTO
        return convertToResponseDTO(savedProperty);
    }
    /**
     * Helper method to map PropertyRequestDTO fields to Property entity.
     */
    private void mapPropertyFields(Property property, PropertyDTO requestDTO) {
        property.setTitle(requestDTO.getTitle());
        property.setDescription(requestDTO.getDescription());
        property.setPropertyType(requestDTO.getPropertyType());
        property.setStatus(requestDTO.getStatus());
        property.setListingType(requestDTO.getListingType());
        property.setPrice(requestDTO.getPrice());
        property.setCurrency(requestDTO.getCurrency());
        property.setAreaSize(requestDTO.getAreaSize());
        property.setAreaUnit(requestDTO.getAreaUnit());
        property.setBedrooms(requestDTO.getBedrooms());
        property.setBathrooms(requestDTO.getBathrooms());
        property.setBalconies(requestDTO.getBalconies());
        property.setFurnishedStatus(requestDTO.getFurnishedStatus());
        property.setParkingSpaces(requestDTO.getParkingSpaces());
        property.setFloorNumber(requestDTO.getFloorNumber());
        property.setTotalFloors(requestDTO.getTotalFloors());
        property.setOwnershipType(requestDTO.getOwnershipType());
        property.setAgeOfProperty(requestDTO.getAgeOfProperty());
        property.setConstructionStatus(requestDTO.getConstructionStatus());
        property.setFacingDirection(requestDTO.getFacingDirection());
        property.setRoadWidth(requestDTO.getRoadWidth());
        property.setWaterAvailability(requestDTO.getWaterAvailability());
        property.setElectricityAvailability(requestDTO.getElectricityAvailability());
        property.setSecurityFeatures(requestDTO.getSecurityFeatures());
        property.setAmenities(requestDTO.getAmenities());
        property.setNearbyLandmarks(requestDTO.getNearbyLandmarks());
        property.setLocationAddress(requestDTO.getLocationAddress());
        property.setCity(requestDTO.getCity());
        property.setState(requestDTO.getState());
        property.setCountry(requestDTO.getCountry());
        property.setPincode(requestDTO.getPincode());
        property.setLatitude(requestDTO.getLatitude());
        property.setLongitude(requestDTO.getLongitude());
        property.setVerificationStatus(requestDTO.getVerificationStatus());
    }

    /**
     * Helper method to save property resources.
     */
    private void savePropertyResources(Property property, List<PropertyResourceDTO> resourceDTOs) {

        if (resourceDTOs != null && !resourceDTOs.isEmpty()) {
            for (PropertyResourceDTO resourceDTO : resourceDTOs) {
                PropertyResource resource = new PropertyResource();
                resource.setPropertyId(property.getId());
                resource.setResourceType(resourceDTO.getResourceType());
                resource.setTitle(resourceDTO.getTitle());
                resource.setUrl(resourceDTO.getUrl());
                resource.setDescription(resourceDTO.getDescription());

                propertyResourcesRepository.save(resource);
            }
        }
    }



    // ✅ Convert Property to Response DTO
    public PropertyDTO convertToResponseDTO(Property property) {
        PropertyDTO responseDTO = new PropertyDTO(property);
        // Fetch property resources and set them
        List<PropertyResource> resources = propertyResourcesRepository.findByPropertyId(property.getId());
        responseDTO.setPropertyResources(resources.stream()
                .map(PropertyResourceDTO::new)
                .collect(Collectors.toList()));

        return responseDTO;
    }
    @Transactional(readOnly = true)
    public PropertyDTO getPropertyById(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new CustomException("Property Not Found"));

        return convertToResponseDTO(property);
    }

    @Transactional(readOnly = true)
    public Property findPropertyEntityById(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new CustomException("Property Not Found"));
    }

    @Transactional(readOnly = true)
    public List<PropertyDTO> getPropertiesByUserId(Long userId) {
        List<Property> properties = propertyRepository.findByUserId(userId);
        if (properties.isEmpty()) {
            throw new CustomException("No properties found for this user.");
        }
        return convertToResponseDTOList(properties, propertyResourcesRepository.findAll());
    }

    @Transactional
    public boolean existsById(Long id) {
        return propertyRepository.existsById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new CustomException("Property Not Found With This Id:"));
        property.setStatus(Property.Status.DELETED);
        propertyRepository.save(property);
    }

    // ✅ Convert list of Properties to PropertyResponseDTO with resources
    public List<PropertyDTO> convertToResponseDTOList(List<Property> properties, List<PropertyResource> propertyResources) {
        return properties.stream().map(property -> {
            PropertyDTO responseDTO = convertToResponseDTO(property);

            // Map property resources
            List<PropertyResourceDTO> resourceDTOs = propertyResources.stream()
                    .filter(resource -> resource.getPropertyId().equals(property.getId()))
                    .map(PropertyResourceDTO::new)
                    .collect(Collectors.toList());

            responseDTO.setPropertyResources(resourceDTOs);
            return responseDTO;
        }).collect(Collectors.toList());
    }

    // ✅ Update property price and record price change
    @Transactional
    public PropertyDTO updatePropertyPriceOnly(BigDecimal newPrice, Property property, BigDecimal oldPrice, User user) {
        property.setPrice(newPrice);
        Property property1 = propertyRepository.save(property);
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

    // ✅ Search properties based on criteria
    @Transactional
    public List<PropertyDTO> searchProperties(PropertySearchRequestDTO request) {
        List<Property> properties = propertyRepository.searchProperties(
                request.getLocationAddress(),
                request.getPropertyType(),
                request.getListingType(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getBedrooms(),
                request.getBathrooms(),
                request.getAmenities()
        );
        return convertToResponseDTOList(properties, propertyResourcesRepository.findAll());
    }
    @Transactional
    public PropertyResourceDTO addPropertyResource(Long propertyId, PropertyResourceDTO resourceDTO) {
        // Fetch property or throw an exception
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new CustomException("Property Not Found"));

        // Create new property resource
        PropertyResource resource = new PropertyResource();
        resource.setPropertyId(property.getId());
        resource.setResourceType(resourceDTO.getResourceType());
        resource.setTitle(resourceDTO.getTitle());
        resource.setUrl(resourceDTO.getUrl());
        resource.setDescription(resourceDTO.getDescription());

        // Save the resource
        PropertyResource savedResource = propertyResourcesRepository.save(resource);

        return convertToResourceResponseDTO(savedResource);
    }

    @Transactional
    public PropertyDTO updateProperty(Long propertyId, PropertyDTO propertyDTO) {
        // Fetch existing property or throw exception
        Property existingProperty = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new CustomException("Property Not Found"));

        // ✅ Update only non-null fields from DTO
        updatePropertyFields(existingProperty, propertyDTO);

        // ✅ Save updated property
        Property savedProperty = propertyRepository.save(existingProperty);

        // ✅ Update resources only if they are provided
        if (propertyDTO.getPropertyResources() != null) {
            propertyResourcesRepository.deleteByPropertyId(propertyId);
            savePropertyResources(savedProperty, propertyDTO.getPropertyResources());
        }

        // ✅ Convert to response DTO and return
        return convertToResponseDTO(savedProperty);
    }

    /**
     * Helper method to update only non-null fields in Property entity.
     */
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
        if (requestDTO.getConstructionStatus() != null) property.setConstructionStatus(requestDTO.getConstructionStatus());
        if (requestDTO.getFacingDirection() != null) property.setFacingDirection(requestDTO.getFacingDirection());
        if (requestDTO.getRoadWidth() != null) property.setRoadWidth(requestDTO.getRoadWidth());
        if (requestDTO.getWaterAvailability() != null) property.setWaterAvailability(requestDTO.getWaterAvailability());
        if (requestDTO.getElectricityAvailability() != null) property.setElectricityAvailability(requestDTO.getElectricityAvailability());
        if (requestDTO.getSecurityFeatures() != null) property.setSecurityFeatures(requestDTO.getSecurityFeatures());
        if (requestDTO.getAmenities() != null) property.setAmenities(requestDTO.getAmenities());
        if (requestDTO.getNearbyLandmarks() != null) property.setNearbyLandmarks(requestDTO.getNearbyLandmarks());
        if (requestDTO.getLocationAddress() != null) property.setLocationAddress(requestDTO.getLocationAddress());
        if (requestDTO.getCity() != null) property.setCity(requestDTO.getCity());
        if (requestDTO.getState() != null) property.setState(requestDTO.getState());
        if (requestDTO.getCountry() != null) property.setCountry(requestDTO.getCountry());
        if (requestDTO.getPincode() != null) property.setPincode(requestDTO.getPincode());
        if (requestDTO.getLatitude() != null) property.setLatitude(requestDTO.getLatitude());
        if (requestDTO.getLongitude() != null) property.setLongitude(requestDTO.getLongitude());
        if (requestDTO.getVerificationStatus() != null) property.setVerificationStatus(requestDTO.getVerificationStatus());
    }
    @Transactional
    public PropertyResourceDTO updateOrCreatePropertyResource(Long propertyId, Long resourceId, PropertyResourceDTO resourceDTO) {
        // ✅ Check if property exists
        Property property = propertyRepository.findById(propertyId)
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
            newResource.setDescription(resourceDTO.getDescription());

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
        if (resourceDTO.getDescription() != null) {
            existingResource.setDescription(resourceDTO.getDescription());
        }

        // ✅ Save updated resource
        PropertyResource updatedResource = propertyResourcesRepository.save(existingResource);
        return convertToResourceResponseDTO(updatedResource);
    }


    private PropertyResourceDTO convertToResourceResponseDTO(PropertyResource updatedResource) {
        // Map property resources
        return new PropertyResourceDTO(updatedResource);
    }


}

