package org.a84.visio.controller;

import org.a84.visio.model.Log;
import org.a84.visio.service.LogDAO;
import org.a84.visio.model.User;
import org.a84.visio.service.UserDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class DisplayHandler {

    /**
     * User DAO Service.
     */
    private final UserDAO userDAO;
    /**
     * Log DAO Service.
     */
    private final LogDAO logDAO;

    /**
     * Constructor
     */
    public DisplayHandler(UserDAO userDAO, LogDAO logDAO) {
        this.userDAO = userDAO;
        this.logDAO = logDAO;
    }

    /**
     * Manager display.
     * @return view.
     */
    @RequestMapping(value = "/manager")
    public String manager(final Model model) {
        final List<User> users = userDAO.findByRoles("MANAGER");
        final List<User> userz = userDAO.findByRoles("FORMATEUR");
        users.addAll(userz);
        model.addAttribute("users", users);

        return "manager";
    }

    /**
     * Log display.
     * @return view
     */
    @RequestMapping("/sadmin/log")
    public String logz(final Model model) {
        final List<Log> logs = logDAO.findAll();
        model.addAttribute("logs", logs);
        return "log";
    }

    /**
     * Super admin display.
     * @return view
     */
    @GetMapping("/sadmin")
    public String admin(final Model model) {
        final List<User> users = userDAO.findAll();
        model.addAttribute("users", users);
        return "sadmin";
    }
}
