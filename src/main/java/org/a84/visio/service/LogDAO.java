package org.a84.visio.service;

import org.a84.visio.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogDAO extends JpaRepository<Log, Integer> {
}
