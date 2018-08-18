package com.yxg.football.backendweb.dao.impl;

import com.yxg.football.backendweb.dao.BaseDao;
import com.yxg.football.backendweb.dao.RedisDao;
import com.yxg.football.backendweb.util.Const;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
public class RedisDaoImpl extends BaseDao implements RedisDao {
    @Override
    public Boolean likeComment(Integer userId, Integer commentId) {
        //对评论进行点赞操作
        //保存用户，避免重复点赞
        //user:userId
        //进行点赞操作
        //有序集合
        if (this.getRedisTemplater().opsForSet().add("likeComment:" + commentId, "user:" + userId) == 0) {
            //永久
            this.getRedisTemplater().persist("likeComment:" + commentId);
            //该用户已经点过赞
            return false;
        }
//        if (!this.getRedisTemplater().opsForZSet().add("LIKE", "comment:" + commentId, 1)) {
//            //返回false则表示已经存在,在进行自增操作
//            this.getRedisTemplater().opsForZSet().incrementScore("LIKE", "comment:" + commentId, 1);
//        }
        return true;
    }

    @Override
    public Boolean likeReply(Integer userId, Integer replyId) {
        if (this.getRedisTemplater().opsForSet().add("likeReply:" + replyId, "user:" + userId) == 0) {
            //该用户已经点过赞
            return false;
        }
        return true;
    }


    /*
    * 获取某条评论的回复数量
    * */
    @Override
    public Integer getReplyCount(Integer commentId) {
        HashOperations hashOperations = this.getRedisTemplater().opsForHash();
        String count = (String) hashOperations.get(Const.HASH_REPLY_NUM, "REPLY_NUM:" + commentId);
        if (count != null) {
            return Integer.parseInt(count);
        }
        return 0;
    }


    /*
    * 设置某条评论的回复数量
    * */
    @Override
    public void setReplyCount(Integer commentId) {
        HashOperations hashOperations = this.getRedisTemplater().opsForHash();
        if (hashOperations.hasKey(Const.HASH_REPLY_NUM, "REPLY_NUM:" + commentId)) {
            //有值，进行自增
            hashOperations.increment(Const.HASH_REPLY_NUM, "REPLY_NUM:" + commentId, 1);
        } else {
            //永久
            this.getRedisTemplater().persist(Const.HASH_REPLY_NUM);
            //之前没有数值，则进行添加操作
            hashOperations.put(Const.HASH_REPLY_NUM, "REPLY_NUM:" + commentId, "1");
        }
    }

    @Override
    public void setArticleCommentCount(Integer articleId, Integer type) {
        HashOperations hashOperations = this.getRedisTemplater().opsForHash();
        if (hashOperations.hasKey(Const.HASH_ARTICLE_NUM, "ARTICLE_COMMENT:" + articleId + ":" + type)) {
            //有值，进行自增
            hashOperations.increment(Const.HASH_ARTICLE_NUM, "ARTICLE_COMMENT:" + articleId + ":" + type, 1);
        } else {
            //永久
            this.getRedisTemplater().persist(Const.HASH_ARTICLE_NUM);
            //之前没有数值，则进行添加操作
            hashOperations.put(Const.HASH_ARTICLE_NUM, "ARTICLE_COMMENT:" + articleId + ":" + type, "1");
        }
    }

    @Override
    public Integer getArticleCommentCount(Integer articleId, Integer type) {
        HashOperations hashOperations = this.getRedisTemplater().opsForHash();
        String count = (String) hashOperations.get(Const.HASH_ARTICLE_NUM, "ARTICLE_COMMENT:" + articleId + ":" + type);
        if (count != null) {
            return Integer.parseInt(count);
        }
        return 0;
    }

