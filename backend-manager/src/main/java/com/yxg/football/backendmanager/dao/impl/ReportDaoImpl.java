package com.yxg.football.backendmanager.dao.impl;

import com.yxg.football.backendmanager.dao.BaseDao;
import com.yxg.football.backendmanager.dao.ReportDao;
import com.yxg.football.backendmanager.enums.ReportEnum;
import com.yxg.football.backendmanager.enums.ReportResultEnum;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class ReportDaoImpl extends BaseDao implements ReportDao {
    @Override
    public Integer insertReport(Integer userId, Map<String, Object> params) {
        String sql = "insert into wechat_report(CONTENT,USER_ID,TYPE,CREATED,RUSER_ID,URL,BELONG_ID) values(?,?,?,?,?,?,?)";
        String content = (String) params.get("content");
        Integer type = (Integer) params.get("type");
        Date created = new Date();
        Integer ruserId = (Integer) params.get("ruserId");
        String url = (String) params.get("url");
        Integer belongId = (Integer) params.get("belongId");
        return this.getJdbcTemplate().update(sql, new Object[]{content, userId, type, created, ruserId, url, belongId});
    }

    @Override
    public List<Map<String, Object>> getReport(Integer page, Integer size, Integer type, Integer result) {
        StringBuilder sql = new StringBuilder("select * from wechat_report where 1=1 ");
        List<Object> args = new ArrayList<>();
        if (type != ReportEnum.REPORT_ALL.getCode()) {
            sql.append("and TYPE = ?");
            args.add(type);
        }
        if (result != ReportResultEnum.RESULT_ALL.getCode()) {
            sql.append(" and RESULT = ?");
            args.add(result);
        }
        sql.append("  ORDER BY CREATED limit ?,? ");
        args.add((page - 1) * size);
        args.add(size);
        return this.getJdbcTemplate().queryForList(new String(sql), args.toArray());
    }

    @Override
    public Integer getReportByTypeCount(Integer type, Integer result) {
        StringBuilder sql = new StringBuilder("select count(*) from wechat_report where 1=1 ");
        List<Object> args = new ArrayList<>();
        if (type != ReportEnum.REPORT_ALL.getCode()) {
            sql.append(" and TYPE = ?");
            args.add(type);
        }
        if (result != ReportResultEnum.RESULT_ALL.getCode()) {
            sql.append(" and RESULT = ?");
            args.add(result);
        }
        return this.getJdbcTemplate().queryForObject(new String(sql), args.toArray(), Integer.class);
    }

    @Override
    public List<Map<String, Object>> getReportDataByType(Integer type, Integer id) {
        if (type == 0) {
            //评论
            String sql = "select * from wechat_comment where ID = ?";
            return this.getJdbcTemplate().queryForList(sql, new Object[]{id});
        } else if (type == 1) {
            // 文章
            String sql = "select * from vue_article where Id = ?";
            return this.getJdbcTemplate().queryForList(sql, new Object[]{id});
        } else if (type == 2) {
            //回复
            String sql = "select * from wechat_reply where ID = ?";
            return this.getJdbcTemplate().queryForList(sql, new Object[]{id});
        }
        return null;
    }

    @Override
    public Integer setReportStatue(Integer statue, Integer id) {
        String sql = "update wechat_report set RESULT = ? where ID = ?";
        return this.getJdbcTemplate().update(sql, new Object[]{statue, id});
    }
}
