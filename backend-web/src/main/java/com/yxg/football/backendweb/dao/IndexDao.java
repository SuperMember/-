package com.yxg.football.backendweb.dao;

import java.util.List;
import java.util.Map;

public interface IndexDao {
    List<Map<String, Object>> getIndexData(Integer page, Integer size);

    Map<String, Object> getArticle(Integer id, Integer type);
}
