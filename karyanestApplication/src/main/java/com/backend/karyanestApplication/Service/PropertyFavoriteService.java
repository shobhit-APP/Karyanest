package com.backend.karyanestApplication.Service;

import com.backend.karyanestApplication.DTO.*;
import com.backend.karyanestApplication.Model.*;
import com.backend.karyanestApplication.Repositry.*;
import com.example.Authentication.DTO.JWTUserDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyFavoriteService {

    private final PropertyFavoriteRepository propertyFavoriteRepository;
    private final UserRepo userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyResourcesRepository propertyResourcesRepository;
    private final AmenitiesRepository amenitiesRepository;


    @Transactional
    public PropertyFavoriteResponseDTO addFavorite(HttpServletRequest jwt, PropertyFavoriteRequestDTO request) {
        JWTUserDTO jwtuser = (JWTUserDTO) jwt.getAttribute("user");
        if (jwtuser == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (propertyFavoriteRepository.existsByUserIdAndPropertyId(jwtuser.getUserId(), request.getPropertyId())) {
            throw new IllegalStateException("Property is already in favorites");
        }

        User user = userRepository.findById(jwtuser.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        PropertyFavorite favorite = new PropertyFavorite();
        favorite.setUser(user);
        favorite.setProperty(property);
        PropertyFavorite savedFavorite = propertyFavoriteRepository.save(favorite);

        return convertToResponse(savedFavorite);
    }

    @Transactional(readOnly = true)
    public List<PropertyDTO> getUserFavorites(HttpServletRequest jwt) {
        JWTUserDTO jwtuser = (JWTUserDTO) jwt.getAttribute("user");
        if (jwtuser == null) {
            throw new IllegalArgumentException("User not found");
        }

        List<PropertyFavorite> favorites = propertyFavoriteRepository.findByUserId(jwtuser.getUserId());

        return favorites.stream().map(fav -> {
            Property property = fav.getProperty();
            PropertyDTO dto = new PropertyDTO(property);

            // Set property resources
            List<PropertyResource> resources = propertyResourcesRepository.findByPropertyId(property.getId());
            dto.setPropertyResources(resources.stream()
                    .map(PropertyResourceDTO::new)
                    .collect(Collectors.toList()));

            // Set amenities
            List<Amenities> amenities = amenitiesRepository.findByPropertyId(property.getId());
            List<AmenitiesResponseDTO> amenitiesDTOs = amenities.stream()
                    .map(AmenitiesResponseDTO::convertToResponseDTO)
                    .collect(Collectors.toList());

            dto.setAmenitiesResponseDTOS(amenitiesDTOs);

            String amenitiesStr = amenitiesDTOs.stream()
                    .map(AmenitiesResponseDTO::getAmenities)
                    .filter(amenity -> amenity != null && !amenity.isBlank())
                    .collect(Collectors.joining(","));

            dto.setAmenities(amenitiesStr.isEmpty() ? null : amenitiesStr);

            return dto;
        }).collect(Collectors.toList());
    }


    @Transactional
    public void removeFavorite(HttpServletRequest jwt, Long propertyId) {
        JWTUserDTO user = (JWTUserDTO) jwt.getAttribute("user");
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        int deletedCount = propertyFavoriteRepository.deleteByUserIdAndPropertyId(user.getUserId(), propertyId);

        if (deletedCount > 0) {
            System.out.println("Property favorite deleted successfully.");
        } else {
            System.out.println("No matching favorite found for deletion.");
        }
    }

    private PropertyFavoriteResponseDTO convertToResponse(PropertyFavorite favorite) {
        return new PropertyFavoriteResponseDTO(
                favorite.getId(),
                favorite.getUser().getId(),
                favorite.getProperty().getId(),
                favorite.getCreatedAt()
        );
    }
}
