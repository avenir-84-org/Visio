package org.a84.visio.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class VisioUserDetails implements UserDetails {

    private String userName;
    private String password;
    private int id;
    private boolean active;
    private List<GrantedAuthority> authorities;

    public VisioUserDetails() {
    }

    public VisioUserDetails(User u) {
        this.userName = u.getUserName();
        this.password = u.getPassword();
        this.id = u.getId();
        this.active = u.isActive();
        String[] roles = u.getRoles().split(",");
        authorities = new LinkedList<GrantedAuthority>();
        for (int i = 0; i < roles.length; i++) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roles[i]));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
