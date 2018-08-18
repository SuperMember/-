package com.yxg.football.backendweb.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;


@Data
public class User implements UserDetails {
    private Integer id;
    private String username;
    private String password;
    private String img;
    private String degree;
    private String phone;
    private Integer statue;
    private Integer sex;
    private Date created;
    private List<GrantedAuthority> grantedAuthorities;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getGrantedAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}




