package com.tca.UserManager.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.tca.UserManager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.tca.UserManager.entity.User;
import java.util.Optional;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * Used by Spring Security to load user-specific data for authentication.
 */
@Service("UserDetailsService") // Naming the service bean explicitly
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> opt = this.userRepository.findByUsername(username);
        if (opt.isEmpty()) {
            throw new UsernameNotFoundException("Username not found: " + username);
        }
        return new CustomUserDetails(opt.get());
    }
}
