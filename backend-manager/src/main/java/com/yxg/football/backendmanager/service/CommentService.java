package com.yxg.football.backendmanager.service;


import java.util.List;
import java.util.Map;

public interface CommentService {
    List<Map<String, Object>> getAllComment(Integer statue, Integer type, Integer page, Integer size);

    Integer deleteComment(Integer id, Integer userId);

    Integer getCommentCount();

    List<Map<String, Object>> getReplyById(Integer replyId, Integer page, Integer size);

    Integer getReplyCount(Integer replyId);

    Integer getCommentByUserIdCount(Integer userId);

    Integer replyComment(Integer userId, Map<String, Object> params);

    List<Map<String, Object>> getReplyByUserId(Integer userId, Integer page, Integer size);

    Integer getReplyByUserIdCount(Integer userId);

    List<Map<String, Object>> getCommentById(Integer commentId);

    Integer deleteReply(Integer replyId, Integer userId);

}
