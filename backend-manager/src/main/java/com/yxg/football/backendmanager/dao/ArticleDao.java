package com.yxg.football.backendmanager.dao;

import java.util.List;
import java.util.Map;

public interface ArticleDao {
    List<Map<String, Object>> getArticle(Integer size, Integer page, Integer userid, Integer status, Integer type);

    Integer saveArticle(Integer userId, Map<String, Object> params);

    int[] deleteArticle(Integer userid, String articleId);

    Long getArticleCount(Integer userId, Integer statue, Integer type);

    List<Map<String, Object>> getAllArticle(Integer statue, Integer page, Integer size);

    Integer updateArticle(Integer id, Integer statue);

    Integer getArticleCountByStatue(Integer statue);

    Integer updateArticleContent(Map<String, Object> params);

    Map<String, Object> getArticleById(Integer articleId);

}
