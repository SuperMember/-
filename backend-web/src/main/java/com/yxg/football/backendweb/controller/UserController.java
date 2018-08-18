package com.yxg.football.backendweb.controller;


import com.yxg.football.backendweb.config.JwtUtil;
import com.yxg.football.backendweb.entity.Result;
import com.yxg.football.backendweb.entity.User;
import com.yxg.football.backendweb.enums.RoleEnum;
import com.yxg.football.backendweb.service.CustomUserService;
import com.yxg.football.backendweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
        return new ResponseEntity<Object>(Result.genSuccessResult(token), HttpStatus.OK);
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
    * 修改密码验证手机号
    * */
    @PostMapping("/phone/secret")
    public ResponseEntity<?> secretPhone(@RequestBody Map<String, Object> params) {
        String phone = (String) params.get("phone");
        userService.getCode(phone);
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

    /*
    * 检测用户是否为圈主
    * */
    @GetMapping("/check/role")
    public ResponseEntity<?> checkRole(Integer cId) {
        if (userService.checkRole(cId)) {
            return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
        }
        return new ResponseEntity<Object>(Result.genFailResult("没有该角色"), HttpStatus.OK);
    }

    /*
    * 修改密码
    * */
    @PutMapping("/secret")
    public ResponseEntity<?> updateSecret(@RequestBody Map<String, Object> params) throws Exception {
        userService.updateSecret((String) params.get("phone"), (String) params.get("password"));
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

    /*
    * 修改用户信息
    * */
    @PutMapping("/info")
    public ResponseEntity<?> editInfo(@RequestBody Map<String, Object> params) {
        try {
            userService.updateUserInfo(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

    /*
    * 创建用户
    * */
    @PostMapping("/info")
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> params) throws Exception {
        userService.createUser(params);
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

    /*
    * 检测用户名唯一性
    * */
    @PostMapping("/check/name")
    public ResponseEntity<?> checkUserName(@RequestBody Map<String, Object> params) {
        if (userService.checkUserName(params)) {
            //存在
            return new ResponseEntity<Object>(Result.genFailResult("用户名已存在"), HttpStatus.OK);
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

    /*
    * 获取用户信息
    * */
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo() throws Exception {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<Object>(Result.genSuccessResult(userService.getUserInfo(user.getId())), HttpStatus.OK);
    }

    /*
    * 获取某个用户的信息
    * */
    @GetMapping("/one/info")
    public ResponseEntity<?> getOneUserInfo(Integer userId) throws Exception {
        Map<String, Object> userInfo = userService.getUserInfo(userId);
        return new ResponseEntity<Object>(Result.genSuccessResult(userInfo), HttpStatus.OK);
    }


    /*
    * 关注用户
    * */
    @PostMapping("/focus")
    public ResponseEntity<?> focusUser(@RequestBody Map<String, Object> params) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userService.focusUser(user.getId(), (Integer) params.get("beuserId"))) {
            return new ResponseEntity<Object>(Result.genSuccessResult("关注成功"), HttpStatus.OK);
        }
        return new ResponseEntity<Object>(Result.genFailResult("取消成功"), HttpStatus.OK);
    }

    /*
  * 判断用户是否关注过某个用户
  * */
    @GetMapping("/focus/exist/{beuserId}")
    public ResponseEntity<?> isFocusCircle(@PathVariable Integer beuserId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userService.isFocusUser(user.getId(), beuserId)) {
            return new ResponseEntity<Object>(Result.genSuccessResult("已经关注"), HttpStatus.OK);
        }
        return new ResponseEntity<Object>(Result.genFailResult("未关注"), HttpStatus.OK);
    }

    /*
    * 获取用户关注的群体
    * */
    @GetMapping("/focus/user")
    public ResponseEntity<?> getFocusUser(@RequestParam(required = false) Integer userId) {
        Integer id;
        if (userId == null) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            id = user.getId();
        } else {
            id = userId;
        }
        List<Map<String, Object>> result = userService.getFocusUser(id);
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 获取粉丝
    * */
    @GetMapping("/fans")
    public ResponseEntity<?> getFans(@RequestParam(required = false) Integer userId) {
        Integer id;
        if (userId == null) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            id = user.getId();
        } else {
            id = userId;
        }
        List<Map<String, Object>> result = userService.getFans(id);
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

}
