package org.a84.visio.model;

import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String userName, action, actionUserName, role, actionRole;
    private String date;

    public Log() {
    }

    public Log(String username, String action, String actionUserName, String date, String role, String actionRole) {
        this.userName = username;
        this.action = action;
        this.actionUserName = actionUserName;
        this.date = date;
        this.role = role;
        this.actionRole = actionRole;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActionRole() {
        return actionRole;
    }

    public void setActionRole(String actionRole) {
        this.actionRole = actionRole;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setActionUserName(String actionUserName) {
        this.actionUserName = actionUserName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionUserName() {
        return actionUserName;
    }

    public void setActionUsername(String actionUserName) {
        this.actionUserName = actionUserName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
