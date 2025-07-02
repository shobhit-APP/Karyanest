package com.backend.karyanestApplication.DTO;

import com.backend.karyanestApplication.Model.Amenities;
import com.backend.karyanestApplication.Model.PropertyResource;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmenitiesResponseDTO {

    private Long id;
    private Long propertyId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    private ZonedDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    private ZonedDateTime updatedAt;
    private String amenities;

    // Convert single Amenities to AmenitiesResponseDTO
    public static AmenitiesResponseDTO convertToResponseDTO(Amenities amenities) {
        if (amenities == null) {
            return null;
        }
        return new AmenitiesResponseDTO(
                amenities.getId(),
                amenities.getPropertyId(),
                amenities.getCreatedAt(),
                amenities.getUpdatedAt(),
                amenities.getAmenities()
        );
    }

    // Convert list of Amenities to list of AmenitiesResponseDTO
    public static List<AmenitiesResponseDTO> convertToResponseDTOList(List<Amenities> amenitiesList) {
        if (amenitiesList == null) {
            return null;
        }
        return amenitiesList.stream()
                .map(AmenitiesResponseDTO::convertToResponseDTO)
                .collect(Collectors.toList());
    }
}