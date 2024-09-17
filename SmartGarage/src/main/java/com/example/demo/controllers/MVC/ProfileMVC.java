package com.example.demo.controllers.MVC;

import com.example.demo.DTO.UserDTO;
import com.example.demo.helpers.UserMapper;
import com.example.demo.models.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileMVC {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public ProfileMVC(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/profile")
    public String getProfileInfo(HttpSession session, Model model){
        User user = (User) session.getAttribute("currentUser");

        if (user != null) {
            session.setAttribute("authenticated", true);
        } else {
            session.setAttribute("authenticated", false);
            return "redirect:/auth/login";
        }
        return "profile";
    }


    @PostMapping("/edit-profile")
    public String setProfileInfo(HttpSession session, @ModelAttribute("employee") UserDTO userDTO, Model model) {
        User authenticatedEmployee = (User) session.getAttribute("authenticatedEmployee");

        if (authenticatedEmployee == null) {
            throw new RuntimeException("No authenticated employee found in session");
        }
        User userChanges = userMapper.fromDto(userDTO);
        User updatedEmployee = userService.updateUser(authenticatedEmployee, authenticatedEmployee.getId(), userChanges);

        session.setAttribute("authenticatedEmployee", updatedEmployee);

        model.addAttribute("employee", updatedEmployee);
        return "redirect:/profile";
    }
}
