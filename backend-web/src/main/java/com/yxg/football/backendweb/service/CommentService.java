package com.yxg.football.backendweb.service;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.List;
import java.util.Map;

public interface CommentService {
    Map<String, Object> getComments(Integer type, Integer id, Integer page, Integer size, String order);

    Integer insertComment(Map<String, Object> params) throws Exception;


    /*
    * 获取回复数据
    * */
    List<Map<String, Object>> getReply(Integer commentId, Integer page, Integer size);

    /*
    * 回复
    * */
    Boolean replyComment(Map<String, Object> params);

    /*
    * 删除评论
    * */
    Integer deleteComment(Integer id);

    /*
    * 点赞
    * */
    Boolean likeComment(Integer userId, Integer commentId);


    Boolean likeReply(Integer userId, Integer replyId);

    /*
    * 获取所有评论
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
