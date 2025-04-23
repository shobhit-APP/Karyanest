package com.example.Authentication.Service;

import com.example.Authentication.Component.UserPrinciple;
import com.example.Authentication.DTO.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Autowired
    private Auth auth;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.trim().isEmpty()) {
            logger.warn("Empty username provided.");
            throw new UsernameNotFoundException("Username must not be empty.");
        }

        UserDTO user = auth.getUserDetails("username", username);

        if (user == null) {
            logger.warn("User not found: {}", username);
            throw new UsernameNotFoundException("User with username '" + username + "' was not found.");
        }

        return new UserPrinciple(user);
    }
}
