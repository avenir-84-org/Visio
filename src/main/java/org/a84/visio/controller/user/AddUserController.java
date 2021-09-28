package org.a84.visio.controller.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.a84.visio.controller.MainController;
import org.a84.visio.model.Log;
import org.a84.visio.model.User;
import org.a84.visio.service.LogDAO;
import org.a84.visio.service.UserDAO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.util.Date;

@Controller
@RequiredArgsConstructor
@Log4j2
public class AddUserController {
    /**
     * User DAO Service.
     */
    private final @NonNull UserDAO userDAO;
    /**
     * Log DAO Service.
     */
    private final @NonNull LogDAO logDAO;
    /**
     * Hash pass.
     */
    private final @NonNull PasswordEncoder passwordEncoder;


    @Autowired
    private Environment env;

    /**
     * Add user / log to the db.
     * @param username - str
     * @param password - str
     * @param accessLevel - str
     * @param principal - principal
     * @return view
     */
    @RequestMapping(value = "/add")
    public String add(
            @RequestParam(name = "username") final String username,
            @RequestParam(name = "password") final String password,
            @RequestParam(name = "accessLevel") final String accessLevel,
            Principal principal
    ) {
        final User verif = userDAO.findByUserName(MainController.currentUserName(principal));
        if (!userDAO.existsByUserName(username)) {
            final String hPass = passwordEncoder.encode(password);
            final DateFormat shortDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            // Create user
            final User user = new User(username, hPass, true, accessLevel, shortDate.format(new Date()));
            userDAO.save(user);
            LOGGER.info("Added user: {}", user.getUserName());
            // Save logs
            final User currentLogged = userDAO.findByUserName(MainController.currentUserName(principal));
            final Log log = new Log(currentLogged.getUserName(), "AJOUT", user.getUserName(), shortDate.format(new Date()), currentLogged.getRoles(), user.getRoles());
            logDAO.save(log);
        }
        if (verif.getRoles().equals("SADMIN")) {
            return "redirect:/sadmin";
        } else {
            return "redirect:/manager";
        }
    }
    /**
     * Add formateur using bash commands.
     * @param username - str
     * @param password - str
     * @param principal - principal
     * @return view
     * @throws IOException - exception
     * @throws InterruptedException - exception
     */
    @PostMapping(value = "/bashadd")
    public String account(
            @RequestParam(name = "username") final String username,
            @RequestParam(name = "password") final String password,
            final Principal principal) throws IOException, InterruptedException {
        final User user = userDAO.findByUserName(MainController.currentUserName(principal));
        final DateFormat shortDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        // Check if user already exists
        if (!userDAO.existsByUserName(username)) {
            if (username != null && password != null) {
                final String hPass = passwordEncoder.encode(password);
                final User acc = new User(username, hPass, true, "FORMATEUR", shortDate.format(new Date()));
                LOGGER.info("Added user: {}", user.getUserName());
                userDAO.save(acc);
                final String shellU = env.getProperty("visio.u");
                final String shellP = env.getProperty("visio.p");
                final String shellH = env.getProperty("visio.h");
                final String co = "echo " + shellP + "| sudo -S prosodyctl register " + username + " " + shellH + " " + password;
                final Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", co});
                Thread.sleep(200); // TODO: LOL that scrappy concurrence avoidance
                final String co3 = "echo " + shellP + "| sudo -S systemctl restart prosody.service";
                final Process p3 = Runtime.getRuntime().exec(new String[]{"bash", "-c", co3});
                Thread.sleep(200); // TODO: LOL that scrappy concurrence avoidance
                // Save logs
                final User currentLogged = userDAO.findByUserName(MainController.currentUserName(principal));
                final Log log = new Log(currentLogged.getUserName(), "AJOUT", acc.getUserName(), shortDate.format(new Date()), currentLogged.getRoles(), acc.getRoles());
                LOGGER.info("cmd: {}", co);
                LOGGER.info("cmd: {}", co3);
                logDAO.save(log);
            }
        }
        if (user.getRoles().equals("SADMIN")) {
            return "redirect:/sadmin";
        } else {
            return "redirect:/manager";
        }
    }
}
