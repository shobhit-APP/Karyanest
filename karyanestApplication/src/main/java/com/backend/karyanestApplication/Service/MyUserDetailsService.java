//package com.backend.karyanestApplication.Service;
//
//import com.backend.karyanestApplication.Model.User;
//import com.backend.karyanestApplication.Model.UserPrinciple;
//
//import com.backend.karyanestApplication.Repositry.UserRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@Service
//public class MyUserDetailsService implements UserDetailsService {
//
//    private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);
//
//    @Autowired
//    private UserRepo userRepo;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepo.findByUsername(username);
//        if (user == null) {
//            logger.error("User not found: {}", username);
//            throw new UsernameNotFoundException("User Not Found: " + username);
//        }
//        return new UserPrinciple(user);
//    }
//}
