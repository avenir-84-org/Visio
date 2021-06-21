package org.a84.visio.controller;

import lombok.RequiredArgsConstructor;
import org.a84.visio.model.User;
import org.a84.visio.service.UserDAO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MainController {
    /**
     * User DAO Service.
     */
    private final UserDAO userDAO;
    /**
     * Login page
     * @return view
     */
    @GetMapping("/")
    public String home(final Principal principal) {
        if (principal != null) {
            final User verif = userDAO.findByUserName(currentUserName(principal));
            if (verif.getRoles().equals("SADMIN")) {
                return "redirect:/sadmin";
            } else if (verif.getRoles().equals("MANAGER")) {
                return "redirect:/manager";
            }
        }
        return "login";
    }
    /**
     * Access Denied Page
     * @return view
     */
    @RequestMapping("/accessDenied")
    public String deny() {
        return "accessDenied";
    }
    /**
     * Get current username.
     * @return username
     */
    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public static String currentUserName(final Principal principal) {
        return principal.getName();
    }
    /**
     * All mapping redirect
     * @return view
     */
    @RequestMapping("*")
    public String def() {
        return "login";
    }
}