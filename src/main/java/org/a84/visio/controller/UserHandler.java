package org.a84.visio.controller;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.a84.visio.model.Log;
import org.a84.visio.service.LogDAO;
import org.a84.visio.model.User;
import org.a84.visio.service.UserDAO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.util.Date;

@Controller
@RequiredArgsConstructor
public class UserHandler {

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
     * Remove user from db.
     * @param id - int
     * @param model - model
     * @param principal - principal
     * @return view
     * @throws IOException - except
     */
    @RequestMapping(value = "/remove")
    public String remove(
            @RequestParam("id") final int id,
            final Model model,
            final Principal principal) throws IOException {

        final User user = userDAO.findById(id);
        userDAO.deleteById(id);
        if (user.getRoles().equals("FORMATEUR")) {
            final String username = user.getUserName();
            final String co = "echo aaa| ssh -tt avenir@avenir843.pro.dns-orange.fr sudo prosodyctl deluser " + username + "@avenir843.pro.dns-orange.fr";
            final Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", co});
        }
        final DateFormat shortDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        final User currentLogged = userDAO.findByUserName(MainController.currentUserName(principal));
        final Log log = new Log(currentLogged.getUserName(), "SUPPRESSION", user.getUserName(), shortDate.format(new Date()), currentLogged.getRoles(), user.getRoles());
        logDAO.save(log);
        final User verif = userDAO.findByUserName(MainController.currentUserName(principal));
        if (verif.getRoles().equals("SADMIN")) {
            return "redirect:/sadmin";
        } else {
            return "redirect:/manager";
        }
    }

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
                userDAO.save(acc);
                final String co = "echo aaa| ssh -tt avenir@avenir843.pro.dns-orange.fr sudo prosodyctl register " + username + " avenir843.pro.dns-orange.fr " + password;
                final Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", co});
                // Save logs
                final User currentLogged = userDAO.findByUserName(MainController.currentUserName(principal));
                final Log log = new Log(currentLogged.getUserName(), "AJOUT", acc.getUserName(), shortDate.format(new Date()), currentLogged.getRoles(), acc.getRoles());
                logDAO.save(log);
            }
        }
        if (user.getRoles().equals("SADMIN")) {
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
