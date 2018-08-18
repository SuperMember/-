package com.yxg.football.backendweb.controller;

import com.yxg.football.backendweb.dao.CircleSearchDao;
import com.yxg.football.backendweb.entity.Circle;
import com.yxg.football.backendweb.entity.Result;
import com.yxg.football.backendweb.service.CircleSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;

@RestController
@RequestMapping("/football/api/search")
public class SearchController {
    @Autowired
    CircleSearchService circleSearchService;

    //圈子搜索
    @GetMapping("/list")
    public ResponseEntity<?> getSearchList(String name) {
        return new ResponseEntity<Object>(Result.genSuccessResult(circleSearchService.getCircles(name)), HttpStatus.OK);
    }



}
