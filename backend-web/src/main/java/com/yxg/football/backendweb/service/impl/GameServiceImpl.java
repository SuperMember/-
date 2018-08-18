package com.yxg.football.backendweb.service.impl;

import com.yxg.football.backendweb.dao.GameDao;
import com.yxg.football.backendweb.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {
    @Autowired
    GameDao gameDao;

    @Override
    public Integer getGameCount(Integer gameId) {
        return gameDao.getGameCount(gameId);
    }
}
