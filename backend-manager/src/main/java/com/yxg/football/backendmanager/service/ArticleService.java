package com.yxg.football.backendmanager.service;

import java.util.List;
import java.util.Map;

public interface ArticleService {
    public List<Map<String, Object>> getArticle(Integer size, Integer page, Integer userid, Integer status, Integer type) throws Exception;

    public Integer saveArticle(Integer userId, Map<String, Object> params) throws Exception;

    public int[] deleteArticle(Integer userid, String ids) throws Exception;

    Long getCount(Integer userId, Integer statue, Integer type) throws Exception;

    List<Map<String, Object>> getAllArticle(Integer statue, Integer page, Integer size);

    Integer updateArticle(Integer id, Integer statue);

    Integer getArticleCountByStatue(Integer statue);

    Integer updateArticleContent(Map<String, Object> params);

    Map<String, Object> getArticleById(Integer articleId);

}
