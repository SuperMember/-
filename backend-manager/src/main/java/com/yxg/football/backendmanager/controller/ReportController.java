package com.yxg.football.backendmanager.controller;

import com.yxg.football.backendmanager.entity.Result;
import com.yxg.football.backendmanager.entity.User;
import com.yxg.football.backendmanager.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manager/report")
public class ReportController {
    @Autowired
    ReportService reportService;

    @PostMapping("/list")
    public ResponseEntity<?> insertReport(@RequestBody Map<String, Object> params) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            reportService.insertReport(user.getId(), params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getReports(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        Integer type,
                                        Integer result) {
        List<Map<String, Object>> resultList = null;
        Map<String, Object> resultMap = new HashMap<>();
        try {
            resultList = reportService.getReport(page, size, type, result);
            resultMap.put("data", resultList);
            resultMap.put("count", reportService.getReportByTypeCount(type, result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(resultMap), HttpStatus.OK);
    }

    @GetMapping("/{type}/{id}")
    public ResponseEntity<?> getReportByType(@PathVariable Integer type,
                                             @PathVariable Integer id) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            List<Map<String, Object>> resultList = reportService.getReportDataByType(type, id);
            if (resultList != null && resultList.size() != 0) {
                resultMap = resultList.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(resultMap), HttpStatus.OK);
    }

    @PutMapping("/list")
    public ResponseEntity<?> setReportStatue(@RequestBody Map<String, Object> params) {
        try {
            reportService.setReportStatue((Integer) params.get("statue"), (Integer) params.get("id"), (Integer) params.get("belongId"), (Integer) params.get("type"), (Integer) params.get("userId"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(Result.genSuccessResult(), HttpStatus.OK);
    }
}
