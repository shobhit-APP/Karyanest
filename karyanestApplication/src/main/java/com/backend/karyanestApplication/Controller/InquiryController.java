package com.backend.karyanestApplication.Controller;

import com.backend.karyanestApplication.DTO.InquiryRequestDTO;
import com.backend.karyanestApplication.DTO.InquiryResponseDTO;
import com.backend.karyanestApplication.Service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/inquiries")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;

    // Create inquiry - Admin, User, or Agent can create an inquiry
    @PostMapping
    @PreAuthorize("(hasRole('ROLE_ADMIN') or hasAuthority('inquiries_create')")
    public ResponseEntity<InquiryResponseDTO> createInquiry(@RequestBody InquiryRequestDTO requestDTO) {
        InquiryResponseDTO response = inquiryService.createInquiry(requestDTO);
        return ResponseEntity.ok(response);
    }

    // Get all inquiries - Admin, User, or Agent can view all inquiries
    @GetMapping
    @PreAuthorize("(hasRole('ROLE_ADMIN') and hasAuthority('inquiries_viewsAll'))")
    public ResponseEntity<List<InquiryResponseDTO>> getAllInquiries() {
        return ResponseEntity.ok(inquiryService.getAllInquiries());
    }

    // Get inquiries by user ID - Only Admin can access this
    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('inquiries_viewByUser')")
    public ResponseEntity<List<InquiryResponseDTO>> getInquiriesByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(inquiryService.getInquiriesByUserId(id));
    }

    // Get inquiries by property ID - Only Admin can access this
    @GetMapping("/property/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('inquiries_viewByProperty')")
    public ResponseEntity<List<InquiryResponseDTO>> getInquiriesByPropertyId(@PathVariable Long id) {
        return ResponseEntity.ok(inquiryService.getInquiriesByPropertyId(id));
    }

    // Delete inquiry by ID - Only Admin can delete
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('inquiries_delete')")
    public ResponseEntity<String> deleteInquiry(@PathVariable Long id) {
        inquiryService.deleteInquiry(id);
        return ResponseEntity.ok("Inquiry deleted successfully");
    }
}
