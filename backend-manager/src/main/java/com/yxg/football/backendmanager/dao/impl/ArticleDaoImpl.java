package com.yxg.football.backendmanager.dao.impl;

import com.yxg.football.backendmanager.dao.ArticleDao;
import com.yxg.football.backendmanager.dao.BaseDao;
import com.yxg.football.backendmanager.enums.ArticleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class ArticleDaoImpl extends BaseDao implements ArticleDao {

    @Override
    public List<Map<String, Object>> getArticle(Integer size, Integer page, Integer userid, Integer status, Integer type) {
        StringBuilder sql = new StringBuilder("select * from vue_article where USERID = ?");
        List<Object> args = new ArrayList<>();
        args.add(userid);
        if (status != ArticleEnum.ARTICLE_ALL.getCode()) {
            //按状态查询
            sql.append(" and STATUE = ? ");
            args.add(status);
        }
        sql.append(" and TYPE = ? order by CREATED limit ?,? ");
        args.add(type);
        args.add((page - 1) * size);
        args.add(size);
        return this.getJdbcTemplate().queryForList(new String(sql), args.toArray());
    }

    @Override
    public Integer saveArticle(Integer userId, Map<String, Object> params) {
        String content = (String) params.get("content");
        String title = (String) params.get("title");
        String stitle = (String) params.get("stitle");
        Date date = new Date();
        Integer status = (Integer) params.get("status");
        Integer type = (Integer) params.get("type");
        String img = (String) params.get("img");
        String sql = "insert into vue_article(CONTENT,TITLE,STITLE,USERID,CREATED,STATUE,TYPE,IMG) values(?,?,?,?,?,?,?,?)";
        return this.getJdbcTemplate().update(sql, new Object[]{content, title, stitle, userId, date, status, type, img});
    }

    /*
    * 批量删除
    * */
    @Override
    public int[] deleteArticle(Integer userid, String articleId) {
        String sql = "delete from vue_article where ID = ? and USERID = ?";
        String[] ids = articleId.split(",");
        List<Integer> articles = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            articles.add(Integer.parseInt(ids[i]));
        }
        return this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1, articles.get(i));
                preparedStatement.setInt(2, userid);
            }

            @Override
            public int getBatchSize() {
                return articles.size();
            }
        });
    }

    @Override
    public Long getArticleCount(Integer userId, Integer statue, Integer type) {
        StringBuilder sql = new StringBuilder("select count(*) from vue_article where USERID=?");
        List<Object> args = new ArrayList<>();
        args.add(userId);
        if (statue != ArticleEnum.ARTICLE_ALL.getCode()) {
            sql.append(" and STATUE = ? ");
            args.add(statue);
        }
        sql.append(" and TYPE = ?");
        args.add(type);
        return (Long) this.getJdbcTemplate().queryForMap(new String(sql), args.toArray()).get("count(*)");
    }

    /*
    * 管理端获取所有的文章
    * */
    @Override
    public List<Map<String, Object>> getAllArticle(Integer statue, Integer page, Integer size) {
        StringBuilder sql = new StringBuilder("select * from vue_article where 1=1");
        List<Object> args = new ArrayList<>();
        if (statue != ArticleEnum.ARTICLE_ALL.getCode()) {
            //全部
            sql.append(" and STATUE = ?");
            args.add(statue);
        }
        sql.append(" order by CREATED limit ?,?");
        args.add((page - 1) * size);
        args.add(size);
        return this.getJdbcTemplate().queryForList(new String(sql), args.toArray());
    }

    /*
    * 修改文章状态
    * */
    @Override
    public Integer updateArticle(Integer id, Integer statue) {
        String sql = "update vue_article set STATUE=? where ID=?";
        return this.getJdbcTemplate().update(sql, new Object[]{statue, id});
    }

    @Override
    public Integer getArticleCountByStatue(Integer statue) {
        StringBuilder sql = new StringBuilder("select count(*) from vue_article where 1=1");
        List<Object> args = new ArrayList<>();
        if (statue != ArticleEnum.ARTICLE_ALL.getCode()) {
            //查找全部
            sql.append(" and STATUE = ?");
            args.add(statue);
        }
        return this.getJdbcTemplate().queryForObject(new String(sql), args.toArray(), Integer.class);
    }

    /*
    * 修改文章内容
    * */
    @Override
    public Integer updateArticleContent(Map<String, Object> params) {
        String title = (String) params.get("title");
        String stitle = (String) params.get("stitle");
        String content = (String) params.get("content");
        Integer articleId = (Integer) params.get("articleId");
        String sql = "update vue_article set TITLE = ? , STITLE = ? , CONTENT = ? where ID =? ";
        return this.getJdbcTemplate().update(sql, new Object[]{title, stitle, content, articleId});
    }

    @Override
    public Map<String, Object> getArticleById(Integer articleId) {
        String sql = "select * from vue_article where ID = ? ";
        List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, new Object[]{articleId});
        if (list != null && list.size() != 0) {
            return list.get(0);
        }
        return null;
    }
}
