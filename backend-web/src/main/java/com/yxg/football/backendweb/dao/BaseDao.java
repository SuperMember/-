package com.yxg.football.backendweb.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

public class BaseDao {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    StringRedisTemplate redisTemplater;
    @Autowired
    RedisTemplate redisTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    public StringRedisTemplate getRedisTemplater() {
        return this.redisTemplater;
    }

    public RedisTemplate getTemplate() {
        return redisTemplate;
    }
}
