package com.backend.karyanestApplication.Controller;

import com.backend.karyanestApplication.DTO.FAQRequestDTO;
import com.backend.karyanestApplication.DTO.FAQResponseDTO;
import com.backend.karyanestApplication.Service.FAQService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/faqs")
@RequiredArgsConstructor
public class FAQController {

    private final FAQService faqService;

    // Create a new FAQ - Admin, User, or Agent with specific authority
    @PostMapping
    @PreAuthorize("(hasRole('ROLE_ADMIN'))")
    public ResponseEntity<FAQResponseDTO> create(@RequestBody FAQRequestDTO requestDTO) {
        return new ResponseEntity<>(faqService.createFAQ(requestDTO), HttpStatus.CREATED);
    }

    // Get all FAQs - Admin, User, or Agent can access this
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_AGENT') or hasAuthority('faqs_view_all')")
    public ResponseEntity<List<FAQResponseDTO>> getAll() {
        return ResponseEntity.ok(faqService.getAllFAQs());
    }

    // Update FAQ by ID - Only Admin can access this
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<FAQResponseDTO> update(@PathVariable Long id, @RequestBody FAQRequestDTO requestDTO) {
        return ResponseEntity.ok(faqService.updateFAQ(id, requestDTO));
    }

    // Delete FAQ by ID - Only Admin can access this
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        faqService.deleteFAQ(id);
        return ResponseEntity.noContent().build();
    }
}
