package com.yxg.football.backendweb.dao.impl;

import com.yxg.football.backendweb.dao.BaseDao;
import com.yxg.football.backendweb.dao.IndexDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class IndexDaoImpl extends BaseDao implements IndexDao {
    @Override
    public List<Map<String, Object>> getIndexData(Integer page, Integer size) {
        String sql = "select va.*,u.USERNAME,u.IMG userImg,u.STATUE  from vue_article va ,all_user u where u.ID = va.USERID and va.STATUE = 3 ORDER BY va.CREATED DESC limit ?,?";
        return this.getJdbcTemplate().queryForList(sql, new Object[]{(page - 1) * size, size});
    }

    //获取某个用户发表的文章/视频详情
    @Override
    public Map<String, Object> getArticle(Integer id, Integer type) {
        String sql = "select va.*,u.USERNAME,u.IMG,u.STATUE from vue_article va ,all_user u where u.ID = va.USERID and va.ID = ? and va.TYPE = ?";
        List<Map<String, Object>> result = this.getJdbcTemplate().queryForList(sql, new Object[]{id, type});
        if (result != null && result.size() != 0) {
            return result.get(0);
        }
        return null;
    }
}
