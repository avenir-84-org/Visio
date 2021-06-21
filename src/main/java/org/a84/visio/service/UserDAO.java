package org.a84.visio.service;

import org.a84.visio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDAO extends JpaRepository<User, Integer> {
    User findByUserName(String userName);
    User findById(int id);
    List<User> findAll();
    User deleteByUserName(String userName);
    List<User> findByRoles(String roles);
    Boolean existsByUserName(String userName);
}
