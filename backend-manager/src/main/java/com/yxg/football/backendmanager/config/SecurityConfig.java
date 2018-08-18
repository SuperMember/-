package com.yxg.football.backendmanager.config;

import com.yxg.football.backendmanager.service.CustomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomUserService customUserService;
    @Autowired
    private EntryPointUnauthorizedHandler unauthorizedHandler;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //注意这里的密码加密格式，md5加密
        //数据库中的密码只要符合md5加密就可以实现自动匹配
        auth.userDetailsService(customUserService).passwordEncoder(md5PasswordEncoder());
    }

    @Bean
    public Md5PasswordEncoder md5PasswordEncoder() {
        Md5PasswordEncoder md5PasswordEncoder = new Md5PasswordEncoder();
        return md5PasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .headers().cacheControl().disable().and()
                .servletApi().and()
                //错误处理
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                //禁用session
                //注意一定要禁用session，否则会出现登录成功不带token也能访问
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                //allow all static resources
                .antMatchers("/").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/**/*.html").permitAll()
                .antMatchers("/**/*.css").permitAll()
                .antMatchers("/**/*.js").permitAll()
                .antMatchers("/index").permitAll()
                .antMatchers(HttpMethod.POST, "/auth").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .antMatchers("/auth/**").authenticated()
                .anyRequest().permitAll()
                //拦截器
                //.and()
                //.logout()
                //.and()
                //.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                //.logoutSuccessUrl("/login")
                .and().addFilterBefore(authenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }


    //验证jwt token
    @Bean
    public AuthenticationTokenFilter authenticationTokenFilter() throws Exception {
        AuthenticationTokenFilter authenticationTokenFilter = new AuthenticationTokenFilter();
        //之所以要设置manager是因为AbstractAuthenticationProcessingFilter并不完成认证逻辑，而是将其交给AuthenticationManager
        //AuthenticationTokenFilter继承UsernamePasswordAuthenticationFilter,而UsernamePasswordAuthenticationFilter继承AbstractAuthenticationProcessingFilter
        //所以如果没有设置manager会报错
        authenticationTokenFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationTokenFilter;
    }

    //不可缺少，缺少报错
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
