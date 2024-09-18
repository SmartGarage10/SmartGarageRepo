package com.example.demo.controllers.MVC;

import com.example.demo.DTO.ServiceDTO;
import com.example.demo.helpers.ServiceMapper;
import com.example.demo.helpers.UserMapper;
import com.example.demo.models.ServiceItem;
import com.example.demo.models.User;
import com.example.demo.service.ServiceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/service")
public class ServiceControllerMvc {
    private final ServiceService service;
    private final ServiceMapper mapper;
    private final UserMapper userMapper;

    public ServiceControllerMvc(ServiceService service, ServiceMapper mapper, UserMapper userMapper) {
        this.service = service;
        this.mapper = mapper;
        this.userMapper = userMapper;
    }
    private boolean isUserLoggedIn(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping("/services")
    public String getAllSerivces(Model model){
        List<ServiceItem> services = service.allServices();
        List<ServiceDTO> serviceDTOS = services.stream()
                .map(mapper::toDto)
                .toList();
        model.addAttribute("services", serviceDTOS);
        return "ServiceView";
    }

    @GetMapping("/{id}")
    public String getServiceById(@PathVariable int id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";  // Redirect to login if not logged in
        }

        Optional<ServiceItem> serviceItem = Optional.ofNullable(service.getServiceById(id));
        if (serviceItem.isPresent()) {
            model.addAttribute("service", mapper.toDto(serviceItem.get()));
            return "ServiceDetails";
        } else {
            return "error/404";
        }
    }

    @GetMapping("/edit/{id}")
    public String editServiceForm(@PathVariable int id, Model model, HttpSession session) {
        if (!isUserLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        Optional<ServiceItem> serviceItem = Optional.ofNullable(service.getServiceById(id));
        if (serviceItem.isPresent()) {
            model.addAttribute("service", mapper.toDto(serviceItem.get()));
            return "serviceEditForm";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateService(@PathVariable int id, @ModelAttribute("service") ServiceDTO serviceDTO, HttpSession session, Model model) {
        if (!isUserLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        ServiceItem serviceItem = mapper.fromDto(serviceDTO);
        ServiceItem updatedService = service.updateService(id, serviceItem);
        model.addAttribute("service", mapper.toDto(updatedService));
        return "redirect:/service/" + id;
    }

    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable int id, HttpSession session) {
        if (!isUserLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        service.deleteService(id);
        return "redirect:/service/services";
    }

    @GetMapping("/new-service")
    public String newServiceForm(Model model, HttpSession session) {
        if (!isUserLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        model.addAttribute("service", new ServiceDTO());
        return "serviceNewForm";
    }

    @PostMapping("/new-service")
    public String createService(@ModelAttribute("service") ServiceDTO serviceDTO, HttpSession session) {
        if (!isUserLoggedIn(session)) {
            return "redirect:/auth/login";
        }

        ServiceItem serviceItem = mapper.fromDto(serviceDTO);
        service.createService(serviceItem);
        return "redirect:/service/services";
    }
}