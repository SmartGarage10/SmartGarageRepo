package com.example.demo.controllers.MVC;

import com.example.demo.DTO.EditUserDTO;
import com.example.demo.helpers.UserMapper;
import com.example.demo.models.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/employee")
public class UserMVC {

    private final UserService userService;
    private final UserMapper userMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserMVC.class);

    @Autowired
    public UserMVC(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/clients")
    public String getClientList(HttpSession session, Model model,
                                @RequestParam(required = false) String username,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String vehicleModel,
                                @RequestParam(required = false) String vehicleMake,
                                @RequestParam(required = false) LocalDateTime visitStartDate,
                                @RequestParam(required = false) LocalDateTime visitEndDate,
                                @RequestParam(required = false, defaultValue = "username") String sortField,
                                @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        User currentUser = (User) session.getAttribute("currentUser");
        logger.info("Retrieved 'currentUser' from session: {}", currentUser);

        List<User> clients = userService.getAllUsers(username, email, phone, vehicleModel, vehicleMake,
                visitStartDate, visitEndDate, sortField, sortDirection);

        model.addAttribute("clients", clients);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("vehicleModel", vehicleModel);
        model.addAttribute("vehicleMake", vehicleMake);
        model.addAttribute("visitStartDate", visitStartDate);
        model.addAttribute("visitEndDate", visitEndDate);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);

        return "client-list";
    }

    @PostMapping("/client/{clientId}/edit")
    public String updateClientProfile(HttpSession session, @PathVariable("clientId") int clientId,
                                      @ModelAttribute("client") EditUserDTO clientDetailsDTO, RedirectAttributes redirectAttributes) {
        User authenticatedEmployee = (User) session.getAttribute("currentUser");

        if (authenticatedEmployee == null) {
            redirectAttributes.addFlashAttribute("error", "No authenticated employee found in session");
            return "redirect:/employee/clients";
        }

        try {
            User clientDetails = userMapper.fromDto(clientDetailsDTO);
            User updatedClient = userService.updateUser(authenticatedEmployee, clientId, clientDetails);

            redirectAttributes.addFlashAttribute("success", "Client updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating client: " + e.getMessage());
        }

        return "redirect:/employee/clients";
    }


    @PostMapping("/client/{clientId}/delete")
    public String deleteClientProfile(HttpSession session, @PathVariable("clientId") int clientId, RedirectAttributes redirectAttributes) {
        User authenticatedEmployee = (User) session.getAttribute("currentUser");

        if (authenticatedEmployee == null) {
            redirectAttributes.addFlashAttribute("error", "No authenticated employee found in session");
            return "redirect:/employee/clients";
        }

        try {
            userService.deleteUser(authenticatedEmployee, clientId);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }

        return "redirect:/employee/clients";
    }
}
