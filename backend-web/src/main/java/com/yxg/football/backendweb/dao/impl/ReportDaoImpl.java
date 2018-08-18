package com.yxg.football.backendweb.dao.impl;

import com.yxg.football.backendweb.dao.BaseDao;
import com.yxg.football.backendweb.dao.ReportDao;
import com.yxg.football.backendweb.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Map;

@Repository
public class ReportDaoImpl extends BaseDao implements ReportDao {
    @Override
    public Integer report(Map<String, Object> params) {
        String sql = "insert into wechat_report(CONTENT,USER_ID,CREATED,TYPE,RUSER_ID,URL,BELONG_ID) values(?,?,?,?,?,?,?)";
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String content = (String) params.get("content");
        Integer userId = user.getId();
        Date created = new Date();
        Integer type = (Integer) params.get("type");
        Integer ruserId = (Integer) params.get("ruserId");
        Integer belongId = (Integer) params.get("belongId");
        String url = (String) params.get("url");
        return this.getJdbcTemplate().update(sql, new Object[]{content, userId, created, type, ruserId, url, belongId});
    }
}
