package com.yxg.football.backendmanager.service;

import java.util.List;
import java.util.Map;

public interface ReportService {
    Integer insertReport(Integer userId, Map<String, Object> params);

    List<Map<String, Object>> getReport(Integer page, Integer size, Integer type, Integer result);

    Integer getReportByTypeCount(Integer type, Integer result);

    List<Map<String, Object>> getReportDataByType(Integer type, Integer id);

    Integer setReportStatue(Integer statue, Integer id, Integer belongId, Integer type, Integer userId);
}
