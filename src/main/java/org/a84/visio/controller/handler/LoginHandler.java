package org.a84.visio.controller.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.a84.visio.controller.MainController;
import org.a84.visio.model.User;
import org.a84.visio.service.UserDAO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Log4j2
public class LoginHandler {
    /**
     * User DAO Service.
     */
    private final UserDAO userDAO;
    /**
     * Login display.
     * @return view
     */
    @RequestMapping("login")
    public String log(final Principal principal) {
        if (principal != null) {
            final User verif = userDAO.findByUserName(MainController.currentUserName(principal));
            if (verif.getRoles().equals("SADMIN")) {
                LOGGER.trace("Successfully logged in to SADMIN");
                return "redirect:/sadmin";
            } else if (verif.getRoles().equals("MANAGER")) {
                LOGGER.trace("Successfully logged in to MANAGER");
                return "redirect:/manager";
            }
        }
        return "login";
    }
}
