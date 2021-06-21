package org.a84.visio.controller;

import org.a84.visio.model.Log;
import org.a84.visio.service.LogDAO;
import org.a84.visio.model.User;
import org.a84.visio.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.util.Date;

// HANDLER TO ADD / MODIFY / DELETE USERS FROM MySQL DB
@Controller
public class UserHandler {

    @Autowired
    UserDAO userDAO;
    @Autowired
    LogDAO logDAO;

    @RequestMapping(value = "/remove")
    public String remove(
            @RequestParam("id") int id,
            Model model,
            Principal principal) throws IOException {

        // Remove username
        User user = userDAO.findById(id);
        userDAO.deleteById(id);
        if (user.getRoles().equals("FORMATEUR")) {
            String username = user.getUserName();
            String co = "echo aaa| ssh -tt avenir@avenir843.pro.dns-orange.fr sudo prosodyctl deluser " + username + "@avenir843.pro.dns-orange.fr";
            Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", co});
        }
        // Add a log about the removed user
        DateFormat shortDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        User currentLogged = userDAO.findByUserName(MainController.currentUserName(principal));
        Log log = new Log(currentLogged.getUserName(), "SUPPRESSION", user.getUserName(), shortDate.format(new Date()), currentLogged.getRoles(), user.getRoles());
        logDAO.save(log);
        User verif = userDAO.findByUserName(MainController.currentUserName(principal));
        if (verif.getRoles().equals("SADMIN")) {
            return "redirect:/sadmin";
        } else {
            return "redirect:/manager";
        }
    }

    @RequestMapping(value = "/modify")
    public String modify(
            @RequestParam("id") int id,
            @RequestParam("role") String role,
            @RequestParam("password") String password,
            Model model,
            Principal principal) throws InterruptedException, IOException {

        DateFormat shortDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

        User user = userDAO.findById(id);
        String username = user.getUserName();

        // Modify User + Saving logs
        if (!user.getRoles().equals("FORMATEUR")) {
            user.setRoles(role);
            user.setDate(shortDate.format(new Date()));
            userDAO.save(user);
            User currentLogged = userDAO.findByUserName(MainController.currentUserName(principal));
            Log log = new Log(currentLogged.getUserName(), "RÔLE MODIFIÉ", user.getUserName(), shortDate.format(new Date()), currentLogged.getRoles(), user.getRoles());
            logDAO.save(log);
        }

        // Modifying FORMATEUR
        else {
            String co = "echo aaa| ssh -tt avenir@avenir843.pro.dns-orange.fr sudo prosodyctl deluser " + username + "@avenir843.pro.dns-orange.fr";
            Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", co});
            Thread.sleep(200);
            String co2 = "echo aaa| ssh -tt avenir@avenir843.pro.dns-orange.fr sudo prosodyctl register " + username + " avenir843.pro.dns-orange.fr " + password;
            Process p2 = Runtime.getRuntime().exec(new String[]{"bash", "-c", co2});
            User currentLogged = userDAO.findByUserName(MainController.currentUserName(principal));
            Log log = new Log(currentLogged.getUserName(), "MDP CHANGÉ", user.getUserName(), shortDate.format(new Date()), currentLogged.getRoles(), user.getRoles());
            logDAO.save(log);
        }

        // Redirect depending on their role
        User verif = userDAO.findByUserName(MainController.currentUserName(principal));
        if (verif.getRoles().equals("SADMIN")) {
            return "redirect:/sadmin";
        } else {
            return "redirect:/manager";
        }
    }

    @RequestMapping(value = "/add")
    public String add(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "password", required = true) String password,
            @RequestParam(name = "accessLevel", required = true) String accessLevel,
            Model model,
            Principal principal
    ) {
        // Check if user already exists
        User verif = userDAO.findByUserName(MainController.currentUserName(principal));

        if (!userDAO.existsByUserName(username)) {
            String salt = BCrypt.gensalt(12);
            String hPass = BCrypt.hashpw(password, salt);
            DateFormat shortDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            // Create user
            User user = new User(username, hPass, true, accessLevel, shortDate.format(new Date()));
            userDAO.save(user);
            // Save logs
            User currentLogged = userDAO.findByUserName(MainController.currentUserName(principal));
            Log log = new Log(currentLogged.getUserName(), "AJOUT", user.getUserName(), shortDate.format(new Date()), currentLogged.getRoles(), user.getRoles());
            logDAO.save(log);
        }

        // Redirect
        if (verif.getRoles().equals("SADMIN")) {
            return "redirect:/sadmin";
        } else {
            return "redirect:/manager";
        }

    }

    @PostMapping(value = "/bashadd")
    public String account(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "password") String password,
            Model model,
            Principal principal) throws IOException, InterruptedException {
        User user = userDAO.findByUserName(MainController.currentUserName(principal));
        DateFormat shortDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        // Check if user already exists
        if (!userDAO.existsByUserName(username)) {

            if (username != null && password != null) {

                String hPass = BCrypt.hashpw(password, BCrypt.gensalt(12));
                User acc = new User(username, hPass, true, "FORMATEUR", shortDate.format(new Date()));
                userDAO.save(acc);
                String co = "echo aaa| ssh -tt avenir@avenir843.pro.dns-orange.fr sudo prosodyctl register " + username + " avenir843.pro.dns-orange.fr " + password;
                Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", co});
                // Save logs
                User currentLogged = userDAO.findByUserName(MainController.currentUserName(principal));
                Log log = new Log(currentLogged.getUserName(), "AJOUT", acc.getUserName(), shortDate.format(new Date()), currentLogged.getRoles(), acc.getRoles());
                logDAO.save(log);

            }
        }
        if (user.getRoles().equals("SADMIN")) {
            return "redirect:/sadmin";
        } else {
            return "redirect:/manager";
        }
    }

    @RequestMapping("/pwd")
    public String pwd(
            @RequestParam("password") String password,
            Principal principal) {

        DateFormat shortDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        String hPass = BCrypt.hashpw(password, BCrypt.gensalt(12));
        // Modify password
        User user = userDAO.findByUserName(MainController.currentUserName(principal));
        user.setPassword(hPass);
        user.setDate(shortDate.format(new Date()));
        userDAO.save(user);

        // Saving logs
        User currentLogged = userDAO.findByUserName(MainController.currentUserName(principal));
        Log log = new Log(currentLogged.getUserName(), "MDP MODIFIÉ", user.getUserName(), shortDate.format(new Date()), currentLogged.getRoles(), user.getRoles());

        logDAO.save(log);

        // Redirect depending on their role
        User verif = userDAO.findByUserName(MainController.currentUserName(principal));
        if (verif.getRoles().equals("SADMIN")) {
            return "redirect:/sadmin";
        } else {
            return "redirect:/manager";
        }
    }
}
