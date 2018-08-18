package com.yxg.football.backendweb.dao;

import java.util.List;
import java.util.Map;

public interface RedisDao {
    //点赞操作
    Boolean likeComment(Integer userId, Integer commentId);

    Boolean likeReply(Integer userId, Integer replyId);

    //获取回复数量
    Integer getReplyCount(Integer commentId);

    //设置某条评论的回复数量
    void setReplyCount(Integer commentId);

    //设置某条文章的评论数量
    void setArticleCommentCount(Integer articleId, Integer type);

    //获取某条文章的评论数量
    Integer getArticleCommentCount(Integer articleId, Integer type);

    //关注操作
    Boolean focusCircle(Integer userId, Integer circleId);

    /*
    * 判断用户是否关注过圈子
    * */
    Boolean isFocusCircle(Integer userId, Integer circleId);

    //判断用户是否关注过用户
    Boolean isFocusUser(Integer userId, Integer beuserId);

    //关注用户
    Boolean focusUser(Integer userId, Integer beuserId);

    //设置视频播放数
    void setVideoCount(Integer articleId);

    //获取视频播放数
    Integer getVideoCount(Integer articleId);

    //设置聊天数据
    void setChat(Map<String, Object> params, String id);

    //返回聊天数据
    List<Map<String, Object>> getChat(String id);

    //设置点赞
    void setZan(String gameId, String side);

    //获取点赞情况
    Map<String, Object> getZan(String gameId);

    /*
    * 用户活跃度排行榜
    * */
    void setRankByUserId(Integer userId, Integer point, Integer circleId);

    /*
    * 获取排行榜
    * */
    List<Map<String, Object>> getRank(Integer circleId, Integer page, Integer size);

    /*
    * 清除排行榜
    * */
    void clearRank();
}
