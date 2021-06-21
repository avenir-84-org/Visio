package org.a84.visio.bootstrap;

import org.a84.visio.model.Log;
import org.a84.visio.service.LogDAO;
import org.a84.visio.model.User;
import org.a84.visio.service.UserDAO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

@Component
public class InitLoader implements CommandLineRunner {


    private final UserDAO userDAO;
    private final LogDAO logDAO;

    public InitLoader(UserDAO userDAO, LogDAO logDAO) {
        this.userDAO = userDAO;
        this.logDAO = logDAO;
    }


    @Override
    public void run(String... args) throws Exception {
        List<User> users = userDAO.findAll();
        if (users.isEmpty()) {
            System.out.println("Adding admin");
            DateFormat shortDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        // Creating an ADMIN ACCOUNT at launch
            String admin_pw = BCrypt.hashpw("aaa", BCrypt.gensalt(12));
            User admin = new User("admin", admin_pw, true, "SADMIN", shortDate.format(new Date()));
            Log log = new Log("initializer", "AJOUT", admin.getUserName(), shortDate.format(new Date()), "BOSS", "SADMIN");
            userDAO.save(admin);
            logDAO.save(log);
            System.out.println("Done");
        }
    }

}
