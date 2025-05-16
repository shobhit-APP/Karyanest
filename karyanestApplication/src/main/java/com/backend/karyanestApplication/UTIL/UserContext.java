//package com.backend.karyanestApplication.UTIL;
//
//import com.backend.karyanestApplication.JwtSecurity.JwtUtil;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//public class UserContext {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    public String getUsername(HttpServletRequest request) {
//        String token = extractToken(request);
//        return (token != null) ? jwtUtil.extractUsername(token) : null;
//    }
//
//    public String getUserRole(HttpServletRequest request) {
//        String token = extractToken(request);
//        return (token != null) ? jwtUtil.extractRole(token) : null;
//    }
//
//    public GrantedAuthority getUserRoleAuthority(HttpServletRequest request) {
//        String role = getUserRole(request);
//        return (role != null) ? new SimpleGrantedAuthority(role) : null;
//    }
//
//    public String extractToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        return (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) ? bearerToken.substring(7) : null;
//    }
//
//    public String validateAndExtractUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String token = extractToken(request);
//        if (token == null) {
//            sendUnauthorizedResponse(response, "Authorization failed: Invalid token");
//            return null;
//        }
//
//        try {
//            String username = jwtUtil.extractUsername(token);
//            String roles = jwtUtil.extractRole(token);
//
//            if (username == null || !jwtUtil.validateToken(token, username, roles)) {
//                sendUnauthorizedResponse(response, "Authorization failed: Invalid or expired token");
//                return null;
//            }
//            return username;
//        } catch (Exception e) {
//            sendUnauthorizedResponse(response, "Authorization failed: " + e.getMessage());
//            return null;
//        }
//    }
//
//    public void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.getWriter().write(message);
//    }
//
//    public String extractRole(HttpServletRequest request) {
//        String token=extractToken(request);
//        return jwtUtil.extractRole(token);
//    }
//    public <T> T extractJWTValue(HttpServletRequest request, String key, Class<T> type) {
//        String token = extractToken(request);
//        return jwtUtil.extractJWTValue(token, key, type);
//    }
//
//
//    public String extractFullname(HttpServletRequest request) {
//        String token=extractToken(request);
//        return (token != null) ? jwtUtil.fullname(token) : null;
//    }
//}
