package com.example.demo.controllers.MVC;

import com.example.demo.DTO.ServiceDTO;
import com.example.demo.helpers.ServiceMapper;
import com.example.demo.service.ServiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeControllerMvc {
    private final ServiceService service;
    private final ServiceMapper mapper;

    public HomeControllerMvc(ServiceService service, ServiceMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/homepage")
    public String showHomePage(Model model){
        List<ServiceDTO> serviceDTOS = service.allServices()
                .stream()
                .map(mapper::toDto)
                .toList();
        model.addAttribute("service", serviceDTOS);
        return "home";
    }
}
