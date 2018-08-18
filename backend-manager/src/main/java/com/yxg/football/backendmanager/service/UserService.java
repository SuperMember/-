package com.yxg.football.backendmanager.service;

import com.yxg.football.backendmanager.entity.Permission;
import com.yxg.football.backendmanager.entity.Role;
import com.yxg.football.backendmanager.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<Map<String, Object>> getAllUser(Integer statue, Integer page, Integer size);

    Integer setUserStatue(Integer userId, Integer statue);

    List<Map<String, Object>> getCommentByUserId(Integer userId, Integer page, Integer size);

    List<Map<String, Object>> getUserById(Integer userId);

    Integer getUserCount(Integer statue);

    Boolean logout(String token);

    User getUserByName(String username);

    List<Role> getRolesByName(String userName);

    List<Permission> getPermissionsByRoleName(String roleName);

    Integer updateProfile(Map<String, Object> params);

    String checkCode(String phone, String code);

    String getCode(String phone);

    Boolean checkPhone(String phone);


}
