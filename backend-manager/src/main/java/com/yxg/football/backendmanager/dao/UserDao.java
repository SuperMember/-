package com.yxg.football.backendmanager.dao;

import com.yxg.football.backendmanager.entity.Permission;
import com.yxg.football.backendmanager.entity.Role;
import com.yxg.football.backendmanager.entity.User;

import java.util.List;
import java.util.Map;

public interface UserDao {
    User getUserByName(String username);

    List<Role> getRolesByName(String username);

    List<Permission> getPermissionsByRoleName(String roleName);

    List<Map<String, Object>> getAllUser(Integer statue, Integer page, Integer size);

    Integer setUserStatue(Integer userId, Integer statue);

    List<Map<String, Object>> getCommentByUserId(Integer userId, Integer page, Integer size);

    List<Map<String, Object>> getUserById(Integer userId);

    Integer getUsersCount(Integer statue);

    Integer updateProfile(String url, Integer userId);

    Boolean checkPhone(String phone);

}
