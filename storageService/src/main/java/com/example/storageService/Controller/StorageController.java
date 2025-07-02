package com.example.storageService.Controller;

import com.backblaze.b2.client.exceptions.B2Exception;
import com.example.storageService.Service.B2StorageService;
import com.example.storageService.Service.R2StorageService;
import com.example.storageService.Service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/storage")
@RequiredArgsConstructor
public class StorageController {
    @Autowired
    private R2StorageService r2StorageService;
    @Autowired
    private B2StorageService b2StorageService;

    @GetMapping("/b2-auth-token")
    public ResponseEntity<String> getAuthToken() {
        try {
            String token = b2StorageService.getAuthToken();
            return ResponseEntity.ok(token);
        } catch (B2Exception e) {
            return ResponseEntity.status(500).body("Token generation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/r2-auth-token")
    public ResponseEntity<String> getR2AuthToken() throws Exception {
        try {
            String token = r2StorageService.getAuthToken();
            return ResponseEntity.ok(token);
        } catch (B2Exception e) {
            return ResponseEntity.status(500).body("Token generation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}