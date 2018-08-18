package com.yxg.football.backendweb.dao;

import com.yxg.football.backendweb.entity.Circle;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface CircleSearchDao extends ElasticsearchRepository<Circle, Integer> {
    List<Circle> getByTitle(String title);
}
