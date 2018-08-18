package com.yxg.football.backendmanager.service.impl;

import com.yxg.football.backendmanager.dao.ArticleDao;
import com.yxg.football.backendmanager.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    ArticleDao articleDao;

    @Override
    public List<Map<String, Object>> getArticle(Integer size, Integer page, Integer userid, Integer status, Integer type) throws Exception {
        return articleDao.getArticle(size, page, userid, status, type);
    }

    @Transactional
    @Override
    public Integer saveArticle(Integer userId, Map<String, Object> params) throws Exception {
        return articleDao.saveArticle(userId, params);
    }

    @Transactional
    @Override
    public int[] deleteArticle(Integer userid, String ids) throws Exception {
        int[] result = articleDao.deleteArticle(userid, ids);
        return result;
    }

    @Override
    public Long getCount(Integer userId, Integer statue, Integer type) throws Exception {
        return articleDao.getArticleCount(userId, statue, type);
    }

    /*
    * 管理端获取全部文章
    * */
    @Override
    public List<Map<String, Object>> getAllArticle(Integer statue, Integer page, Integer size) {
        return articleDao.getAllArticle(statue, page, size);
    }

    /*
    * 管理端修改文章状态
    * */
    @Override
    public Integer updateArticle(Integer id, Integer statue) {
        return articleDao.updateArticle(id, statue);
    }

    @Override
    public Integer getArticleCountByStatue(Integer statue) {
        return articleDao.getArticleCountByStatue(statue);
    }

    @Override
    public Integer updateArticleContent(Map<String, Object> params) {
        return articleDao.updateArticleContent(params);
    }

    @Override
    public Map<String, Object> getArticleById(Integer articleId) {
        return articleDao.getArticleById(articleId);
    }
}
