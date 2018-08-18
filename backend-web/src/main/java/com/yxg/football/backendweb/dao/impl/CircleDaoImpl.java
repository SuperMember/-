package com.yxg.football.backendweb.dao.impl;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.yxg.football.backendweb.dao.BaseDao;
import com.yxg.football.backendweb.dao.CircleDao;
import com.yxg.football.backendweb.entity.User;
import com.yxg.football.backendweb.enums.CircleEnum;
import com.yxg.football.backendweb.exceptions.CircleException;
import com.yxg.football.backendweb.util.Const;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
public class CircleDaoImpl extends BaseDao implements CircleDao {
    @Override
    public List<Map<String, Object>> getArticles(Integer cid, Integer page, Integer size) {
        String sql = "select wca.*,u.USERNAME,u.IMG,u.STATUE from wechat_circle_article wca ,all_user u  where wca.C_ID=? and wca.USER_ID = u.ID and wca.TOP = 0 ORDER BY wca.CREATED DESC limit ?,? ";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{cid, (page - 1) * size, size});
    }

    /*
    * 根据回复时间获取帖子
    * */
    @Override
    public List<Map<String, Object>> getArticlesByTime(Integer cid, Integer page, Integer size) {
        String sql = "select wca.*,u.USERNAME,u.IMG,u.STATUE from wechat_circle_article wca ,all_user u  where wca.C_ID=? and wca.USER_ID = u.ID and wca.TOP = 0 ORDER BY wca.UPDATED DESC limit ?,?";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{cid, (page - 1) * size, size});
    }

    @Override
    public List<Map<String, Object>> getAsByTime(Integer page, Integer size) {
        String sql = "select wca.*,u.* from wechat_circle_article wca, all_user u where wca.USER_ID = u.ID order by wca.CREATED limit ?,?";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{(page - 1) * size, size});
    }

    @Transactional
    @Override
    public Integer insertArticle(Map<String, Object> params) {
        if (params == null) {
            throw new CircleException(CircleEnum.PARAM_NULL.getMsg());
        }
        String content = (String) params.get("content");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getId();
        Integer cId = Integer.parseInt((String) params.get("cId"));
        String title = (String) params.get("title");
        String imgs = (String) params.get("imgs");
        Integer top = (Integer) params.get("top");
        Integer topArticle = top == null ? 0 : top;
        Date date = new Date();
        String sql = "insert into wechat_circle_article(CONTENT,CREATED,USER_ID,C_ID,TITLE,IMGS,TOP) values(?,?,?,?,?,?,?)";
        return this.getJdbcTemplate().update(sql, new Object[]{content, date, userId, cId, title, imgs, topArticle});
    }


    /*
    * 帖子删除
    * */
    @Transactional
    @Override
    public Integer deleteArticle(Integer aId) {
        String sql = "delete from wechat_circle_article where ID=?";
        return this.getJdbcTemplate().update(sql, new Object[]{aId});
    }


    /*
    * 判断圈子唯一性
    * */
    @Override
    public Boolean isExistCircle(String name) {
        String sql = "select * from wechat_circle where TITLE=?";
        List<Map<String, Object>> resultList = this.getJdbcTemplate().queryForList(sql, new Object[]{name});
        if (resultList != null && resultList.size() != 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> getCircles(Integer statue, Integer type) {
        String sql = "select u.ID,u.USERNAME,u.STATUE,wc.* from wechat_circle wc,all_user u where wc.STATUE=1 and wc.TYPE = ? and wc.USER_ID = u.ID";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{type});
    }

    @Override
    public Integer createCircle(Map<String, Object> params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "insert into wechat_circle(TITLE,INTRODUCTION,USER_ID,IMG,STATUE,BACKGROUND,TYPE) values(?,?,?,?,?,?,?)";
        String circleTitle = (String) params.get("title");
        String introduction = (String) params.get("introduction");
        Double d = (Double) params.get("userId");
        Integer userId = Integer.valueOf(d.intValue());
        String img = (String) params.get("img");
        String background = (String) params.get("background");
        Double t = (Double) params.get("type");
        Integer type = Integer.valueOf(t.intValue());
        this.getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, circleTitle);
                ps.setString(2, introduction);
                ps.setInt(3, userId);
                ps.setString(4, img);
                ps.setInt(5, 1);
                ps.setString(6, background);
                ps.setInt(7, type);
                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }


    /*
    * 删除圈子
    * */
    @Override
    public Integer deleteCircle(Integer id) {
        String sql = "delete from wechat_circle where TITLE=?";
        return this.getJdbcTemplate().update(sql, new Object[]{id});
    }


    /*
    * 修改圈子状态,设置为正常状态
    * */
    @Override
    public Integer updateCircle(String title) {
        String sql = "update wechat_circle set STATUE=1 where TITLE=?";
        return this.getJdbcTemplate().update(sql, new Object[]{title});
    }

    /*
    * 根据id获取文章
    * */
    @Override
    public Map<String, Object> getArticleById(Integer id) {
        String sql = "select wca.*,u.USERNAME,u.IMG,u.STATUE from wechat_circle_article wca ,all_user u  where wca.ID= ? and wca.USER_ID = u.ID";
        List<Map<String, Object>> resultList = this.getJdbcTemplate().queryForList(sql, new Object[]{id});
        if (resultList != null && resultList.size() != 0) {
            return resultList.get(0);
        }
        return null;
    }


    /*
    * 获取用户的所有帖子
    * */
    @Override
    public List<Map<String, Object>> getArticleByUserId(Integer userId) {
        String sql = "select wca.*,wc.TITLE cTITLE,u.USERNAME,u.IMG,u.STATUE from wechat_circle_article wca ,wechat_circle wc,all_user u where wca.USER_ID = ? and wca.C_ID = wc.ID and u.ID = wca.USER_ID";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{userId});
    }

    /*
    * 关注圈子
    * */
    @Override
    public Integer focusCircle(Integer userId, Integer circleId) {
        String sql = "insert into wechat_circle_focus(USERID,CIRCLEID,CREATED) values(?,?,?)";
        Date created = new Date();
        return this.getJdbcTemplate().update(sql, new Object[]{userId, circleId, created});
    }


    /*
    * 获取用户关注的圈子
    * */
    @Override
    public List<Map<String, Object>> getCircleFocusByUserId(Integer userId) {
        String sql = "select wc.* from wechat_circle_focus wcf,wechat_circle wc where wc.ID = wcf.CIRCLEID and wcf.USERID = ?";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{userId});
    }

    /*
    * 取消关注
    * */
    @Override
    public Integer unFocusCircle(Integer userId, Integer circleId) {
        String sql = "delete from wechat_circle_focus where USERID = ? and CIRCLEID = ?";
        return this.getJdbcTemplate().update(sql, new Object[]{userId, circleId});
    }

    /*
    * 获取置顶帖
    * */
    @Override
    public List<Map<String, Object>> getTopList(Integer circleId) {
        String sql = "select wca.*,u.USERNAME,u.IMG,u.STATUE from wechat_circle_article wca ,all_user u  where wca.C_ID=? and wca.USER_ID = u.ID and wca.TOP = 1 ORDER BY wca.CREATED DESC limit 0,3";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{circleId});
    }

    @Override
    public Integer updateTime(Integer id) {
        String sql = "update wechat_circle_article set UPDATED = ? where ID = ?";
        return this.getJdbcTemplate().update(sql, new Object[]{new Date(), id});
    }

    @Override
    public Integer updateReplyTime(Integer commentId) {
        String sql = "update wechat_circle_article w set w.UPDATED = ? where w.ID = (select ID from (select wca.ID AS ID from wechat_circle_article wca ,wechat_comment wc,wechat_reply wr where wr.COMMENT_ID = ? and wr.COMMENT_ID = wc.ID and wc.BELONG_ID = wca.ID) AS temp)";
        return this.getJdbcTemplate().update(sql, new Object[]{new Date(), commentId});
    }

    @Override
    public List<Map<String, Object>> getCircleOwner(Integer cId) {
        String sql = "select u.IMG,u.USERNAME,u.STATUE,u.ID,u.CREATED from all_user u,wechat_circle wc where wc.USER_ID = u.ID and wc.ID = ?";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{cId});
    }


}
