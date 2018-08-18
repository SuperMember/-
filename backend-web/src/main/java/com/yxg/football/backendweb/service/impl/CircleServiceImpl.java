package com.yxg.football.backendweb.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yxg.football.backendweb.dao.CircleDao;
import com.yxg.football.backendweb.dao.CircleSearchDao;
import com.yxg.football.backendweb.dao.RedisDao;
import com.yxg.football.backendweb.dao.UserDao;
import com.yxg.football.backendweb.entity.Circle;
import com.yxg.football.backendweb.entity.User;
import com.yxg.football.backendweb.enums.CircleEnum;
import com.yxg.football.backendweb.exceptions.CircleException;
import com.yxg.football.backendweb.service.CircleService;
import com.yxg.football.backendweb.util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;


/*
* 有关排行榜积分说明
* 发帖子积2分，留言积1分
* 每日更新
* 12点重新计算
* */
@Service
public class CircleServiceImpl implements CircleService {
    @Autowired
    UserDao userDao;
    @Autowired
    CircleDao circleDao;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisDao redisDao;
    @Autowired
    CircleSearchDao circleSearchDao;
    @Autowired
    Gson gson;
    @Value("${redis.circle.timeOut}")
    Integer timeout;
    @Value("${redis.circle.count}")
    Integer countPerson;//点赞总人数

    @Override
    public List<Map<String, Object>> getArticles(Integer type, Integer cid, Integer page, Integer size) {
        List<Map<String, Object>> resultList = null;
        if (type == 0) {
            resultList = circleDao.getArticles(cid, page, size);
        } else {
            //按最新回复获取数据
            resultList = circleDao.getArticlesByTime(cid, page, size);
        }
        for (int i = 0; i < resultList.size(); i++) {
            Map<String, Object> resultMap = resultList.get(i);
            //redis获取评论数量
            Integer count = redisDao.getArticleCommentCount((Integer) resultMap.get("ID"), 0);
            resultMap.put("COUNT", count);
        }
        return resultList;
    }

    @Override
    public List<Map<String, Object>> getAsByTime(Integer page, Integer size) {
        return circleDao.getAsByTime(page, size);
    }

    /*
    * 用户权限控制
    * */
    @Override
    public Integer insertArticle(Map<String, Object> params) {
        //设置活跃度
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        redisDao.setRankByUserId(user.getId(), 2, Integer.parseInt((String) params.get("cId")));
        return circleDao.insertArticle(params);
    }


    /*
    * 圈子创建规则:提交申请，等待用户点击，超过一定用户数量，则申请成功，否则失败(用户权限控制)
    * */
    @Transactional
    @Override
    public Boolean createdCircle(Map<String, Object> params) throws Exception {
        if (params == null) {
            throw new CircleException(CircleEnum.PARAM_NULL.getMsg());
        }
        String title = (String) params.get("title");
        //根据圈子名称判断是否有圈子创建过或者正在创建
        //检测是否有圈子正在创建
        if (stringRedisTemplate.hasKey(title + ":INFO")) {
            return false;
        }
        //检测是否有圈子已经被创建
        if (circleDao.isExistCircle(title)) {
            return false;
        }
        //保存信息到redis
        params.put("created", new Date().toString());
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        params.put("userId", user.getId());
        String info = gson.toJson(params);
        //设置过期时间
        stringRedisTemplate.opsForValue().set(title + ":INFO", info, timeout, TimeUnit.DAYS);
        //初始化点赞人数
        stringRedisTemplate.opsForValue().set(title + ":COUNT", "0", timeout, TimeUnit.DAYS);
        return true;
    }

    @Override
    public Integer deleteArticle(Integer aId) {
        return circleDao.deleteArticle(aId);
    }


