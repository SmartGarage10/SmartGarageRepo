package com.example.demo.helpers;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class SecurityHelper {
    private final UserRepository userRepository;

    @Autowired
    public SecurityHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser");
    }
    public Authentication getAuthentication() {
        return isAuthenticated() ? SecurityContextHolder.getContext().getAuthentication() : null;
    }
    public User getCurrentUser() {
        isAuthenticated();

        Authentication authentication = getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
                UserDetails userDetails = (UserDetails) principal;
                return userRepository.findUserByEmail(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        }
        return null;
    }
}
