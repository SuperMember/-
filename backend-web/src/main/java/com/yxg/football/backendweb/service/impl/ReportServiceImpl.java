package com.yxg.football.backendweb.service.impl;

import com.yxg.football.backendweb.dao.ReportDao;
import com.yxg.football.backendweb.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    ReportDao reportDao;

    @Override
    public Integer report(Map<String, Object> params) {
        return reportDao.report(params);
    }
}
