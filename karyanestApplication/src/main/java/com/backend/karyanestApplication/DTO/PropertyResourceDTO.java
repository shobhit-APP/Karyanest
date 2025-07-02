package com.backend.karyanestApplication.DTO;

import com.backend.karyanestApplication.Model.PropertyResource;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

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

    // 🔽 New fields added
    private Long fileSize;
    private String fileType;
    private Integer sortOrder;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    private ZonedDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    private ZonedDateTime updateAt;

    public PropertyResourceDTO(PropertyResource propertyResource) {
        this.id = propertyResource.getId();
        this.propertyId = propertyResource.getPropertyId();
        this.resourceType = propertyResource.getResourceType();
        this.title = propertyResource.getTitle();
        this.url = propertyResource.getUrl();
        this.fileId = propertyResource.getFileId();
        this.description = propertyResource.getDescription();

        // 🔽 Add getters when available in entity
        this.fileSize = propertyResource.getFileSize();
        this.fileType = propertyResource.getFileType();
        this.sortOrder = propertyResource.getSortOrder();

        this.createdAt = propertyResource.getCreatedAt();
        this.updateAt = propertyResource.getUpdatedAt();
    }
}
