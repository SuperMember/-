package com.yxg.football.backendweb.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.yxg.football.backendweb.dao.RedisDao;
import com.yxg.football.backendweb.dao.UserDao;
import com.yxg.football.backendweb.entity.Permission;
import com.yxg.football.backendweb.entity.Role;
import com.yxg.football.backendweb.entity.User;
import com.yxg.football.backendweb.enums.UserExceptionEnum;
import com.yxg.football.backendweb.exceptions.UserException;
import com.yxg.football.backendweb.service.UserService;
import com.yxg.football.backendweb.util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Value("${code.get.url}")
    String url;

    @Value("${code.check.url}")
    String checkUrl;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    Gson gson;
    @Autowired
    RedisDao redisDao;

    public User getUserByName(String username) {
        return userDao.getUserByName(username);
    }

    public List<Role> getRolesByName(String userName) {
        return userDao.getRolesByName(userName);
    }

    public List<Permission> getPermissionsByRoleName(String roleName) {
        return userDao.getPermissionsByRoleName(roleName);
    }

    @Override
    public Integer updateUserInfo(Map<String, Object> params) {
        return userDao.updateUserInfo(params);
    }

    /*
    * 检测手机号是否有效
    * true表示占用,false表示可用
    * */
    @Override
    public Boolean checkPhone(String phone) {
        if (userDao.checkPhone(phone)) {
            return true;
        }
        //发送验证码
        getCode(phone);
        return false;
    }

    @Transactional
    @Override
    public Integer createUser(Map<String, Object> params) throws Exception {
        if (userDao.checkUserName((String) params.get("username"))) {
            //存在用户名
            throw new UserException(UserExceptionEnum.USER_EXIST.getMsg());
        }
        Integer userId = userDao.createUser(params);
        //更新角色表
        return userDao.saveRole((String) params.get("username"), userId);
    }

    /*
    * 发送验证码
    * */
    @Override
    public String getCode(String phone) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-LC-Id", Const.APPID);
        headers.set("X-LC-Key", Const.APPKEY);
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("mobilePhoneNumber", phone);
        HttpEntity<String> entity = new HttpEntity<String>(jsonParam.toJSONString(), headers);
        String result = restTemplate.postForObject(url, entity, String.class);
        return result;
    }

    /*
    * 检测验证码
    * */
    @Override
    public String checkCode(String phone, String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-LC-Id", Const.APPID);
        headers.set("X-LC-Key", Const.APPKEY);
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("mobilePhoneNumber", phone);
        HttpEntity<String> entity = new HttpEntity<String>(jsonParam.toJSONString(), headers);
        String result = restTemplate.postForObject(checkUrl + code, entity, String.class);
        return result;
    }

    /*
    * 获取用户信息
    * */
    @Override
    public Map<String, Object> getUserInfo(Integer userId) {
        List<Map<String, Object>> result = userDao.getUserInfo(userId);
        if (result != null && result.size() != 0) {
            return result.get(0);
        }
        return null;
    }


    /*
    * 修改密码
    * */
    @Override
    public Integer updateSecret(String phone, String password) {
        return userDao.updateSecret(phone, password);
    }


    //关注用户
    @Override
    public Boolean focusUser(Integer userId, Integer beuserId) {
        //判断是否关注过
        //redis
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (redisDao.focusUser(user.getId(), beuserId)) {
            //未关注
            userDao.focusUser(user.getId(), beuserId);
            return true;
        }
        //取消关注
        userDao.unFocusUser(user.getId(), beuserId);
        return false;
    }

    /*
    * 检测用户名唯一性
    * */


    @Override
    public Boolean isFocusUser(Integer userId, Integer beuserId) {
        return redisDao.isFocusUser(userId, beuserId);
    }

    @Override
    public List<Map<String, Object>> getFocusUser(Integer userId) {
        return userDao.getFocusUser(userId);
    }

    @Override
    public List<Map<String, Object>> getFans(Integer userId) {
        return userDao.getFans(userId);
    }

    @Override
    public Integer getUserStatue(Integer userId) {
        return userDao.getUserStatue(userId);
    }

    @Override
    public Integer getUserPoint(Integer userId) {
        return userDao.getUserPoint(userId);
    }

    @Override
    public Boolean checkUserName(Map<String, Object> params) {
        return userDao.checkUserName((String) params.get("username"));
    }

    /*
    * 判断是否有圈主角色
    * */
    @Override
    public Boolean checkRole(Integer cId) {
        return userDao.checkRole(cId);
    }

}
