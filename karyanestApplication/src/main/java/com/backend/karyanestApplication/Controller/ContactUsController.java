package com.backend.karyanestApplication.Controller;

import com.backend.karyanestApplication.DTO.ContactUsRequestDTO;
import com.backend.karyanestApplication.DTO.ContactUsResponseDTO;
import com.backend.karyanestApplication.Service.ContactUsServiceImpl;
import com.example.Authentication.DTO.JWTUserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/contactUs")
@RequiredArgsConstructor
public class ContactUsController {

    private final ContactUsServiceImpl contactUsService;

    // ✅ 1. Submit a Contact Us request
    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasAuthority('contactUs')")
    public ResponseEntity<?> submitContactQuery(@Valid @RequestBody ContactUsRequestDTO requestDTO,HttpServletRequest request) {
        try {
            JWTUserDTO user = (JWTUserDTO) request.getAttribute("user");
            String username = user.getUsername();
            ContactUsResponseDTO savedQuery = contactUsService.saveContactQuery(requestDTO,user);
            return ResponseEntity.ok(savedQuery);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to submit query: " + e.getMessage());
        }
    }

    // ✅ 2. Fetch all queries (admin only)
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('getall_contactUsQueries')")
    public ResponseEntity<?> getAllContactQueries() {
        try {
            List<ContactUsResponseDTO> allQueries = contactUsService.getAllContactQueries();
            if (allQueries.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No contact queries found.");
            }
            return ResponseEntity.ok(allQueries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching contact queries: " + e.getMessage());
        }
    }

    // ✅ 3. Fetch current user's own queries
    @GetMapping("/user/queries")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasAuthority('contact_queries_viewByUser')")
    public ResponseEntity<?> getMyQueries(HttpServletRequest request) {
        try {
            JWTUserDTO user = (JWTUserDTO) request.getAttribute("user");
            String username = user.getUsername();

            List<ContactUsResponseDTO> userQueries = contactUsService.getQueriesByEmail(username);
            if (userQueries.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No queries found for user: " + username);
            }
            return ResponseEntity.ok(userQueries);
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: Unable to extract user from request.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching user's contact queries: " + e.getMessage());
        }
    }
}
