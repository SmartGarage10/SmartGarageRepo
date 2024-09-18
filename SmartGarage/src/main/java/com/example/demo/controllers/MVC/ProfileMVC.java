package com.example.demo.controllers.MVC;

import com.example.demo.DTO.EditUserDTO;
import com.example.demo.DTO.UserDTO;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.helpers.UserMapper;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import com.example.demo.service.EmailService;
import com.example.demo.service.PasswordResetService;
import com.example.demo.service.UserService;
import com.example.demo.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProfileMVC {

    private final UserService userService;
    private final UserMapper userMapper;
    private final VehicleService vehicleService;
    private final PasswordResetService passwordResetService;
    private final EmailService emailService;

    @Autowired
    public ProfileMVC(UserService userService, UserMapper userMapper, VehicleService vehicleService, PasswordResetService passwordResetService, EmailService emailService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.vehicleService = vehicleService;
        this.passwordResetService = passwordResetService;
        this.emailService = emailService;
    }

    @GetMapping("/profile")
    public String getProfileInfo(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        List<Vehicle> vehicles = vehicleService.getVehiclesByUser(user);

        if (user != null) {
            User user1 = userService.getUserByUsername(user.getUsername()).orElseThrow();
            session.setAttribute("authenticated", true);
            model.addAttribute("vehicles", vehicles);
            model.addAttribute("user", user1);
        } else {
            session.setAttribute("authenticated", false);
            return "redirect:/auth/login";
        }
        return "profile";
    }


    @PostMapping("/edit-profile")
    public String setProfileInfo(HttpSession session, @ModelAttribute("employee") EditUserDTO userDTO, Model model) {
        User authenticatedEmployee = (User) session.getAttribute("currentUser");
        if (authenticatedEmployee == null) {
            throw new RuntimeException("No authenticated employee found in session");
        }
        User userChanges = userMapper.fromDto(userDTO);
        User updatedEmployee = userService.updateUser(authenticatedEmployee, authenticatedEmployee.getId(), userChanges);

        session.setAttribute("authenticatedEmployee", updatedEmployee);

        model.addAttribute("employee", updatedEmployee);
        return "redirect:/profile";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        User user = userService.getUserByEmail(email).orElse(null);

        if (user == null) {
            model.addAttribute("message", "No account found with that email address.");
            return "login";
        }

        // Create a token and send email
        String token = passwordResetService.createResetToken(email);
        String resetUrl = "http://localhost:8080/reset-password?token=" + token + "&email=" + email;
        emailService.sendEmail(email, "Reset Password", "Click here to reset your password: " + resetUrl);

        model.addAttribute("message", "Password reset link has been sent to your email.");
        return "redirect:/profile";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token,
                                        @RequestParam("email") String email, Model model) {
        if (!passwordResetService.isValidToken(email, token)) {
            model.addAttribute("message", "Invalid or expired token.");
            return "reset-password";
        }

        // Token is valid, show form
        model.addAttribute("token", token);
        model.addAttribute("email", email);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handlePasswordReset(@RequestParam("token") String token,
                                      @RequestParam("email") String email,
                                      @RequestParam("password") String password,
                                      Model model) {
//        if (!passwordResetService.isValidToken(email, token)) {
//            model.addAttribute("message", "Invalid or expired token.");
//            return "reset-password";
//        }

        // Update user's password and invalidate the token
        User user = userService.getUserByEmail(email).orElseThrow();
        userService.changePassword(user,user.getPassword(), password); // Make sure to hash the password
        passwordResetService.invalidateToken(email);

        model.addAttribute("message", "Password reset successful. You can now log in.");
        return "login";
    }
}
