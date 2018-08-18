package com.yxg.football.backendmanager.dao;

import java.util.List;
import java.util.Map;

public interface ReportDao {
    Integer insertReport(Integer userId, Map<String, Object> params);

    List<Map<String, Object>> getReport(Integer page, Integer size, Integer type, Integer result);

    Integer getReportByTypeCount(Integer type, Integer reuslt);

    List<Map<String, Object>> getReportDataByType(Integer type, Integer id);

    Integer setReportStatue(Integer statue, Integer id);
}
