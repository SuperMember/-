package com.yxg.football.backendweb.service.impl;

import com.yxg.football.backendweb.dao.CircleDao;
import com.yxg.football.backendweb.dao.CommentDao;
import com.yxg.football.backendweb.dao.RedisDao;
import com.yxg.football.backendweb.entity.User;
import com.yxg.football.backendweb.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
* 有关排行榜积分说明
* 发帖子积2分，留言积1分
* 每日更新
* 12点重新计算
* */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentDao commentDao;
    @Autowired
    RedisDao redisDao;
    @Autowired
    CircleDao circleDao;

    /*
    * 首层评论数据
    * */
    @Override
    public Map<String, Object> getComments(Integer type, Integer id, Integer page, Integer size, String order) {
        List<Map<String, Object>> resultList = commentDao.getComments(type, id, page, size, order);
        //包装数据
        for (int i = 0; i < resultList.size(); i++) {
            Map<String, Object> map = resultList.get(i);
            //redis获取回复评论数
            Integer replyNum = redisDao.getReplyCount((Integer) map.get("ID"));
            if (replyNum != 0) {
                //获取1~3条回复的评论
                List<Map<String, Object>> replyList = commentDao.getReplyComment((Integer) map.get("ID"), 1, 3);
                map.put("reply", replyList);
                //总回复数量
                map.put("REPLYNUM", replyNum);
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", resultList);
        resultMap.put("count", redisDao.getArticleCommentCount(id, type));
        return resultMap;
    }

    @Transactional
    @Override
    public Integer insertComment(Map<String, Object> params) throws Exception {
        //设置评论数量
        redisDao.setArticleCommentCount(Integer.parseInt((String) params.get("belongId")), Integer.parseInt((String) params.get("type")));
        //保存评论
        commentDao.insertComment(params);
        //修改该用户活跃度
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        redisDao.setRankByUserId(user.getId(), 1, (Integer) params.get("cId"));
        //修改帖子的时间
        return circleDao.updateTime(Integer.parseInt((String) params.get("belongId")));
    }


    /*
    * 获取回复数据
    * */
    @Override
    public List<Map<String, Object>> getReply(Integer commentId, Integer page, Integer size) {
        return commentDao.getReplyComment(commentId, page, size);
    }

    /*
    * 回复用户
    * */
    @Transactional
    @Override
    public Boolean replyComment(Map<String, Object> params) {
        try {
            //添加数据库
            commentDao.replyComment(params);
            //添加redis
            redisDao.setReplyCount((Integer) params.get("commentId"));
            //修改帖子的时间
            circleDao.updateReplyTime((Integer) params.get("commentId"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Integer deleteComment(Integer id) {
        return commentDao.deleteComment(id);
    }

    /*
    * 点赞
    * */
    @Override
    public Boolean likeComment(Integer userId, Integer commentId) {
        //检测该用户是否进行点赞
        if (!redisDao.likeComment(userId, commentId)) {
            return false;
        }
        //保存进数据库
        commentDao.likeComment(commentId);
        return true;
    }

    @Override
    public Boolean likeReply(Integer userId, Integer replyId) {
        //检测该用户是否进行点赞
        if (!redisDao.likeReply(userId, replyId)) {
            return false;
        }
        //保存进数据库
        commentDao.likeReply(replyId);
        return true;
    }

    @Override
    public List<Map<String, Object>> getAllReply(Integer commentId, Integer page, Integer size, String order) {
        return commentDao.getAllReply(commentId, page, size, order);
    }

    @Override
    public Map<String, Object> getCommentById(Integer id) {
        return commentDao.getCommentById(id);
    }

    @Override
    public List<Map<String, Object>> getCommentsByUserId(Integer userId, Integer page, Integer size) {
        return commentDao.getCommentsByUserId(userId, page, size);
    }
}
