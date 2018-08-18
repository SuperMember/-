package com.yxg.football.backendmanager.service.impl;

import com.yxg.football.backendmanager.dao.ArticleDao;
import com.yxg.football.backendmanager.dao.CommentDao;
import com.yxg.football.backendmanager.dao.ReportDao;
import com.yxg.football.backendmanager.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    ReportDao reportDao;
    @Autowired
    CommentDao commentDao;
    @Autowired
    ArticleDao articleDao;
    @Autowired
    RestTemplate restTemplate;

    @Override
    public Integer insertReport(Integer userId, Map<String, Object> params) {
        return reportDao.insertReport(userId, params);
    }

    @Override
    public List<Map<String, Object>> getReport(Integer page, Integer size, Integer type, Integer result) {
        return reportDao.getReport(page, size, type, result);
    }

    @Override
    public Integer getReportByTypeCount(Integer type, Integer result) {
        return reportDao.getReportByTypeCount(type, result);
    }

    @Override
    public List<Map<String, Object>> getReportDataByType(Integer type, Integer id) {
        return reportDao.getReportDataByType(type, id);
    }

    @Transactional
    @Override
    public Integer setReportStatue(Integer statue, Integer id, Integer belongId, Integer type, Integer userId) {
        try {
            reportDao.setReportStatue(statue, id);
            //举报成功之后删除评论或者文章
            if (type == 0) {
                //评论
                return commentDao.deleteComment(belongId, userId);
            } else if (type == 1) {
                //文章
                articleDao.deleteArticle(userId, belongId + "");
            } else {
                //回复
                commentDao.deleteReply(belongId, userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
