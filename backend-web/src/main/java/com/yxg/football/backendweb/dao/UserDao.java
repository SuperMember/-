package com.yxg.football.backendweb.dao;


import com.yxg.football.backendweb.entity.Permission;
import com.yxg.football.backendweb.entity.Role;
import com.yxg.football.backendweb.entity.User;

import java.util.List;
import java.util.Map;

public interface UserDao {
    public User getUserByName(String username);

    public List<Role> getRolesByName(String username);

    public List<Permission> getPermissionsByRoleName(String roleName);

    public Integer updateUserInfo(Map<String, Object> params);

    public Boolean checkPhone(String phone);

    public Integer createUser(Map<String, Object> params);

    public Boolean checkUserName(String username);

    List<Map<String, Object>> getUserInfo(Integer userId);

    Integer updateSecret(String phone, String password);

    Integer focusUser(Integer userId, Integer beuserId);

    Integer unFocusUser(Integer userId, Integer beuserId);

    //获取用户关注的用户群
    List<Map<String, Object>> getFocusUser(Integer userId);

    //获取用户的粉丝
    List<Map<String, Object>> getFans(Integer userId);

    //获取用户积分和状态
    Integer getUserStatue(Integer userId);

    //获取用户积分
    Integer getUserPoint(Integer userId);

    //更新角色表
    Integer saveRole(String username, Integer userId);

    //判断是否有圈主角色
    Boolean checkRole(Integer cId);


}
