//package com.backend.karyanestApplication.Middleware;
//
//import com.backend.karyanestApplication.Exception.CustomException;
//import com.backend.karyanestApplication.Model.User;
//import com.backend.karyanestApplication.Service.UserActivityLogService;
//import com.backend.karyanestApplication.Service.UserService;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.client.SimpleClientHttpRequestFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.util.ContentCachingRequestWrapper;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//
//@Component
//public class LoginLoggingFilter extends OncePerRequestFilter {
//    private static final Logger logger = LoggerFactory.getLogger(LoginLoggingFilter.class);
//    private static final String LOGIN_PATH = "/v1/auth/login";
//    private static final String POST_METHOD = "POST";
//
//    private final UserActivityLogService userActivityLogService;
//    private final UserService userService;
//    private final ObjectMapper objectMapper;
//    private final RestTemplate restTemplate;
//
//    public LoginLoggingFilter(
//            @Lazy UserService userService,
//            UserActivityLogService userActivityLogService
//    ) {
//        this.userService = userService;
//        this.userActivityLogService = userActivityLogService;
//        this.objectMapper = new ObjectMapper();
//
//        // Create RestTemplate directly instead of autowiring it
//        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//        factory.setConnectTimeout(3000);
//        factory.setReadTimeout(3000);
//        this.restTemplate = new RestTemplate(factory);
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        // Process only login requests
//        if (isLoginRequest(request)) {
//            ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);
//            filterChain.doFilter(cachingRequest, response);
//
//            // Process the login request after the response has been sent
//            try {
//                processLoginRequest(cachingRequest);
//            } catch (Exception e) {
//                logger.error("Error processing login request: {}", e.getMessage());
//                // Don't throw exception here to prevent affecting user experience
//            }
//        } else {
//            filterChain.doFilter(request, response);
//        }
//    }
//
//    /**
//     * Checks if the request is a login attempt
//     */
//    private boolean isLoginRequest(HttpServletRequest request) {
//        return LOGIN_PATH.equals(request.getRequestURI()) &&
//                POST_METHOD.equalsIgnoreCase(request.getMethod());
//    }
//
//    /**
//     * Processes login requests by logging user activity
//     */
//    private void processLoginRequest(ContentCachingRequestWrapper cachingRequest) {
//        try {
//            byte[] body = cachingRequest.getContentAsByteArray();
//            if (body.length == 0) {
//                logger.warn("Login request with empty body received");
//                return;
//            }
//
//            // Parse request body
//            String requestBody = new String(body, StandardCharsets.UTF_8);
//            JsonNode jsonNode = objectMapper.readTree(requestBody);
//
//            // Extract username safely
//            String username = jsonNode.has("username") ? jsonNode.get("username").asText() : null;
//            String email = jsonNode.has("email") ? jsonNode.get("email").asText() : null;
//            String phoneNumber = jsonNode.has("phoneNumber") ? jsonNode.get("phoneNumber").asText() : null;
//            String ipAddress = getClientIpAddress(cachingRequest);
//
//            // Try fetching by username first
//            User user=null;
//            if (username != null && !username.isEmpty()) {
//                user = userService.getUserByUsername(username);
//            }
//
//            // If not found, try by email
//            if (email != null && !email.isEmpty()) {
//                user = userService.getUserByEmail(email);
//            }
//            // If still not found, try by phone number
//            if (phoneNumber != null && !phoneNumber.isEmpty()) {
//                user = userService.getUserByPhoneNumber(phoneNumber);
//            }
//            if (user == null) {
//                logger.warn("Login attempt failed. No matching user found for provided credentials.");
//                return;
//            }
//            // Now you can continue with login, log activity, or send notification
//            // Get location from IP
//            String location = getLocationFromIP(ipAddress);
//
//            // Log the activity with the location
//            userActivityLogService.logActivity(
//                    "LOGIN",
//                    "User logged in successfully",
//                    location,
//                    user
//            );
//
//            // Update last login timestamp
//            userService.updateUserLastLogin(user);
//
//        } catch (Exception e) {
//            logger.error("Error logging login activity: {}", e.getMessage());
//            // Don't throw the exception to avoid affecting the user
//        }
//    }
//
//    /**
//     * Extracts the client IP address, handling potential proxy scenarios
//     */
//    private String getClientIpAddress(HttpServletRequest request) {
//        String xForwardedFor = request.getHeader("X-Forwarded-For");
//        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
//            // Get the first IP in the chain which is the original client
//            return xForwardedFor.split(",")[0].trim();
//        }
//        return request.getRemoteAddr();
//    }
//
//    /**
//     * Fetches the approximate location based on the IP address using a REST API.
//     */
//    private String getLocationFromIP(String ipAddress) {
//        if (ipAddress == null || ipAddress.isBlank()
//                || ipAddress.equals("127.0.0.1")
//                || ipAddress.equals("0:0:0:0:0:0:0:1")) {
//            return "Local Development";
//        }
//
//        try {
//            String apiUrl = "https://ipapi.co/" + ipAddress + "/json/";
//            String response = restTemplate.getForObject(apiUrl, String.class);
//
//            if (response != null) {
//                JsonNode json = objectMapper.readTree(response);
//
//                String city = json.path("city").asText("Unknown City");
//                String country = json.path("country_name").asText("Unknown Country");
//                return city + ", " + country;
//            }
//        } catch (Exception e) {
//            logger.error("🌍 Error fetching location for IP {}: {}", ipAddress, e.getMessage());
//        }
//
//        return "Unknown Location";
//    }
//}