package com.example.demo.helpers;

import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.models.User;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticationHelper {
    private final UserService service;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationHelper.class);

    public AuthenticationHelper(UserService service){
        this.service = service;
    }

    public User extractUserFromToken(Authentication authentication) throws AuthorizationException {
        if (authentication == null) {
            throw new AuthorizationException("Authentication object is null");
        }

        logger.debug("Authentication object: " + authentication);
        logger.debug("Authentication principal: " + authentication.getPrincipal());
        logger.debug("Authentication details: " + authentication.getDetails());

        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            logger.debug("Extracted username: " + username);

            Optional<User> optionalUser = service.getUserByUsername(username);
            if (optionalUser.isEmpty()) {
                throw new AuthorizationException("User not found");
            }

            User user = optionalUser.get();
            logger.debug("User found: " + user);
            return user;
        }

        throw new AuthorizationException("Authentication details are missing or invalid");
    }
}

