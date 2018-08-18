package com.yxg.football.backendweb.dao.impl;


import com.yxg.football.backendweb.dao.BaseDao;
import com.yxg.football.backendweb.dao.CommentDao;
import com.yxg.football.backendweb.dao.RedisDao;
import com.yxg.football.backendweb.entity.User;
import com.yxg.football.backendweb.enums.CircleEnum;
import com.yxg.football.backendweb.exceptions.CircleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class CommentDaoImpl extends BaseDao implements CommentDao {
    @Autowired
    RedisDao redisDao;

    @Override
    public List<Map<String, Object>> getComments(Integer type, Integer id, Integer page, Integer size, String order) {
        String sql = "select wc.*,u.USERNAME,u.IMG,u.STATUE from wechat_comment wc ,all_user u where wc.type=? and wc.BELONG_ID =? and wc.USER_ID=u.ID ORDER BY CREATED " + order + " limit ?,?";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{type, id, (page - 1) * size, size});
    }

    @Transactional
    @Override
    public Integer insertComment(Map<String, Object> params) {
        if (params == null) {
            throw new CircleException(CircleEnum.PARAM_NULL.getMsg());
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String sql = "insert into wechat_comment(CONTENT,CREATED,BELONG_ID,USER_ID,TYPE,URL) values(?,?,?,?,?,?)";
        String content = (String) params.get("comment");
        Date created = new Date();
        Integer belongId = Integer.parseInt((String) params.get("belongId"));
        Integer userId = user.getId();
        Integer type = Integer.parseInt((String) params.get("type"));
        String url = (String) params.get("url");
        return this.getJdbcTemplate().update(sql, new Object[]{content, created, belongId, userId, type, url});
    }


    /*
    * 回复用户
    * */
    @Override
    public Integer replyComment(Map<String, Object> params) {
        if (params == null) {
            throw new CircleException(CircleEnum.PARAM_NULL.getMsg());
        }
        //插入数据库
        String sql = "insert into wechat_reply(USER_ID,CONTENT,CREATED,COMMENT_ID,TOUSER_ID,URL) values(?,?,?,?,?,?)";
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getId();
        String content = (String) params.get("content");
        Date created = new Date();
        Integer commentId = (Integer) params.get("commentId");
        Integer touserId = (Integer) params.get("touserId");
        String url = (String) params.get("url");
        return this.getJdbcTemplate().update(sql, new Object[]{userId, content, created, commentId, touserId, url});
    }

    @Override
    public List<Map<String, Object>> getReplyComment(Integer commentId, Integer page, Integer size) {
        String sql = "select u.USERNAME,u.ID,u.STATUE,wr.CONTENT,wr.CREATED,wr.URL from wechat_reply wr,all_user u where u.ID=wr.USER_ID and wr.COMMENT_ID =? ORDER BY wr.CREATED DESC limit ?,?";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{commentId, (page - 1) * size, size});
    }

    @Transactional
    @Override
    public Integer deleteComment(Integer id) {
        String sql = "delete from wechat_comment where id=?";
        return this.getJdbcTemplate().update(sql, new Object[]{id});
    }


    @Override
    public Integer likeComment(Integer commentId) {
        String sql = "update wechat_comment set COUNT=COUNT+1 where ID=?";
        return this.getJdbcTemplate().update(sql, new Object[]{commentId});
    }

    @Override
    public Integer likeReply(Integer replyId) {
        String sql = "update wechat_reply set COUNT=COUNT+1 where ID=?";
        return this.getJdbcTemplate().update(sql, new Object[]{replyId});
    }

    @Override
    public List<Map<String, Object>> getAllReply(Integer commentId, Integer page, Integer size, String order) {
        String sql = "select u.USERNAME,u.IMG,u.STATUE,wr.* from wechat_reply wr,all_user u where u.ID = wr.USER_ID and wr.COMMENT_ID = ? ORDER BY CREATED " + order + " limit ?,?";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{commentId, (page - 1) * size, size});
    }

    @Override
    public Map<String, Object> getCommentById(Integer id) {
        String sql = "select wc.*,u.USERNAME,u.IMG,u.STATUE from wechat_comment wc ,all_user u where wc.ID = ? and wc.USER_ID=u.ID ";
        List<Map<String, Object>> result = this.getJdbcTemplate().queryForList(sql, new Object[]{id});
        if (result != null && result.size() != 0) {
            return result.get(0);
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> getCommentsByUserId(Integer userId, Integer page, Integer size) {
        String sql = "select wc.*,u.USERNAME,u.IMG,u.STATUE from all_user u,wechat_comment wc where wc.USER_ID = u.ID and wc.USER_ID = ? limit ?,?";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{userId, (page - 1) * size, size});
    }


}
