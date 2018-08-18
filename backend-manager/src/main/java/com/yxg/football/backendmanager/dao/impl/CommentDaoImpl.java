package com.yxg.football.backendmanager.dao.impl;

import com.yxg.football.backendmanager.dao.BaseDao;
import com.yxg.football.backendmanager.dao.CommentDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class CommentDaoImpl extends BaseDao implements CommentDao {

    @Override
    public List<Map<String, Object>> getAllComment(Integer statue, Integer type, Integer page, Integer size) {
        StringBuilder sql = new StringBuilder("select * from wechat_comment where 1=1 ");
        List<Object> args = new ArrayList<>();
        if (statue != 2) {
            //根据状态查找数据
            sql.append("and STATUE = ? ");
            args.add(statue);
        }
        if (type != 3) {
            sql.append("and TYPE = ? ");
            args.add(type);
        }
        sql.append(" order by CREATED limit ?,?");
        args.add((page - 1) * size);
        args.add(size);
        return this.getJdbcTemplate().queryForList(new String(sql), args.toArray());
    }

    /*
    * 每删除一次评论则减少用户积分2分
    * */
    @Transactional
    @Override
    public Integer deleteComment(Integer id, Integer userId) {
        String content = "该用户评论不当,评论已经被删除";
        String sql = "update wechat_comment set CONTENT=? , STATUE = 1 where ID=?";
        this.getJdbcTemplate().update(sql, new Object[]{content, id});
        //减少积分2分
        String user = "update all_user set POINTS = POINTS-2 where ID=?";
        return this.getJdbcTemplate().update(user, new Object[]{userId});
    }

    @Override
    public Integer getCommentCount() {
        String sql = "select count(*) from wechat_comment";
        return this.getJdbcTemplate().queryForObject(sql, Integer.class);
    }

    @Override
    public List<Map<String, Object>> getReplyById(Integer replyId, Integer page, Integer size) {
        String sql = "select wr.*,u.USERNAME,u.IMG from wechat_reply wr,all_user u where wr.COMMENT_ID = ? and wr.USER_ID = u.ID limit ? , ?";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{replyId, (page - 1) * size, size});
    }

    @Override
    public Integer getReplyCount(Integer replyId) {
        String sql = "select count(*) from wechat_reply where COMMENT_ID = ?";
        return this.getJdbcTemplate().queryForObject(sql, new Object[]{replyId}, Integer.class);
    }

    @Override
    public Integer getCommentByUserIdCount(Integer userId) {
        String sql = "select count(*) from vue_article v, wechat_comment wc where v.USERID = ? and v.ID = wc.BELONG_ID and wc.TYPE = 2";
        return this.getJdbcTemplate().queryForObject(sql, new Object[]{userId}, Integer.class);
    }

    @Override
    public Integer replyComment(Integer userId, Map<String, Object> params) {
        String sql = "insert into wechat_reply(USER_ID,CONTENT,CREATED,COMMENT_ID,TOUSER_ID,URL) values(?,?,?,?,?,?)";
        String content = (String) params.get("content");
        Date created = new Date();
        Integer commentId = (Integer) params.get("commentId");
        Integer touserId = (Integer) params.get("touserId");
        String url = (String) params.get("url");
        return this.getJdbcTemplate().update(sql, new Object[]{userId, content, created, commentId, touserId, url});
    }

    @Override
    public List<Map<String, Object>> getReplyByUserId(Integer userId, Integer page, Integer size) {
        String sql = "select * from wechat_reply where USER_ID = ? limit ?,?";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{userId, (page - 1) * size, size});
    }

    @Override
    public Integer getReplyByUserIdCount(Integer userId) {
        String sql = "select count(*) from wechat_reply where USER_ID = ?";
        return this.getJdbcTemplate().queryForObject(sql, new Object[]{userId}, Integer.class);
    }

    @Override
    public List<Map<String, Object>> getCommentById(Integer commentId) {
        String sql = "select * from wechat_comment where ID = ?";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{commentId});
    }

    @Override
    public Integer deleteReply(Integer replyId, Integer userId) {
        String content = "该用户评论不当,评论已经被删除";
        String sql = "update wechat_reply set CONTENT = ?,STATUE = 1 where USER_ID = ? and ID = ?";
        return this.getJdbcTemplate().update(sql, new Object[]{content, userId, replyId});
    }


}
