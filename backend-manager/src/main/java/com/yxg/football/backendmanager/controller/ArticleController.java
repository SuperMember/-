package com.yxg.football.backendmanager.controller;

import com.yxg.football.backendmanager.entity.Result;
import com.yxg.football.backendmanager.entity.ResultCode;
import com.yxg.football.backendmanager.entity.User;
import com.yxg.football.backendmanager.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import sun.plugin.liveconnect.SecurityContextHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manager/article")
public class ArticleController {
    @Autowired
    ArticleService articleService;

    @GetMapping("/list")
    public ResponseEntity<?> getArticles(@RequestParam(defaultValue = "10") Integer size,
                                         @RequestParam(defaultValue = "1") Integer page, Integer statue, Integer type) {
        List<Map<String, Object>> resultList = null;
        Map<String, Object> resultMap = new HashMap<>();
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            resultList = articleService.getArticle(size, page, user.getId(), statue, type);
            resultMap.put("data", resultList);
            resultMap.put("count", articleService.getCount(user.getId(), statue, type));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<Object>(Result.genSuccessResult(resultMap), HttpStatus.OK);
    }

    @PostMapping("/list")
    public ResponseEntity<?> addArticle(@RequestBody Map<String, Object> params) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            articleService.saveArticle(user.getId(), params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

    @DeleteMapping("/list")
    public ResponseEntity<?> deleteArticle(@RequestBody Map<String, Object> params) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            articleService.deleteArticle(user.getId(), (String) params.get("articleId"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

    @GetMapping("/list/{articleId}")
    public ResponseEntity<?> getArticleById(@PathVariable("articleId") Integer articleId) {
        Map<String, Object> resultMap = null;
        try {
            resultMap = articleService.getArticleById(articleId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(resultMap), HttpStatus.OK);
    }
}
