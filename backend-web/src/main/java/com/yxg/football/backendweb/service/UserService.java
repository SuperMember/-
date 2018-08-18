package com.yxg.football.backendweb.service;


import com.yxg.football.backendweb.entity.Permission;
import com.yxg.football.backendweb.entity.Role;
import com.yxg.football.backendweb.entity.User;
import org.apache.xpath.operations.Bool;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {
    public User getUserByName(String username);

    public List<Role> getRolesByName(String userName);

    public List<Permission> getPermissionsByRoleName(String roleName);

    public Integer updateUserInfo(Map<String, Object> params);

    public Boolean checkPhone(String phone);

    public Integer createUser(Map<String, Object> params) throws Exception;

    public String getCode(String phone);

    public String checkCode(String phone, String code);

    public Map<String, Object> getUserInfo(Integer userId);

    public Integer updateSecret(String phone, String password);

    Boolean focusUser(Integer userId, Integer beuserId);

    Boolean isFocusUser(Integer userId, Integer beuserId);

    //获取用户关注的用户群
    List<Map<String, Object>> getFocusUser(Integer userId);

    //获取用户的粉丝
    List<Map<String, Object>> getFans(Integer userId);

    Integer getUserStatue(Integer userId);

    Integer getUserPoint(Integer userId);

    //用户名唯一性
    Boolean checkUserName(Map<String, Object> params);

    /*
    * 判断是否有某一角色
    * */
    Boolean checkRole(Integer cId);
}
