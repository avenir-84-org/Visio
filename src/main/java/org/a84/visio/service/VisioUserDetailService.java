package org.a84.visio.service;

import org.a84.visio.model.User;
import org.a84.visio.model.VisioUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class VisioUserDetailService implements UserDetailsService {

    private final UserDAO userDAO;

    public VisioUserDetailService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User u = userDAO.findByUserName(userName);
        if (u == null) {
            throw new UsernameNotFoundException("Not found: " + userName);
        }
        return new VisioUserDetails(u);
    }

}
