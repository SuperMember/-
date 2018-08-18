package com.yxg.football.backendweb.controller;

import com.yxg.football.backendweb.entity.Result;
import com.yxg.football.backendweb.entity.User;
import com.yxg.football.backendweb.service.CircleService;
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
@RequestMapping("/football/api/circle")
public class CircleController {

    @Autowired
    CircleService circleService;


    /*
    * 关注圈子
    * */
    @PostMapping("/focus")
    public ResponseEntity<?> focusCircle(@RequestBody Map<String, Object> params) throws Exception {
        if (circleService.focusCircle(params)) {
            return new ResponseEntity<Object>(Result.genSuccessResult("关注成功"), HttpStatus.OK);
        }
        return new ResponseEntity<Object>(Result.genFailResult("取消成功"), HttpStatus.OK);

    }


    /*
    * 获取用户关注的圈子
    * */
    @GetMapping("/focus")
    public ResponseEntity<?> getFocusCircle() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Map<String, Object>> result = circleService.getCircleFocusByUserId(user.getId());
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 判断用户是否关注过某个圈子
    * */
    @GetMapping("/focus/exist/{circleId}")
    public ResponseEntity<?> isFocusCircle(@PathVariable String circleId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (circleService.isFocusCircle(user.getId(), Integer.parseInt(circleId))) {
            return new ResponseEntity<Object>(Result.genSuccessResult("已经关注"), HttpStatus.OK);
        }
        return new ResponseEntity<Object>(Result.genFailResult("未关注"), HttpStatus.OK);
    }

    /*
    * 根据Id获取某个圈子的所有帖子(type为0则是按时间获取1按最新回复获取)
    * */
    @GetMapping("/arlist")
    public ResponseEntity<?> getArticleList(
            Integer cId,
            @RequestParam(defaultValue = "0") Integer type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10", required = false) Integer size) throws Exception {
        List<Map<String, Object>> resultList = null;
        resultList = circleService.getArticles(type, cId, page, size);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", resultList);
        //置顶帖
        resultMap.put("top", circleService.getTopList(cId));
        //排行榜
        resultMap.put("rank", circleService.getRank(cId, 0, 10));
        return new ResponseEntity<Object>(Result.genSuccessResult(resultMap), HttpStatus.OK);
    }

    /*
    * 根据时间获取最新的帖子
    * */
    @GetMapping("/lastest")
    public ResponseEntity<?> getArticleByTime(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) throws Exception {
        List<Map<String, Object>> resutlList = null;
        resutlList = circleService.getAsByTime(page, size);
        return new ResponseEntity<Object>(Result.genSuccessResult(resutlList), HttpStatus.OK);
    }

    /*
    * 提交帖子
    * */
    @PostMapping("/arsubmit")
    public ResponseEntity<?> insertArticle(@RequestBody Map<String, Object> params) throws Exception {
        Integer result = null;
        result = circleService.insertArticle(params);
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }


    /*
    * 申请创建圈子
    * */
    @PostMapping("/ccsubmit")
    public ResponseEntity<?> createCircle(@RequestBody Map<String, Object> params) throws Exception {
        if (circleService.createdCircle(params)) {
            return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
        }
        return new ResponseEntity<Object>(Result.genFailResult("圈子已存在"), HttpStatus.OK);
    }

    /*
    * 删除帖子
    * */
    @DeleteMapping("/article")
    public ResponseEntity<?> deleteArticle(Integer aId) throws Exception {
        Integer result = null;
        result = circleService.deleteArticle(aId);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    /*
    * 获取圈子集合
    * */
    @GetMapping("/cclist")
    public ResponseEntity<?> getCircleList(
            Integer statue,
            Integer type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10", required = false) Integer size) throws Exception {
        List<Map<String, Object>> result = null;
        result = circleService.getAllCircle(statue, type);
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 点赞增加圈子
    * */
    @PostMapping("/creation")
    public ResponseEntity<?> supportCircle(@RequestBody Map<String, Object> params) throws Exception {
        if (circleService.supportCircle((String) params.get("title"))) {
            return new ResponseEntity<Object>(Result.genSuccessResult("创建成功"), HttpStatus.OK);
        }
        return new ResponseEntity<Object>(Result.genSuccessResult("支持成功"), HttpStatus.OK);
    }

    /*
    * 根据id获取文章
    * */
    @GetMapping("/article")
    public ResponseEntity<?> getArticleById(Integer id) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result = circleService.getArticleById(id);
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 根据用户id获取文章
    * */
    @GetMapping("/article/list")
    public ResponseEntity<?> getArticleByUserId() throws Exception {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Map<String, Object>> result = circleService.getArticleByUserId(user.getId());
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 获取其他用户的文章
    * */
    @GetMapping("/article/one/list")
    public ResponseEntity<?> getOneArticleByUserId(Integer userId) throws Exception {
        List<Map<String, Object>> result = circleService.getArticleByUserId(userId);
        return new ResponseEntity<Object>(Result.genSuccessResult(result), HttpStatus.OK);
    }

    /*
    * 设置视频播放数
    * */
    @PostMapping("/video")
    public ResponseEntity<?> setVideoCount(@RequestBody Map<String, Object> params) throws Exception {
        circleService.setVideoCount(Integer.parseInt((String) params.get("articleId")));
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }


    /*
    * 获取排行榜
    * */
    @GetMapping("/rank")
    public ResponseEntity<?> getRank(@RequestParam(defaultValue = "0", required = false) Integer page,
                                     @RequestParam(defaultValue = "10", required = false) Integer size,
                                     Integer cId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("list", circleService.getRank(cId, page, size));
        map.put("owner", circleService.getCircleOwner(cId));
        return new ResponseEntity<Object>(Result.genSuccessResult(map), HttpStatus.OK);
    }
}
