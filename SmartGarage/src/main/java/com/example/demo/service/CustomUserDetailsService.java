package com.example.demo.service;

import com.example.demo.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRole().getAuthorities() // Ensure the role name has "ROLE_" prefix
        );
    }

//    private Collection<SimpleGrantedAuthority> getAuthorities(String roleName) {
//        // Ensure that the role name starts with "ROLE_"
//        if (!roleName.startsWith("ROLE_")) {
//            roleName = "ROLE_" + roleName;
//        }
//        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
//    }
}
