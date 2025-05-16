package com.backend.karyanestApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFavoritesResponseDTO {
    private Long userId;
    private String username;
    private List<PropertyFavoriteResponseDTO> favorites;
}
