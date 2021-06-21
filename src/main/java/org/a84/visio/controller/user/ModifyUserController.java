package org.a84.visio.controller.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.a84.visio.controller.MainController;
import org.a84.visio.model.Log;
import org.a84.visio.service.LogDAO;
import org.a84.visio.model.User;
import org.a84.visio.service.UserDAO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.util.Date;

@Controller
@RequiredArgsConstructor
@Log4j2
public class ModifyUserController {

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

    /**
     * Modify user from db.
     * @param id - int
     * @param role - str
     * @param password - str
     * @param principal - principal
     * @return view
     * @throws InterruptedException - except
     * @throws IOException - except
     */
    @RequestMapping(value = "/modify")
    public String modify(
            @RequestParam("id") final int id,
            @RequestParam("role") final String role,
            @RequestParam("password") final String password,
            final Principal principal) throws InterruptedException, IOException {

        final DateFormat shortDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        final User user = userDAO.findById(id);
        final String username = user.getUserName();

        if (!user.getRoles().equals("FORMATEUR")) {
            user.setRoles(role);
            user.setDate(shortDate.format(new Date()));
            userDAO.save(user);
            LOGGER.info("Added user: {}", user.getUserName());
            final User currentLogged = userDAO.findByUserName(MainController.currentUserName(principal));
            final Log log = new Log(currentLogged.getUserName(), "RÔLE MODIFIÉ", user.getUserName(), shortDate.format(new Date()), currentLogged.getRoles(), user.getRoles());
            logDAO.save(log);
        }

        else {
            final String co = "echo aaa| ssh -tt avenir@avenir843.pro.dns-orange.fr sudo prosodyctl deluser " + username + "@avenir843.pro.dns-orange.fr";
            final Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", co});
            Thread.sleep(200);
            final String co2 = "echo aaa| ssh -tt avenir@avenir843.pro.dns-orange.fr sudo prosodyctl register " + username + " avenir843.pro.dns-orange.fr " + password;
            final Process p2 = Runtime.getRuntime().exec(new String[]{"bash", "-c", co2});
            final User currentLogged = userDAO.findByUserName(MainController.currentUserName(principal));
            final Log log = new Log(currentLogged.getUserName(), "MDP CHANGÉ", user.getUserName(), shortDate.format(new Date()), currentLogged.getRoles(), user.getRoles());
            logDAO.save(log);
        }

        final User verif = userDAO.findByUserName(MainController.currentUserName(principal));
        if (verif.getRoles().equals("SADMIN")) {
            return "redirect:/sadmin";
        } else {
            return "redirect:/manager";
        }
    }



    /**
     * Change passwords.
     * @param password - str
     * @param principal - principal
     * @return view
     */
    @RequestMapping("/pwd")
    public String pwd(
            @RequestParam("password") final String password,
            final Principal principal) {

        final DateFormat shortDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        String hPass = passwordEncoder.encode(password);
        // Modify password
        final User user = userDAO.findByUserName(MainController.currentUserName(principal));
        user.setPassword(hPass);
        user.setDate(shortDate.format(new Date()));
        userDAO.save(user);
        LOGGER.info("Password changed for user: {}", user.getUserName());

        // Saving logs
        final User currentLogged = userDAO.findByUserName(MainController.currentUserName(principal));
        final Log log = new Log(currentLogged.getUserName(), "MDP MODIFIÉ", user.getUserName(), shortDate.format(new Date()), currentLogged.getRoles(), user.getRoles());
        logDAO.save(log);

        // Redirect depending on their role
        final User verif = userDAO.findByUserName(MainController.currentUserName(principal));
        if (verif.getRoles().equals("SADMIN")) {
            return "redirect:/sadmin";
        } else {
            return "redirect:/manager";
        }
    }
}
