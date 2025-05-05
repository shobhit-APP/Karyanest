package com.example.storageService.Controller;

import com.backblaze.b2.client.exceptions.B2Exception;
import com.example.module_b.ExceptionAndExceptionHandler.CustomException;
import com.example.storageService.Model.B2FileVersion;
import com.example.storageService.Service.B2FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/v1/storage")
@RequiredArgsConstructor
public class StorageController {

    @Autowired
    private B2FileService b2FileService;

    @GetMapping("/auth-token")
    public ResponseEntity<String> getAuthToken() {
        try {
            String token = b2FileService.getAuthToken();
            return ResponseEntity.ok(token);
        } catch (B2Exception e) {
            return ResponseEntity.status(500).body("Token generation failed: " + e.getMessage());
        }
    }
}