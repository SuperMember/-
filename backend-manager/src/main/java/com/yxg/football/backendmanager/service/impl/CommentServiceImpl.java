package com.yxg.football.backendmanager.service.impl;

import com.yxg.football.backendmanager.dao.CommentDao;
import com.yxg.football.backendmanager.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentDao commentDao;

    @Override
    public List<Map<String, Object>> getAllComment(Integer statue, Integer type, Integer page, Integer size) {
        return commentDao.getAllComment(statue, type, page, size);
    }

    /*
    * 删除评论(设置评论内容为固定文字)
    * */
    @Override
    public Integer deleteComment(Integer id, Integer userId) {
        return commentDao.deleteComment(id, userId);
    }

    @Override
    public Integer getCommentCount() {
        return commentDao.getCommentCount();
    }

    @Override
    public List<Map<String, Object>> getReplyById(Integer replyId, Integer page, Integer size) {
        return commentDao.getReplyById(replyId, page, size);
    }

    @Override
    public Integer getReplyCount(Integer replyId) {
        return commentDao.getReplyCount(replyId);
    }

    @Override
    public Integer getCommentByUserIdCount(Integer userId) {
        return commentDao.getCommentByUserIdCount(userId);
    }

    @Override
    public Integer replyComment(Integer userId, Map<String, Object> params) {
        return commentDao.replyComment(userId, params);
    }

    @Override
    public List<Map<String, Object>> getReplyByUserId(Integer userId, Integer page, Integer size) {
        return commentDao.getReplyByUserId(userId, page, size);
    }

    @Override
    public Integer getReplyByUserIdCount(Integer userId) {
        return commentDao.getReplyByUserIdCount(userId);
    }

    @Override
    public List<Map<String, Object>> getCommentById(Integer commentId) {
        return commentDao.getCommentById(commentId);
    }

    @Override
    public Integer deleteReply(Integer replyId, Integer userId) {
        return commentDao.deleteReply(replyId, userId);
    }


}