    /*
    * 关注圈子,返回false表示已经关注
    * */
    @Transactional
    @Override
    public Boolean focusCircle(Integer userId, Integer circleId) {
        ValueOperations<String, List<String>> valueOperations = this.getTemplate().opsForValue();
        String key = Const.FOCUS_CIRCLE + ":" + userId;
        if (!this.getTemplate().hasKey(key)) {
            //永久
            this.getTemplate().persist(key);
            valueOperations.set(key, new ArrayList<String>());
        }
        //获取内容
        List<String> list = valueOperations.get(Const.FOCUS_CIRCLE + ":" + userId);
        for (int i = 0; i < list.size(); i++) {
            //判断是否存在
            if (list.get(i).equals(circleId + "")) {
                //删除
                list.remove(i);
                valueOperations.set(Const.FOCUS_CIRCLE + ":" + userId, list);
                return false;
            }
        }
        list.add(circleId + "");
        valueOperations.set(Const.FOCUS_CIRCLE + ":" + userId, list);
        return true;
    }

    @Override
    public Boolean isFocusCircle(Integer userId, Integer circleId) {
        String key = Const.FOCUS_CIRCLE + ":" + userId;
        if (this.getTemplate().hasKey(key)) {
            //判断是否关注过该圈子
            ValueOperations<String, List<String>> valueOperations = this.getTemplate().opsForValue();
            List<String> list = valueOperations.get(Const.FOCUS_CIRCLE + ":" + userId);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(circleId + "")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Boolean isFocusUser(Integer userId, Integer beuserId) {
        String key = Const.FOCUS_USER + ":" + userId;
        if (this.getTemplate().hasKey(key)) {
            //判断是否关注过该圈子
            ValueOperations<String, List<String>> valueOperations = this.getTemplate().opsForValue();
            List<String> list = valueOperations.get(Const.FOCUS_USER + ":" + userId);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(beuserId + "")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Boolean focusUser(Integer userId, Integer beuserId) {
        ValueOperations<String, List<String>> valueOperations = this.getTemplate().opsForValue();
        String key = Const.FOCUS_USER + ":" + userId;
        if (!this.getTemplate().hasKey(key)) {
            //永久
            this.getTemplate().persist(key);
            valueOperations.set(key, new ArrayList<String>());
        }
        //获取内容
        List<String> list = valueOperations.get(Const.FOCUS_USER + ":" + userId);
        for (int i = 0; i < list.size(); i++) {
            //判断是否存在
            if (list.get(i).equals(beuserId + "")) {
                //删除
                list.remove(i);
                valueOperations.set(Const.FOCUS_USER + ":" + userId, list);
                return false;
            }
        }
        list.add(beuserId + "");
        valueOperations.set(Const.FOCUS_USER + ":" + userId, list);
        return true;
    }

    //设置视频播放数
    @Override
    public void setVideoCount(Integer articleId) {
        HashOperations hashOperations = this.getRedisTemplater().opsForHash();
        if (hashOperations.hasKey(Const.HASH_VIDEO, "VIDEO_COUNT:" + articleId)) {
            //有值，进行自增
            hashOperations.increment(Const.HASH_VIDEO, "VIDEO_COUNT:" + articleId, 1);
        } else {
            //永久
            this.getRedisTemplater().persist(Const.HASH_VIDEO);
            //之前没有数值，则进行添加操作
            hashOperations.put(Const.HASH_VIDEO, "VIDEO_COUNT:" + articleId, "1");
        }
    }

    //获取视频播放数
    @Override
    public Integer getVideoCount(Integer articleId) {
        HashOperations hashOperations = this.getRedisTemplater().opsForHash();
        String count = (String) hashOperations.get(Const.HASH_VIDEO, "VIDEO_COUNT:" + articleId);
        if (count != null) {
            return Integer.parseInt(count);
        }
        return 0;
    }

    //设置聊天数据
    //将聊天数据放入redis中
    @Override
    public void setChat(Map<String, Object> params, String id) {
        ValueOperations<String, List<Map<String, Object>>> valueOperations = this.getTemplate().opsForValue();
        String key = Const.LIST_HASH + ":" + id;
        if (!this.getTemplate().hasKey(key)) {
            //永久
            this.getTemplate().persist(key);
            valueOperations.set(key, new ArrayList<Map<String, Object>>());
        }
        //获取数据
        List<Map<String, Object>> list = valueOperations.get(key);
        list.add(params);
        valueOperations.set(key, list);
    }

    //获取聊天数据
    @Override
    public List<Map<String, Object>> getChat(String id) {
        ValueOperations<String, List<Map<String, Object>>> valueOperations = this.getTemplate().opsForValue();
        String key = Const.LIST_HASH + ":" + id;
        if (this.getTemplate().hasKey(key)) {
            return valueOperations.get(key);
        }
        return null;
    }

    @Override
    public void setZan(String gameId, String side) {
        HashOperations hashOperations = this.getRedisTemplater().opsForHash();
        if (hashOperations.hasKey(Const.HASH_GAME_ZAN, "GAME:" + side + ":" + gameId)) {
            //有值，进行自增
            hashOperations.increment(Const.HASH_GAME_ZAN, "GAME:" + side + ":" + gameId, 1);
        } else {
            //永久
            this.getRedisTemplater().persist(Const.HASH_GAME_ZAN);
            //之前没有数值，则进行添加操作
            hashOperations.put(Const.HASH_GAME_ZAN, "GAME:left:" + gameId, "0");
            hashOperations.put(Const.HASH_GAME_ZAN, "GAME:right:" + gameId, "0");
            hashOperations.increment(Const.HASH_GAME_ZAN, "GAME:" + side + ":" + gameId, 1);
        }

    }

    @Override
    public Map<String, Object> getZan(String gameId) {
        HashOperations hashOperations = this.getRedisTemplater().opsForHash();
        Map<String, Object> result = new HashMap<>();
        if (hashOperations.hasKey(Const.HASH_GAME_ZAN, "GAME:left:" + gameId)) {
            result.put("left", hashOperations.get(Const.HASH_GAME_ZAN, "GAME:left:" + gameId));
            result.put("right", hashOperations.get(Const.HASH_GAME_ZAN, "GAME:right:" + gameId));
            return result;
        }
        result.put("left", "0");
        result.put("right", "0");
        return result;
    }


    /*
    * 设置排行榜
    * */
    @Override
    public void setRankByUserId(Integer userId, Integer point, Integer circleId) {
        //有序
        ZSetOperations zSetOperations = this.getTemplate().opsForZSet();
        if (!zSetOperations.add(Const.ZSET_RANK + ":" + circleId, userId + "",
                                Double.parseDouble(point + ""))) {
            //添加不成功则表示已存在
            //进行增加
            zSetOperations.incrementScore(Const.ZSET_RANK + ":" + circleId, userId + "",
                                          Double.parseDouble(point + ""));
        }
    }


    /*
    * 获取排行榜
    * */
    @Override
    public List<Map<String, Object>> getRank(Integer circleId, Integer page, Integer size) {
        ZSetOperations zSetOperations = this.getTemplate().opsForZSet();
        //每次获取10个
        Set<ZSetOperations.TypedTuple> set = zSetOperations.reverseRangeWithScores(Const.ZSET_RANK + ":" + circleId, page * size, page * size + size);
        Iterator iterator = set.iterator();
        List<Map<String, Object>> list = new ArrayList<>();
        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple typedTuple = (ZSetOperations.TypedTuple) iterator.next();
            String value = (String) typedTuple.getValue();
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("ID", value);
            resultMap.put("ACTIVE", typedTuple.getScore());
            list.add(resultMap);
        }
        return list;
    }


    /*
    * 清除排行榜
    * */
    @Override
    public void clearRank() {
        Set<String> set = this.getTemplate().keys(Const.ZSET_RANK + ":*");
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            String str = (String) iterator.next();
            this.getTemplate().opsForZSet().removeRange(str, 0, -1);
        }
    }
}
