package com.yxg.football.backendweb.config;

import com.google.gson.Gson;
import com.yxg.football.backendweb.dao.RedisDao;
import com.yxg.football.backendweb.dao.UserDao;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 初始化链接时候的组件
 */
public class MyWebSocketChannelHandler extends ChannelInitializer<SocketChannel> {
    public Gson gson;
    public JwtUtil jwtUtil;
    public UserDao userDao;
    public RedisDao redisDao;
    public String id;
    public MyWebSocketChannelHandler(String id,UserDao userDao, Gson gson, RedisDao redisDao, JwtUtil jwtUtil) {
        //初始化
        this.id=id;
        this.userDao = userDao;
        this.jwtUtil = jwtUtil;
        this.redisDao = redisDao;
        this.gson = gson;
    }

    @Override
    protected void initChannel(SocketChannel e) throws Exception {
        e.pipeline().addLast("http-codec", new HttpServerCodec());
        e.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        e.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        e.pipeline().addLast("handler", new MyWebSockeHandler(id,userDao, gson, redisDao, jwtUtil));
    }
}
