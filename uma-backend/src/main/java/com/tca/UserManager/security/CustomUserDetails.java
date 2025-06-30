package com.tca.UserManager.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.tca.UserManager.entity.User;
import java.util.ArrayList;

/**
 * Custom implementation of Spring Security's UserDetails interface.
 * Wraps the application's User entity to provide authentication information to Spring Security.
 */
public class CustomUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L;
    private User user;

    public CustomUserDetails(User user) {
        super();
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>(); // Currently, no roles/authorities are defined, so return empty list
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
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
