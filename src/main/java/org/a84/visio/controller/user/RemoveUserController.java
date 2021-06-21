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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.util.Date;

@Controller
@RequiredArgsConstructor
@Log4j2
public class RemoveUserController {
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
     * @param principal - principal
     * @return view
     * @throws IOException - except
     */
    @RequestMapping(value = "/remove")
    public String remove(
            @RequestParam("id") final int id,
            final Principal principal) throws IOException {

        final User user = userDAO.findById(id);
        userDAO.deleteById(id);
        LOGGER.info("Removed user: {}", user.getUserName());
        if (user.getRoles().equals("FORMATEUR")) {
            final String username = user.getUserName();
            final String co = "echo aaa| ssh -tt avenir@avenir843.pro.dns-orange.fr sudo prosodyctl deluser " + username + "@avenir843.pro.dns-orange.fr";
            final Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", co});
            LOGGER.info("Removed formateur: {}", user.getUserName());
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
}
