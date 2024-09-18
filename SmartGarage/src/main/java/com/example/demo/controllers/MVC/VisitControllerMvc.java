package com.example.demo.controllers.MVC;

import com.example.demo.DTO.VisitDTO;
import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.helpers.AuthenticationHelper;
import com.example.demo.helpers.UserMapper;
import com.example.demo.helpers.VisitMapper;
import com.example.demo.models.User;
import com.example.demo.models.Visit;
import com.example.demo.service.VisitService;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.Banner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/visit")
public class VisitControllerMvc {
    private final VisitService service;
    private final VisitMapper mapper;
    private final AuthenticationHelper authenticationHelper;

    public VisitControllerMvc(VisitService service, VisitMapper mapper, AuthenticationHelper authenticationHelper) {
        this.service = service;
        this.mapper = mapper;

        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/create")
    public String showCreateVisit(HttpSession session, Model model){
        User user = (User) session.getAttribute("currentUser");
        model.addAttribute("visit", new VisitDTO());
        return "create-visit";
    }

    @PostMapping("/create")
    public String createVisit(HttpSession session, @ModelAttribute("visit") VisitDTO visitDTO, Model model){
        try {
            User user = (User) session.getAttribute("currentUser");

            Visit visit = mapper.fromDto(visitDTO);
            service.createVisit(user, visit);

            return "redirect:/visit/visits";
        }catch (AuthorizationException e){
            model.addAttribute("error", "You don't have permission to create visits! ");
            return "error";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteVisit(HttpSession session, @PathVariable int id, Model model){
        try {
            User user = (User) session.getAttribute("currentUser");

            service.deleteVisit(user, id);
            return "redirect:/visit/visits";
        }catch (AuthorizationException e){
            model.addAttribute("error", "You don't have permission to delete visits");
            return "error";
        }
    }

    @GetMapping("/visits")
    public String listVisits(HttpSession session, Model model){
        User user = (User) session.getAttribute("currentUser");

        model.addAttribute("visits", service.getAllVisits());
        return "visit-list";
    }
}