    /*
    * 根据状态获取不同的圈子集合
    * */
    @Override
    public List<Map<String, Object>> getAllCircle(Integer statue, Integer type) {
        if (statue == 1) {
            return circleDao.getCircles(statue, type);
        }
        List<Map<String, Object>> resultList = new ArrayList<>();
        //在redis中获取信息

        //循环获取所有的key
        Set<String> set = stringRedisTemplate.keys("*:INFO");
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            //循环遍历所有的key
            String key = iterator.next();
            String json = (String) stringRedisTemplate.opsForValue().get(key);
            Map<String, Object> infos = gson.fromJson(json, new TypeToken<Map<String, Object>>() {
            }.getType());
            String count = key.substring(0, key.indexOf(":")) + ":COUNT";
            infos.put("count", stringRedisTemplate.opsForValue().get(count));
            //获取过期时间
            infos.put("expire", stringRedisTemplate.getExpire(key));
            resultList.add(infos);
        }
        return resultList;
    }


    /*
     * 支持创建圈子
     * */
    //TODO
    //分布式
    @Override
    public Boolean supportCircle(String title) {
        ValueOperations valueOperations = stringRedisTemplate.opsForValue();
        //修改圈子的实时点赞人数
        valueOperations.increment(title + ":COUNT", 1);
        //检测是否到达指定人数，若到达指定人数，将redis存储的数据存进数据库，否则继续点赞操作
        Integer integer = Integer.parseInt((String) valueOperations.get(title + ":COUNT"));
        if (integer >= countPerson) {
            //达到总人数
            //修改圈子状态
            //String result = (String) valueOperations.get(title + ":INFO");
            String result = (String) stringRedisTemplate.opsForValue().get(title + ":INFO");
            Map<String, Object> params = gson.fromJson(result, new TypeToken<Map<String, Object>>() {
            }.getType());
            //存进数据库中
            Integer id = circleDao.createCircle(params);
            //加入到搜索中
            Circle circle = new Circle();
            circle.setID(id);
            circle.setTitle((String) params.get("title"));
            circle.setIMG((String) params.get("img"));
            circle.setBACKGROUND((String) params.get("background"));
            Double t = (Double) params.get("type");
            Integer type = Integer.valueOf(t.intValue());
            circle.setTYPE(type);
            Double d = (Double) params.get("userId");
            Integer userId = Integer.valueOf(d.intValue());
            circle.setUSERID(userId);
            circle.setSTATUE(1);
            circle.setCREATED(new Date());
            circle.setCOUNT(0);
            circle.setINTRODUCTION((String) params.get("introduction"));
            circleSearchDao.save(circle);
            //删除redis数据
            stringRedisTemplate.delete(title + ":INFO");
            stringRedisTemplate.delete(title + ":COUNT");
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Object> getArticleById(Integer id) {
        return circleDao.getArticleById(id);
    }

    @Override
    public List<Map<String, Object>> getArticleByUserId(Integer userId) {
        return circleDao.getArticleByUserId(userId);
    }

    @Override
    public Boolean focusCircle(Map<String, Object> params) {
        //判断是否关注过
        //redis
        Integer circleId = (Integer) params.get("circleId");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (redisDao.focusCircle(user.getId(), circleId)) {
            //未关注
            circleDao.focusCircle(user.getId(), circleId);
            return true;
        }
        //取消关注
        circleDao.unFocusCircle(user.getId(), circleId);
        return false;
    }

    @Override
    public Boolean isFocusCircle(Integer userId, Integer circleId) {
        return redisDao.isFocusCircle(userId, circleId);
    }

    @Override
    public List<Map<String, Object>> getCircleFocusByUserId(Integer userId) {
        return circleDao.getCircleFocusByUserId(userId);
    }

    @Override
    public void setVideoCount(Integer articleId) {
        redisDao.setVideoCount(articleId);
    }

    @Override
    public Integer getVideoCount(Integer articleId) {
        return redisDao.getVideoCount(articleId);
    }

    @Override
    public List<Map<String, Object>> getTopList(Integer circleId) {
        return circleDao.getTopList(circleId);
    }

    @Override
    public void setRankByUserId(Integer userId, Integer point, Integer circleId) {
        redisDao.setRankByUserId(userId, point, circleId);
    }

    @Override
    public List<Map<String, Object>> getRank(Integer circleId, Integer page, Integer size) {
        List<Map<String, Object>> list = redisDao.getRank(circleId, page, size);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            List<Map<String, Object>> userList = userDao.getUserInfo(Integer.parseInt((String) map.get("ID")));
            map.put("IMG", userList.get(0).get("IMG"));
            map.put("USERNAME", userList.get(0).get("USERNAME"));
            map.put("STATUE", userList.get(0).get("STATUE"));
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getCircleOwner(Integer cId) {
        return circleDao.getCircleOwner(cId);
    }
}
