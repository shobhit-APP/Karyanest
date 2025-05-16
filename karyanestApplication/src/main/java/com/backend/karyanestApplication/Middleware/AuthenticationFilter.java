//package com.backend.karyanestApplication.Middleware;
//import com.backend.karyanestApplication.Model.UserPrinciple;
//import com.backend.karyanestApplication.Service.MyUserDetailsService;
//import com.backend.karyanestApplication.UTIL.UserContext;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Collections;
//import java.util.List;
//@Component
//public class AuthenticationFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private UserContext userContext;
//
//    @Autowired
//    private MyUserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//        String path = request.getServletPath();
//        System.out.println("Request Path: " + path);
//
//        if (path.matches("^/v1/auth/.*") || path.matches("^/v1/home/.*") || path.matches("^/v3/api-docs.*") || path.matches("^/swagger-ui/.*") || path.matches("^/swagger-ui.html$")) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        String username = userContext.validateAndExtractUser(request, response);
//        if (username == null) {
//            // Stop further execution if username is not valid
//            return;
//        }
//
//        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//// Cast UserDetails to UserPrinciple to access the custom method
//        if (userDetails instanceof UserPrinciple userPrinciple) {
//            // Use the custom method to check account status
//            boolean isAccountValid = userPrinciple.checkAccountStatus();
//            if (!isAccountValid) {
//                System.out.println("User account is either inactive or not verified.");
//                // Handle invalid account logic here (e.g., throw an exception or deny access)
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("User account is inactive or not verified or have Not Access To this route.");
//                return;
//            }
//        } else {
//            System.out.println("Invalid UserDetails implementation.");
//        }
//
//
//// Retrieve the user's granted authority
//        GrantedAuthority authority = userContext.getUserRoleAuthority(request);
//
//// Create and configure an authentication token
//        UsernamePasswordAuthenticationToken authToken =
//                new UsernamePasswordAuthenticationToken(userDetails, null, Collections.singletonList(authority));
//        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//// Set the authentication context
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//
//// Proceed with the next filter in the chain
//        chain.doFilter(request, response);
//
//    }
//}
//
