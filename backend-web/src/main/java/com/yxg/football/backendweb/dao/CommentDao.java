package com.yxg.football.backendweb.dao;

import java.util.List;
import java.util.Map;

public interface CommentDao {
    List<Map<String, Object>> getComments(Integer type, Integer id, Integer page, Integer size, String order);

    Integer insertComment(Map<String, Object> params);

    /*
    * 回复用户
    * */
    Integer replyComment(Map<String, Object> params);


    /*
    * 获取回复的评论(前几条)
    * */
    List<Map<String, Object>> getReplyComment(Integer commentId, Integer page, Integer size);

    /*
    * 删除评论
    * */
    Integer deleteComment(Integer id);

    /*
    * 点赞
    * */
    Integer likeComment(Integer commentId);


    Integer likeReply(Integer commentId);

    /*
    * 获取回复的所有评论
    * */
    List<Map<String, Object>> getAllReply(Integer commentId, Integer page, Integer size, String order);

    /*
    * 获取某条评论
    * */
    Map<String, Object> getCommentById(Integer id);

    /*
    * 获取某个用户的所有评论
    * */
    List<Map<String, Object>> getCommentsByUserId(Integer userId, Integer page, Integer size);

}


