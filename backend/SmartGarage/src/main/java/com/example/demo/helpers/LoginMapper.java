package com.example.demo.helpers;

import com.example.demo.DTO.LoginDTO;
import com.example.demo.DTO.UserDTO;
import com.example.demo.models.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginMapper {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public User fromDto(Long id, LoginDTO loginDTO){
        User user = fromDto(loginDTO);
        user.setId(id);

        return user;
    }

    public User fromDto(LoginDTO loginDTO){
        User user = new User();
        user.setEmail(loginDTO.getEmail());
        user.setPassword(loginDTO.getPassword());

        return user;
    }

}
