package com.yxg.football.backendweb.service;

import com.yxg.football.backendweb.entity.Circle;

import java.util.List;
import java.util.Map;

public interface CircleSearchService {
    List<Map<String, Object>> getCircles(String name);
}
