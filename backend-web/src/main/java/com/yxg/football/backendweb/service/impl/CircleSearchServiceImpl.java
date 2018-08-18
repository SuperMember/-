package com.yxg.football.backendweb.service.impl;

import com.yxg.football.backendweb.dao.CircleSearchDao;
import com.yxg.football.backendweb.entity.Circle;
import com.yxg.football.backendweb.service.CircleSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CircleSearchServiceImpl implements CircleSearchService {
    @Autowired
    CircleSearchDao circleSearchDao;

    @Transactional
    @Override
    public List<Map<String, Object>> getCircles(String name) {
        List<Circle> list = circleSearchDao.getByTitle(name);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Circle circle = list.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("ID", circle.getID());
            map.put("IMG", circle.getIMG());
            map.put("BACKGROUND", circle.getBACKGROUND());
            map.put("STATUE", circle.getSTATUE());
            map.put("TYPE", circle.getTYPE());
            map.put("TITLE", circle.getTitle());
            map.put("CREATED", circle.getCREATED());
            map.put("COUNT", circle.getCOUNT());
            map.put("INTRODUCTION", circle.getINTRODUCTION());
            map.put("USER_ID", circle.getUSERID());
            resultList.add(map);
        }
        return resultList;
    }
}
