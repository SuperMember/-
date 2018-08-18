package com.yxg.football.backendweb.service;


import com.yxg.football.backendweb.entity.Role;
import com.yxg.football.backendweb.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserService implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User sysUser = userService.getUserByName(s);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        //获取角色
        sysUser.setGrantedAuthorities(this.getAuthorities(s));
        return sysUser;
    }

    public List<GrantedAuthority> getAuthorities(String username) {
        List<GrantedAuthority> list = new ArrayList<>();
        List<Role> roles = userService.getRolesByName(username);
        for (Role role : roles) {
            list.add(new SimpleGrantedAuthority(role.getRolename()));
        }
        return list;
    }
}
