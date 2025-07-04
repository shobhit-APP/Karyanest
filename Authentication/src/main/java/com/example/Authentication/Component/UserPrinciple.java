package com.example.Authentication.Component;

import com.example.Authentication.DTO.UserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Component
public class UserPrinciple implements UserDetails {

    private final UserDTO user;

    public UserPrinciple(UserDTO user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String UserRole=user.getRole(); // Assuming user has a method getRole() that returns a single UserRole
        return Collections.singletonList(new SimpleGrantedAuthority(UserRole));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
    public Long getUserId()
    {
        return  user.getUserId();
    }
    public boolean checkAccountStatus()
    {
        return user != null &&
                Objects.equals(user.getStatus(), "Active") &&
                Objects.equals(user.getVerificationStatus(), "Verified");
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
