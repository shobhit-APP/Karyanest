package com.example.notification.Controller;

import com.example.notification.DTO.UpdateTokenRequestDTO;
import com.example.notification.Services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/token")
@RequiredArgsConstructor
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @PostMapping("/update-token")
    public ResponseEntity<String> updateFCMToken(@RequestBody UpdateTokenRequestDTO request) {
        tokenService.updateFCMToken(request.getUserId(), request.getFcmToken(), request.getDeviceId());
        return ResponseEntity.ok("Token updated");
    }
}
