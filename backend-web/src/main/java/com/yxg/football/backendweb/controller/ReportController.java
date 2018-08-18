package com.yxg.football.backendweb.controller;

import com.yxg.football.backendweb.entity.Result;
import com.yxg.football.backendweb.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/football/api")
public class ReportController {
    @Autowired
    ReportService reportService;

    @PostMapping("/report")
    public ResponseEntity<?> report(@RequestBody Map<String, Object> params) throws Exception {
        reportService.report(params);
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }
}
