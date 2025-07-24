package com.example.demo.controllers.MVC;

import com.example.demo.DTO.EditUserDTO;
import com.example.demo.DTO.EditVehicleDTO;
import com.example.demo.DTO.RegisterDTO;
import com.example.demo.DTO.VehicleDTO;
import com.example.demo.exceptions.EntityDuplicateException;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.helpers.UserMapper;
import com.example.demo.helpers.VehicleMapper;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import com.example.demo.service.UserService;
import com.example.demo.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;
import java.util.List;

@Controller
@RequestMapping("/vehicle")
public class VehicleMVC {
    private final VehicleService vehicleService;
    private final VehicleMapper vehicleMapper;
    private static final Logger logger = LoggerFactory.getLogger(VehicleMVC.class);

    @Autowired
    public VehicleMVC(VehicleService vehicleService, VehicleMapper vehicleMapper) {
        this.vehicleService = vehicleService;
        this.vehicleMapper = vehicleMapper;
    }

    @GetMapping("/vehicles")
    public String getClientList(HttpSession session, Model modelMVC,
                                @RequestParam(required = false) String client,
                                @RequestParam(required = false) String brand,
                                @RequestParam(required = false) String model,
                                @RequestParam(required = false) Year year,
                                @RequestParam(required = false, defaultValue = "brand") String sortField,
                                @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        User currentUser = (User) session.getAttribute("currentUser");
        logger.info("Retrieved 'currentUser' from session: {}", currentUser);

        List<Vehicle> vehicles = vehicleService.getAllVehicles(currentUser, client, brand, model, year, sortField, sortDirection);

        modelMVC.addAttribute("vehicles", vehicles);
        modelMVC.addAttribute("client", client);
        modelMVC.addAttribute("brand", brand);
        modelMVC.addAttribute("model", model);
        modelMVC.addAttribute("year", year);
        modelMVC.addAttribute("sortField", sortField);
        modelMVC.addAttribute("sortDirection", sortDirection);

        return "vehicle-list";
    }

    @PostMapping("/{vehicleId}/edit")
    public String updateVehicle(HttpSession session, @PathVariable("vehicleId") int vehicleId,
                                @ModelAttribute("vehicle") EditVehicleDTO vehicle, RedirectAttributes redirectAttributes) {
        User authenticatedEmployee = (User) session.getAttribute("currentUser");

        if (authenticatedEmployee == null) {
            redirectAttributes.addFlashAttribute("error", "No authenticated employee found in session");
            return "login";
        }

        try {
            Vehicle vehicleDetails = vehicleService.getVehicleById(vehicleId).orElseThrow(() -> new EntityNotFoundException("Vehicle was not found"));
            Vehicle vehicleChanges = vehicleMapper.fromDto(vehicle);
            Vehicle updateVehicle = vehicleService.update(authenticatedEmployee, vehicleDetails, vehicleChanges);

            redirectAttributes.addFlashAttribute("success", "Client updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating client: " + e.getMessage());
        }

        return "redirect:/vehicle/vehicles";
    }


    @PostMapping("/{vehicleId}/delete")
    public String deleteVehicle(HttpSession session, @PathVariable("vehicleId") int vehicleId, RedirectAttributes redirectAttributes) {
        User authenticatedEmployee = (User) session.getAttribute("currentUser");

        if (authenticatedEmployee == null) {
            redirectAttributes.addFlashAttribute("error", "No authenticated employee found in session");
            return "login";
        }

        try {
            vehicleService.deleteVehicle(authenticatedEmployee, vehicleId);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }

        return "redirect:/vehicle/vehicles";
    }

    @GetMapping("/create")
    public String getVehicleCreateForm(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null || (!currentUser.getRole().getRoleName().equals(Role.RoleType.ADMIN) && !currentUser.getRole().getRoleName().equals(Role.RoleType.EMPLOYEE))) {
            return "login";
        }

        model.addAttribute("vehicle", new VehicleDTO());
        return "create-vehicle";
    }

    @PostMapping("/create")
    public String handleVehicleCreateForm(@Valid @ModelAttribute("vehicle") VehicleDTO vehicleDTO,
                                          HttpSession session,
                                          BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "create-vehicle";
        }

        try {
            User user = (User) session.getAttribute("currentUser");

            Vehicle vehicle = vehicleMapper.fromDto(vehicleDTO);
            vehicleService.createNewVehicle(vehicle);
            return "redirect:/vehicle/vehicles";
        } catch (EntityDuplicateException e) {
            bindingResult.rejectValue("username", "username_error", e.getMessage());
            return "create-vehicle";
        }
    }
}
