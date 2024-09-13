package com.example.demo.controllers.MVC;

import com.example.demo.DTO.ServiceDTO;
import com.example.demo.helpers.ServiceMapper;
import com.example.demo.helpers.UserMapper;
import com.example.demo.models.ServiceItem;
import com.example.demo.service.ServiceService;
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

    @GetMapping("/{id}")
    public String getServiceById(@PathVariable int id, Model model){
        Optional<ServiceItem> serviceItem = Optional.ofNullable(service.getServiceById(id));
        if (serviceItem.isPresent()) {
            model.addAttribute("service", mapper.toDto(serviceItem.get()));
            return "serviceDetails";
        } else {
            return "error/404";
        }
    }

    @GetMapping("/edit/{id}")
    public String editServiceForm(@PathVariable int id, Model model) {
        Optional<ServiceItem> serviceItem = Optional.ofNullable(service.getServiceById(id));
        if (serviceItem.isPresent()) {
            model.addAttribute("service", mapper.toDto(serviceItem.get()));
            return "serviceEditForm";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateService(@PathVariable int id, @ModelAttribute("service") ServiceDTO serviceDTO, Model model) {
        ServiceItem serviceItem = mapper.fromDto(serviceDTO);
        ServiceItem updatedService = service.updateService(id, serviceItem);
        model.addAttribute("service", mapper.toDto(updatedService));
        return "redirect:/service/" + id;
    }

    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable int id) {
        service.deleteService(id);
        return "redirect:/service";
    }

    @GetMapping
    public String getAllServices(Model model) {
        List<ServiceItem> services = service.allServices();
        List<ServiceDTO> serviceDTOs = services.stream()
                .map(mapper::toDto)
                .toList();
        model.addAttribute("services", serviceDTOs);
        return "serviceList";
    }

    @GetMapping("/new")
    public String newServiceForm(Model model) {
        model.addAttribute("service", new ServiceDTO());
        return "serviceNewForm";
    }

    @PostMapping("/new")
    public String createService(@ModelAttribute("service") ServiceDTO serviceDTO) {
        ServiceItem serviceItem = mapper.fromDto(serviceDTO);
        service.createService(serviceItem);
        return "redirect:/service";
    }

}
