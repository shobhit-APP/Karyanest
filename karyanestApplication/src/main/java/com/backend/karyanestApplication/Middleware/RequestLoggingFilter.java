//
//package com.backend.karyanestApplication.Middleware;
//
//import com.backend.karyanestApplication.DTO.JWTUserDTO;
//import com.backend.karyanestApplication.Helper.trackingActivity;
//import com.backend.karyanestApplication.JwtSecurity.JwtUtil;
//import com.backend.karyanestApplication.Model.UserNotification;
//import com.backend.karyanestApplication.Service.NotificationService;
//import com.backend.karyanestApplication.Service.UserActivityLogService;
//import com.backend.karyanestApplication.Service.UserService;
//import com.backend.karyanestApplication.UTIL.UserContext;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.client.SimpleClientHttpRequestFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.util.ContentCachingRequestWrapper;
//import org.springframework.web.util.ContentCachingResponseWrapper;
//
//import java.io.IOException;
//import java.util.UUID;
//
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
//public class RequestLoggingFilter extends OncePerRequestFilter {
//
//    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
//    private final UserActivityLogService userActivityLogService;
//    private final RestTemplate restTemplate;
//    private final ObjectMapper objectMapper;
//    private  final NotificationService notificationService;
//    private final trackingActivity activity;
//
//    @Autowired
//    public RequestLoggingFilter(
//            JwtUtil jwtUtil, UserContext userContext, @Lazy UserService userService,
//            UserActivityLogService userActivityLogService, NotificationService notificationService, trackingActivity activity
//    ) {
//
//        this.userActivityLogService = userActivityLogService;
//        this.notificationService = notificationService;
//        this.activity = activity;
//        this.objectMapper = new ObjectMapper();
//
//        // Create RestTemplate directly
//        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//        factory.setConnectTimeout(3000);
//        factory.setReadTimeout(3000);
//        this.restTemplate = new RestTemplate(factory);
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        // Generate a unique request ID for correlation
//        String requestId = UUID.randomUUID().toString().substring(0, 8);
//
//        // Wrap request and response to enable multiple reads
//        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
//        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
//
//        // Get client information
//        String clientIp = getClientIpAddress(request);
//        String method = request.getMethod();
//        String uri = request.getRequestURI();
//
//        // Pre-processing log
//        logger.info("[{}] Request started: {} {} from IP: {}", requestId, method, uri, clientIp);
//
//        long startTime = System.currentTimeMillis();
//        try {
//            // Execute the request
//            filterChain.doFilter(requestWrapper, responseWrapper);
//        } finally {
//            long duration = System.currentTimeMillis() - startTime;
//            int status = responseWrapper.getStatus();
//
//            // Log the response
//            if (status >= 500) {
//                logger.error("[{}] Response: {} {} - Status: {} - Time: {} ms",
//                        requestId, method, uri, status, duration);
//            } else if (status >= 400) {
//                logger.warn("[{}] Response: {} {} - Status: {} - Time: {} ms",
//                        requestId, method, uri, status, duration);
//            } else {
//                logger.info("[{}] Response: {} {} - Status: {} - Time: {} ms",
//                        requestId, method, uri, status, duration);
//            }
//
//            // Log to database (except for login path which is handled by LoginLoggingFilter)
//            if (!"/v1/auth/login".equals(uri)) {
//                saveRequestToDatabase(requestWrapper, responseWrapper, status, duration, clientIp);
//            }
//
//            // Copy content back to the original response
//            responseWrapper.copyBodyToResponse();
//        }
//    }
//
//    /**
//     * Save request details to database
//     */
//    private void saveRequestToDatabase(
//            ContentCachingRequestWrapper request,
//            ContentCachingResponseWrapper response,
//            int status,
//            long duration,
//            String ipAddress
//    ) {
//        try {
//            // Extract JWT token from request
//            JWTUserDTO user = (JWTUserDTO) request.getAttribute("user");
//            // Get user from database
//            if (user == null) {
//                logger.warn("Unknown user attempted to access: {}", request.getRequestURI());
//                return;
//            }
//
//            // Get location from IP
//            String location = getLocationFromIP(ipAddress);
//
//            // Create activity description
//            String activityDescription = String.format(
//                    "%s %s - Status: %d - Duration: %d ms",
//                    request.getMethod(),
//                    request.getRequestURI(),
//                    status,
//                    duration
//            );
//            // Determine activity type based on status code
//
//            String activityType;
//            if (status >= 500) {
//                activityType = "ERROR";  // Server errors
//            } else if (status >= 400) {
//                activityType = "UNAUTHORIZED";  // Client errors like 401, 403
//            } else {
//                activityType = "LOGIN";  // Successful login (2xx status)
//            }
//
//            activityType = activity.Activity(request);
//           // Save to database
//            userActivityLogService.logActivityUsingJWTDTO(
//                    activityType,
//                    activityDescription,
//                    location,
//                    user
//            );  // ✅ Semicolon yahan hona chahiye
//            notificationService.sendNotification_otherRequest(
//                    user,
//                    UserNotification.NotificationType.ALERT,
//                    "Thank You For Accessing The Route"
//            );
//
//
//        } catch (Exception e) {
//            logger.error("Error saving request to database: {}", e.getMessage());
//        }
//    }
//
//    /**
//     * Extract client IP address considering proxies
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
//        if (ipAddress == null || ipAddress.isEmpty() || "127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
//            return "Local Development";
//        }
//
//        try {
//            String apiUrl = "https://ipwho.is/" + ipAddress;
//            String response = restTemplate.getForObject(apiUrl, String.class);
//            JsonNode jsonResponse = objectMapper.readTree(response);
//
//            if (jsonResponse.has("status") && "success".equals(jsonResponse.get("status").asText())) {
//                String city = jsonResponse.path("city").asText("Unknown City");
//                String country = jsonResponse.path("country").asText("Unknown Country");
//                return city + ", " + country;
//            }
//        } catch (Exception e) {
//            logger.warn("Error getting location for IP {}: {}", ipAddress, e.getMessage());
//        }
//        return "Unknown Location";
//    }
//
//    /**
//     * Skip logging for certain paths
//     */
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String path = request.getRequestURI();
//        return path.startsWith("/static/") ||
//                path.startsWith("/resources/") ||
//                path.startsWith("/favicon.ico");
//    }
//}