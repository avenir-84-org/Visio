package org.a84.visio.bootstrap;

import org.a84.visio.model.Log;
import org.a84.visio.service.LogDAO;
import org.a84.visio.model.User;
import org.a84.visio.service.UserDAO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

@Component
public class InitLoader implements CommandLineRunner {
    /**
     * User DAO Service.
     */
    private final UserDAO userDAO;
    /**
     * Log DAO Service.
     */
    private final LogDAO logDAO;
    /**
     * Pass encoder.
     */
    private final PasswordEncoder passwordEncoder;
    /**
     * Constructor.
     */
    public InitLoader(UserDAO userDAO, LogDAO logDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.logDAO = logDAO;
        this.passwordEncoder = passwordEncoder;
    }
    /**
     * Init db.
     * @throws Exception - exception
     */
    @Override
    public void run(String... args) throws Exception {
        final List<User> users = userDAO.findAll();
        if (users.isEmpty()) {
            System.out.println("Adding admin");
            final DateFormat shortDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            final String admin_pw = passwordEncoder.encode("aaa");
            final User admin = new User("admin", admin_pw, true, "SADMIN", shortDate.format(new Date()));
            final Log log = new Log("initializer", "AJOUT", admin.getUserName(), shortDate.format(new Date()), "BOSS", "SADMIN");
            userDAO.save(admin);
            logDAO.save(log);
            System.out.println("Done");
        }
    }

}
