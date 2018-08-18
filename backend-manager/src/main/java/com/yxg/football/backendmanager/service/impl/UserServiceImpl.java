package com.yxg.football.backendmanager.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yxg.football.backendmanager.dao.UserDao;
import com.yxg.football.backendmanager.entity.Permission;
import com.yxg.football.backendmanager.entity.Role;
import com.yxg.football.backendmanager.entity.User;
import com.yxg.football.backendmanager.service.UserService;
import com.yxg.football.backendmanager.util.Const;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;
    @Value("${code.check.url}")
    String checkUrl;
    @Value("${code.get.url}")
    String url;
    @Autowired
    RestTemplate restTemplate;

    @Override
    public List<Map<String, Object>> getAllUser(Integer statue, Integer page, Integer size) {
        return userDao.getAllUser(statue, page, size);
    }

    @Override
    public Integer setUserStatue(Integer userId, Integer statue) {
        return userDao.setUserStatue(userId, statue);
    }

    @Override
    public List<Map<String, Object>> getCommentByUserId(Integer userId, Integer page, Integer size) {
        return userDao.getCommentByUserId(userId, page, size);
    }


    @Override
    public List<Map<String, Object>> getUserById(Integer userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public Integer getUserCount(Integer statue) {
        return userDao.getUsersCount(statue);
    }


    //登出
    @Override
    public Boolean logout(String token) {
        return null;
    }


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
    public Integer updateProfile(Map<String, Object> params) {
        String url = (String) params.get("img");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDao.updateProfile(url, user.getId());
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

}
