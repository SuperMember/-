package com.yxg.football.backendmanager.controller;


import com.yxg.football.backendmanager.entity.Result;
import com.yxg.football.backendmanager.entity.User;
import com.yxg.football.backendmanager.service.ArticleService;
import com.yxg.football.backendmanager.service.CommentService;
import com.yxg.football.backendmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* 管理员页面
* */
@RestController
@RequestMapping("/admin/manager")
public class AdminController {
    @Autowired
    CommentService commentService;
    @Autowired
    UserService userService;
    @Autowired
    ArticleService articleService;

    /*
    * 管理员权限获取全部评论
    * */
    @GetMapping("/comment")
    public ResponseEntity<?> getAllComment(Integer statue,
                                           Integer type,
                                           @RequestParam(defaultValue = "0") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size) {
        List<Map<String, Object>> resultList = null;
        try {
            resultList = commentService.getAllComment(statue, type, page, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", resultList);
        result.put("count", commentService.getCommentCount());
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 删除评论(评论违规)
    * */
    @DeleteMapping("/comment")
    public ResponseEntity<?> deleteComment(@RequestBody Map<String, Object> params) {
        try {
            commentService.deleteComment((Integer) params.get("id"), (Integer) params.get("userId"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

    /*
    * 删除回复
    * */
    @DeleteMapping("/reply")
    public ResponseEntity<?> deleteReply(@RequestBody Map<String, Object> params) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            commentService.deleteReply((Integer) params.get("replyId"), user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

    /*
    * 根据id获取评论的所有回复
    * */
    @GetMapping("/reply")
    public ResponseEntity<?> getReplyById(Integer commentId,
                                          @RequestParam(defaultValue = "0") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size) {
        List<Map<String, Object>> result = null;
        try {
            result = commentService.getReplyById(commentId, page, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", result);
        resultMap.put("count", commentService.getReplyCount(commentId));
        return new ResponseEntity<Object>(Result.genSuccessResult(resultMap), HttpStatus.OK);
    }

    /*
    * 获取所有用户
    * */
    @GetMapping("/users/{statue}")
    public ResponseEntity<?> getAllUser(@PathVariable("statue") Integer statue,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size) {
        List<Map<String, Object>> resultList = null;
        try {
            resultList = userService.getAllUser(statue, page, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", resultList);
        result.put("count", userService.getUserCount(statue));
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }


    /*
    * 获取某个用户的所有评论
    * */
    @GetMapping("/user/comment")
    public ResponseEntity<?> getCommentByUserId(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size) {
        User user = null;
        List<Map<String, Object>> resultList = null;
        try {
            user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            resultList = userService.getCommentByUserId(user.getId(), page, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", resultList);
        resultMap.put("count", commentService.getCommentByUserIdCount(user.getId()));
        return new ResponseEntity<Object>(Result.genSuccessResult(resultMap), HttpStatus.OK);
    }

    /*
    * 回复用户的评论
    * */
    @PostMapping("/user/comment")
    public ResponseEntity<?> replyComment(@RequestBody Map<String, Object> params) {
        User user = null;
        try {
            user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            commentService.replyComment(user.getId(), params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

    /*
    * 获取用户的所有回复
    * */
    @GetMapping("/user/reply")
    public ResponseEntity<?> getReplyByUserId(@RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size) {
        List<Map<String, Object>> resultList = null;
        Map<String, Object> resultMap = new HashMap<>();
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            resultList = commentService.getReplyByUserId(user.getId(), page, size);
            resultMap.put("data", resultList);
            resultMap.put("count", commentService.getReplyByUserIdCount(user.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<Object>(Result.genSuccessResult(resultMap), HttpStatus.OK);
    }

    /*
    * 根据评论id获取评论内容
    * */
    @GetMapping("/user/comment/{commentId}")
    public ResponseEntity<?> getCommentById(@PathVariable Integer commentId) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            List<Map<String, Object>> resultList = commentService.getCommentById(commentId);
            if (resultList != null && resultList.size() != 0) {
                resultMap = resultList.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(resultMap), HttpStatus.OK);
    }

    /*
    * 设置用户状态(正常/小黑屋)
    * */
    @PutMapping("/user")
    public ResponseEntity<?> setUserStatue(@RequestBody Map<String, Object> params) {
        try {
            userService.setUserStatue((Integer) params.get("userId"), (Integer) params.get("statue"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }


    /*
    * 获取全部文章
    * */
    @GetMapping("/articles/{statue}")
    public ResponseEntity<?> getAllArticle(@PathVariable("statue") Integer statue,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size) {
        List<Map<String, Object>> resultList = null;
        try {
            resultList = articleService.getAllArticle(statue, page, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", resultList);
        result.put("count", articleService.getArticleCountByStatue(statue));
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 修改文章状态
    * */
    @PutMapping("/articles")
    public ResponseEntity<?> updateArticle(@RequestBody Map<String, Object> params) {
        try {
            articleService.updateArticle((Integer) params.get("id"), (Integer) params.get("statue"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

    /*
    * 修改文章内容
    * */
    @PutMapping("/article")
    public ResponseEntity<?> updateArticleContent(@RequestBody Map<String, Object> params) {
        try {
            articleService.updateArticleContent(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

    /*
    * 根据id获取某个用户的信息
    * */
    @GetMapping("/user")
    public ResponseEntity<?> getUserById(Integer userId) {
        Map<String, Object> map = null;
        try {
            List<Map<String, Object>> resultList = userService.getUserById(userId);
            if (resultList != null && resultList.size() != 0) {
                map = userService.getUserById(userId).get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(map), HttpStatus.OK);
    }
}
