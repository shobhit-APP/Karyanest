package com.backend.karyanestApplication.Controller;

import com.backend.karyanestApplication.DTO.TermsAndConditionsRequestDTO;
import com.backend.karyanestApplication.DTO.TermsAndConditionsResponseDTO;
import com.backend.karyanestApplication.Service.TermsAndConditionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/terms")
@RequiredArgsConstructor
public class TermsAndConditionsController {

    private final TermsAndConditionsService service;

    // Create Terms - Admin only
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TermsAndConditionsResponseDTO> createTerms(@RequestBody TermsAndConditionsRequestDTO request) {
        return new ResponseEntity<>(service.createTerms(request), HttpStatus.CREATED);
    }

    // Get All Terms - Admin, User, and Agent can view
    @GetMapping
    @PreAuthorize("(hasRole('ROLE_ADMIN') or hasAuthority('terms_view')")
    public ResponseEntity<List<TermsAndConditionsResponseDTO>> getAllTerms() {
        return ResponseEntity.ok(service.getAllTerms());
    }

    // Update Terms - Admin only
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TermsAndConditionsResponseDTO> updateTerms(@PathVariable Long id, @RequestBody TermsAndConditionsRequestDTO request) {
        return ResponseEntity.ok(service.updateTerms(id, request));
    }

    // Delete Terms - Admin only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteTerms(@PathVariable Long id) {
        service.deleteTerms(id);
        return ResponseEntity.noContent().build();
    }
}
