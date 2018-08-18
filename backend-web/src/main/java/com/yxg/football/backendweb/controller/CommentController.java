package com.yxg.football.backendweb.controller;


import com.yxg.football.backendweb.entity.Result;
import com.yxg.football.backendweb.entity.User;
import com.yxg.football.backendweb.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
* 有关排行榜积分说明
* 发帖子积2分，留言积1分
* 每日更新
* 12点重新计算
* */
@RestController
@RequestMapping("/football/api/comment")
public class CommentController {
    @Autowired
    CommentService commentService;

    @GetMapping("/list/{type}/{id}")
    public ResponseEntity<?> getComments(@PathVariable("type") Integer type,
                                         @PathVariable("id") Integer id,
                                         @RequestParam(defaultValue = "DESC", required = false) String order,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10", required = false) Integer size) throws Exception {
        Map<String, Object> resultMap = null;
        resultMap = commentService.getComments(type, id, page, size, order);
        return new ResponseEntity<Object>(Result.genSuccessResult(resultMap), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") Integer id) throws Exception {
        commentService.deleteComment(id);
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

    //根据id获取某条评论
    @GetMapping("/one")
    public ResponseEntity<?> getCommentById(Integer id) throws Exception {
        Map<String, Object> result = null;
        result = commentService.getCommentById(id);
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }


    //获取用户所有评论
    @GetMapping("/")
    public ResponseEntity<?> getCommentsByUserId(@RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10", required = false) Integer size) throws Exception {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Map<String, Object>> result = commentService.getCommentsByUserId(user.getId(), page, size);
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    //获取其他用户的所有评论
    @GetMapping("/one/all")
    public ResponseEntity<?> getOneCommentsByUserId(Integer userId,
                                                    @RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10", required = false) Integer size) throws Exception {
        List<Map<String, Object>> result = commentService.getCommentsByUserId(userId, page, size);
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }


    //评论
    @PostMapping("/list")
    public ResponseEntity<?> insertComment(@RequestBody Map<String, Object> params) throws Exception {
        commentService.insertComment(params);
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

    //回复
    @PostMapping("/reply")
    public ResponseEntity<?> setReplyComment(@RequestBody Map<String, Object> params) throws Exception {
        commentService.replyComment(params);
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

    //获取回复的数据
    @GetMapping("/reply")
    public ResponseEntity<?> getReplyComment(@RequestParam(defaultValue = "DESC", required = false) String order,
                                             @RequestParam Integer commentId,
                                             @RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer size) throws Exception {
        List<Map<String, Object>> resultList = null;
        resultList = commentService.getAllReply(commentId, page, size, order);
        return new ResponseEntity<Object>(Result.genSuccessResult(resultList), HttpStatus.OK);
    }

    /*
    * 点赞评论
    * */
    @PostMapping("/clike")
    public ResponseEntity<?> likeComment(@RequestBody Map<String, Object> params) throws Exception {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getId();
        Integer commentId = (Integer) params.get("commentId");
        if (commentService.likeComment(userId, commentId)) {
            return new ResponseEntity<Object>(Result.genSuccessResult("点赞成功"), HttpStatus.OK);
        }
        return new ResponseEntity<Object>(Result.genFailResult("你已经点过赞"), HttpStatus.OK);
    }

    /*
    * 点赞回复
    * */
    @PostMapping("/rlike")
    public ResponseEntity<?> likeReply(@RequestBody Map<String, Object> params) throws Exception {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getId();
        Integer replyId = (Integer) params.get("replyId");
        if (commentService.likeReply(userId, replyId)) {
            return new ResponseEntity<Object>(Result.genSuccessResult("点赞成功"), HttpStatus.OK);
        }
        return new ResponseEntity<Object>(Result.genFailResult("你已经点过赞"), HttpStatus.OK);
    }
}
