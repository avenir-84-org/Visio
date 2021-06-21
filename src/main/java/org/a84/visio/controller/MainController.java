package org.a84.visio.controller;

import org.a84.visio.model.User;
import org.a84.visio.service.UserDAO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class MainController {

    private final UserDAO userDAO;

    public MainController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping("/")
    public String home(Principal principal) {
        if (principal != null) {
            User verif = userDAO.findByUserName(currentUserName(principal));
            if (verif.getRoles().equals("SADMIN")) {
                return "redirect:/sadmin";
            } else if (verif.getRoles().equals("MANAGER")) {
                return "redirect:/manager";
            }
        }
        return "login";
    }

    @RequestMapping("/accessDenied")
    public String deny() {
        return "accessDenied";
    }

    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public static String currentUserName(Principal principal) {
        return principal.getName();
    }

    @RequestMapping("*")
    public String def() {
        return "login";
    }

}