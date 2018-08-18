package com.yxg.football.backendweb.aop;

import com.yxg.football.backendweb.dao.UserDao;
import com.yxg.football.backendweb.entity.User;
import com.yxg.football.backendweb.enums.PermissionEnum;
import com.yxg.football.backendweb.exceptions.PermissionException;
import com.yxg.football.backendweb.service.CircleService;
import com.yxg.football.backendweb.service.CommentService;
import com.yxg.football.backendweb.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/*
* 权限控制(判断用户是否有权限进行评论和发帖)
* */
@Component
@Aspect
public class PermissionAspect {
    @Autowired
    UserService userService;

    //匹配圈子发帖情况
    @Pointcut("execution(* com.yxg.football.backendweb.service.CircleService.insert*(..)) || execution(* com.yxg.football.backendweb.service.CircleService.create*(..))")
    public void circleService() {

    }

    //匹配评论情况
    @Pointcut("execution(* com.yxg.football.backendweb.service.CommentService.insert*(..)) || execution(* com.yxg.football.backendweb.service.CommentService.reply*(..))")
    public void commentService() {

    }

    //判断用户的情况是否可以进行评论或发帖
    @Before("commentService() || circleService()")
    public void permission(JoinPoint joinPoint) throws PermissionException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer id = user.getId();
        if (userService.getUserStatue(id) == 1) {
            //小黑屋
            //不允许评论
            throw new PermissionException(PermissionEnum.USER_LOCK.getMsg());
        }
    }


}
