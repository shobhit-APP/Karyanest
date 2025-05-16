//package com.backend.karyanestApplication.Middleware;
//
//import com.backend.karyanestApplication.DTO.JWTUserDTO;
////import com.backend.karyanestApplication.Service.PermissionService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Collections;
//import java.util.Map;
//
//import com.backend.karyanestApplication.UTIL.UserContext;
//
//
//@Component
//public class AuthorizationFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private UserContext userContext; // Using UserContext instead of JwtUtil
//
////    @Autowired
////    private PermissionService permissionService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//
//        String path = request.getServletPath();
//        System.out.println("Request Path: " + path);
//
//        // Bypass authentication for certain endpoints
//        if (path.matches("^/v1/auth/.*") || path.matches("^/v1/home/.*") ||
//                path.matches("^/v3/api-docs.*") || path.matches("^/swagger-ui/.*") ||
//                path.matches("^/swagger-ui.html$")) {
//
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // Extract and validate user
//        String username = userContext.validateAndExtractUser(request, response);
//        if (username == null) {
//            return; // Response already sent in case of failure
//        }
//
//        // Extract role separately
//        String role = userContext.extractRole(request);
//        String fullName=userContext.extractFullname(request);
//        if (role == null) {
//            userContext.sendUnauthorizedResponse(response, "Authorization failed: Role not found in token");
//            return;
//        }
//
////        if (!permissionService.checkPermission(role, path, username)) {
////            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
////            response.setContentType("application/json");
////            response.getWriter().write("{\"error\": \"Access Denied: You do not have permission to view this resource or access data belonging to another.\"}");
////            return;
////        }
//
//        // Authenticate the user
//        GrantedAuthority authority = userContext.getUserRoleAuthority(request);
//        UsernamePasswordAuthenticationToken authToken =
//                new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority));
//        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//       Long userId = userContext.extractJWTValue(request, "id", Long.class);
//        JWTUserDTO user = new JWTUserDTO(userId, username, role,fullName);
//        request.setAttribute("user", user);
//        chain.doFilter(request, response);
//    }
//}
