package com.yxg.football.backendmanager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@Component
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response, AuthenticationException e)
            throws IOException, ServletException {
        // 当没经过身份认证的用户访问一个受保护接口时
        // 返回一个401响应代替默认的重定向到登录页面
        logger.info(e.getMessage());
        HashMap<String, Object> obj = new HashMap<String, Object>();
        obj.put("status", "401");
        obj.put("error", "授权失败");
        obj.put("message", "帐号或密码错误");

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), obj);
    }
}