package org.a84.visio.bootstrap;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
@RequiredArgsConstructor
public class InitLoader implements CommandLineRunner {
    /**
     * User DAO Service.
     */
    private final @NonNull UserDAO userDAO;
    /**
     * Log DAO Service.
     */
    private final @NonNull LogDAO logDAO;
    /**
     * Pass encoder.
     */
    private final @NonNull PasswordEncoder passwordEncoder;
    /**
     * Init db.
     * @throws Exception - exception
     */
    @Override
    public void run(String... args) throws Exception {
        final List<User> users = userDAO.findAll();
        if (users.isEmpty()) {
            LOGGER.info("Populating database...");
            final DateFormat shortDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            final String admin_pw = passwordEncoder.encode("aaa");
            final User admin = new User("admin", admin_pw, true, "SADMIN", shortDate.format(new Date()));
            final User admin2 = new User("admin2", admin_pw, true, "MANAGER", shortDate.format(new Date()));
            final Log log = new Log("initializer", "AJOUT", admin.getUserName(), shortDate.format(new Date()), "BOSS", "SADMIN");
            userDAO.save(admin);
            userDAO.save(admin2);
            logDAO.save(log);
            LOGGER.info("Done.");
        }
    }

}
