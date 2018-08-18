package com.yxg.football.backendweb.controller;

import com.google.gson.Gson;
import com.yxg.football.backendweb.config.JwtUtil;
import com.yxg.football.backendweb.config.MyWebSocketChannelHandler;
import com.yxg.football.backendweb.dao.RedisDao;
import com.yxg.football.backendweb.dao.UserDao;
import com.yxg.football.backendweb.service.UserService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StartWebSocket {
    @Autowired
    UserDao userDao;
    @Autowired
    Gson gson;
    @Autowired
    RedisDao redisDao;
    @Autowired
    JwtUtil jwtUtil;

    @GetMapping(value = "/action/webSocket")
    public void action(String id) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            //开启服务端
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(eventLoopGroup, workGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new MyWebSocketChannelHandler(id,userDao, gson, redisDao, jwtUtil));
            System.out.println("服务端开启等待客户端连接..");
            Channel channel = serverBootstrap.bind(8888).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //退出程序
            eventLoopGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
