package com.backend.karyanestApplication.DTO;

import com.backend.karyanestApplication.Model.PropertyResource;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyResourceDTO {

    private Long id;
    private Long propertyId;
    private PropertyResource.ResourceType resourceType; // Image, Video, Document
    private PropertyResource.ResourceTitle title; // Front, Interior, etc.
    private String url;
    private String fileId;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;

    public PropertyResourceDTO(PropertyResource propertyResource) {
        this.id = propertyResource.getId();
        this.propertyId = propertyResource.getPropertyId();
        this.resourceType=propertyResource.getResourceType();
        this.description=propertyResource.getDescription();
        this.url=propertyResource.getUrl();
        this.fileId = propertyResource.getFileId();
        this.title=propertyResource.getTitle();
        this.updateAt=propertyResource.getUpdatedAt();
        this.createdAt=propertyResource.getCreatedAt();
    }
}
