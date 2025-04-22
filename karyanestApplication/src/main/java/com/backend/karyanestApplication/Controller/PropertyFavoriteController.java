package com.backend.karyanestApplication.Controller;

import com.backend.karyanestApplication.DTO.PropertyDTO;
import com.backend.karyanestApplication.DTO.PropertyFavoriteRequestDTO;
import com.backend.karyanestApplication.DTO.PropertyFavoriteResponseDTO;
import com.backend.karyanestApplication.Service.PropertyFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/v1/favorites")
@RequiredArgsConstructor
public class PropertyFavoriteController {

    private final PropertyFavoriteService propertyFavoriteService;

    // Endpoint for adding favorites
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('favorites_create')")
    public ResponseEntity<PropertyFavoriteResponseDTO> addFavorite(@RequestBody PropertyFavoriteRequestDTO request) {
        return new ResponseEntity<>(propertyFavoriteService.addFavorite(request), HttpStatus.CREATED);
    }

    // Endpoint for getting user favorites
    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('favorites_get')")
    public ResponseEntity<List<PropertyDTO>> getUserFavorites(@PathVariable Long id) {
        return ResponseEntity.ok(propertyFavoriteService.getUserFavorites(id));
    }

    // Endpoint for removing favorites
    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('favorites_delete')")
    public ResponseEntity<Void> removeFavorite(@RequestBody PropertyFavoriteRequestDTO request) {
        propertyFavoriteService.removeFavorite(request.getUserId(), request.getPropertyId());
        return ResponseEntity.noContent().build();
    }
}
