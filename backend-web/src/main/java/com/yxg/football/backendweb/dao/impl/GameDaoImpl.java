package com.yxg.football.backendweb.dao.impl;

import com.yxg.football.backendweb.dao.BaseDao;
import com.yxg.football.backendweb.dao.GameDao;
import org.springframework.stereotype.Repository;

@Repository
public class GameDaoImpl extends BaseDao implements GameDao {
    @Override
    public Integer getGameCount(Integer gameId) {
        String sql = "select LCOUNT from wechat_game where LID = ? ";
        return this.getJdbcTemplate().queryForObject(sql, new Object[]{gameId}, Integer.class);
    }
}
