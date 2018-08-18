package com.yxg.football.backendmanager.controller;


import com.yxg.football.backendmanager.config.JwtUtil;
import com.yxg.football.backendmanager.entity.Result;
import com.yxg.football.backendmanager.entity.User;
import com.yxg.football.backendmanager.service.CustomUserService;
import com.yxg.football.backendmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    CustomUserService customUserService;
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> params) {
        //进行验证
        Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                (String) params.get("username"),
                (String) params.get("password")
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //生成jwt
        User sysUser = (User) customUserService.loadUserByUsername((String) params.get("username"));
        String token = jwtUtil.generateToken(sysUser);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        return new ResponseEntity<Object>(Result.genSuccessResult(resultMap), HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<?> getInfo() {
        User user = null;
        try {
            user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<String> result = new ArrayList<>();
            List<GrantedAuthority> map = user.getGrantedAuthorities();
            result.add(map.get(0).getAuthority());
            user.setRole(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(user), HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

    @PostMapping("/profile")
    public ResponseEntity<?> profile(@RequestBody Map<String, Object> params) {
        try {
            userService.updateProfile(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }


    /*
    * 获取手机验证码
    * */
    @PostMapping("/phone")
    public ResponseEntity<?> phone(@RequestBody Map<String, Object> params) {
        try {
            String phone = (String) params.get("phone");
            if (userService.checkPhone(phone)) {
                return new ResponseEntity<Object>(Result.genFailResult("手机号已被占用"), HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

    /*
    * 提交验证
    * */
    @PostMapping("/phone/check")
    public ResponseEntity<?> checkCode(@RequestBody Map<String, Object> params) {
        try {
            String phone = (String) params.get("phone");
            String code = (String) params.get("code");
            userService.checkCode(phone, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

}
