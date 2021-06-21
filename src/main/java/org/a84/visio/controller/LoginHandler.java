package org.a84.visio.controller;

import org.a84.visio.service.LogDAO;
import org.a84.visio.model.User;
import org.a84.visio.service.UserDAO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class LoginHandler {
    /**
     * User DAO Service.
     */
    private final UserDAO userDAO;

    /**
     * Constructor injection.
     */
    public LoginHandler(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Login display.
     * @return view
     */
    @RequestMapping("login")
    public String log(final Principal principal) {
        if (principal != null) {
            final User verif = userDAO.findByUserName(MainController.currentUserName(principal));
            if (verif.getRoles().equals("SADMIN")) {
                return "redirect:/sadmin";
            } else if (verif.getRoles().equals("ADMIN")) {
                return "redirect:/manager";
            }
        }
        return "login";
    }
}
