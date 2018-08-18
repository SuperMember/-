package com.yxg.football.backendweb.schedule;


import com.yxg.football.backendweb.dao.RedisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class RankSchedule {
    @Autowired
    RedisDao redisDao;

    /*
    * 重置排行榜数据(每晚12点)
    * */
    @Scheduled(cron = "0 0 0 * * ? ")
    public void resetRank() {
        redisDao.clearRank();
    }
}
