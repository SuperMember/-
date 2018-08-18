package com.yxg.football.backendweb.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yxg.football.backendweb.dao.RedisDao;
import com.yxg.football.backendweb.dao.UserDao;
import com.yxg.football.backendweb.dao.impl.RedisDaoImpl;
import com.yxg.football.backendweb.dao.impl.UserDaoImpl;
import com.yxg.football.backendweb.entity.User;
import com.yxg.football.backendweb.service.UserService;
import com.yxg.football.backendweb.service.impl.UserServiceImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接受/处理/响应客户端websocke请求的核心业务处理类
 */
public class MyWebSockeHandler extends SimpleChannelInboundHandler<Object> {
    public Gson gson;
    public JwtUtil jwtUtil;
    public UserDao userDao;
    public RedisDao redisDao;
    public String id;

    public MyWebSockeHandler(String id, UserDao userDao, Gson gson, RedisDao redisDao, JwtUtil jwtUtil) {
        //初始化
        this.id = id;
        this.userDao = userDao;
        this.jwtUtil = jwtUtil;
        this.redisDao = redisDao;
        this.gson = gson;
    }

    private WebSocketServerHandshaker webSocketServerHandshaker;
    private static final String WEB_SOCKET_URL = "ws://localhost:8888/webSocket";

    //客户端与服务端创建链接的时候调用
    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        NettyConfig.group.add(context.channel());
        System.out.println("客户端与服务端连接开启");

    }

    //客户端与服务端断开连接的时候调用
    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        NettyConfig.group.remove(context.channel());
        System.out.println("客户端与服务端连接断开");
    }

    //服务端接收客户端发送过来的数据结束之后调用
    @Override
    public void channelReadComplete(ChannelHandlerContext context) throws Exception {
        context.flush();
    }

    //工程出现异常的时候调用
    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable throwable) throws Exception {
        throwable.printStackTrace();
        context.close();
    }

    //服务端处理客户端websocke请求的核心方法
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        //处理客户端向服务端发起的http握手请求
        if (o instanceof FullHttpRequest) {
            handHttpRequest(channelHandlerContext, (FullHttpRequest) o);
            String uri = ((FullHttpRequest) o).getUri();
            id = uri.substring(uri.lastIndexOf("=") + 1);
            List<Map<String, Object>> resultList = redisDao.getChat(id);
            String json = gson.toJson(resultList);
            TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(json);
            //服务端向每个连接上来的客户端发送消息
            NettyConfig.group.writeAndFlush(textWebSocketFrame);
        } else if (o instanceof WebSocketFrame) {//处理websocket链接业务
            handWebSocketFrame(channelHandlerContext, (WebSocketFrame) o);
        }

    }

    /**
     * 处理客户端与服务端之间的websocket业务
     *
     * @param context
     * @param webSocketFrame
     */
    private void handWebSocketFrame(ChannelHandlerContext context, WebSocketFrame webSocketFrame) {
        if (webSocketFrame instanceof CloseWebSocketFrame) {//判断是否是关闭websocket的指令
            webSocketServerHandshaker.close(context.channel(), (CloseWebSocketFrame) webSocketFrame.retain());
        }
        if (webSocketFrame instanceof PingWebSocketFrame) {//判断是否是ping消息
            context.channel().write(new PongWebSocketFrame(webSocketFrame.content().retain()));
            return;
        }
        if (!(webSocketFrame instanceof TextWebSocketFrame)) {//判断是否是二进制消息
            System.out.println("不支持二进制消息");
            throw new RuntimeException(this.getClass().getName());
        }
        //返回应答消息
        //获取客户端向服务端发送的消息
        String request = ((TextWebSocketFrame) webSocketFrame).text();
        System.out.println("服务端收到客户端的消息：" + request);
        Map<String, Object> map = gson.fromJson(request, new TypeToken<Map<String, Object>>() {
        }.getType());
        //解析代码
        //每个用户的token
        String token = (String) map.get("info");
        //通过token获取用户信息
        String username = jwtUtil.getUsernameFromToken(token);
        //通过用户名获取用户信息
        if (username != null && !username.isEmpty()) {
            User user = userDao.getUserByName(username);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("img", user.getImg());
            resultMap.put("username", user.getUsername());
            resultMap.put("id", user.getId());
            resultMap.put("message", map.get("content"));
            redisDao.setChat(resultMap, id);
        }
        List<Map<String, Object>> resultList = redisDao.getChat(id);
        String json = gson.toJson(resultList);
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(json);
        //服务端向每个连接上来的客户端发送消息
        NettyConfig.group.writeAndFlush(textWebSocketFrame);
    }


    /**
     * 处理客户端向服务端发起http握手请求业务
     *
     * @param context
     * @param fullHttpRequest
     */
    private void handHttpRequest(ChannelHandlerContext context, FullHttpRequest fullHttpRequest) {
        if (!fullHttpRequest.getDecoderResult().isSuccess() || !("websocket".equals(fullHttpRequest.headers().get("Upgrade")))) {//判断是否http握手请求
            sendHttpResponse(context, fullHttpRequest, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        WebSocketServerHandshakerFactory webSocketServerHandshakerFactory = new WebSocketServerHandshakerFactory(WEB_SOCKET_URL, null, false);
        webSocketServerHandshaker = webSocketServerHandshakerFactory.newHandshaker(fullHttpRequest);
        if (webSocketServerHandshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(context.channel());
        } else {
            webSocketServerHandshaker.handshake(context.channel(), fullHttpRequest);
        }
    }

    /**
     * 服务端想客户端发送响应消息
     *
     * @param context
     * @param fullHttpRequest
     * @param defaultFullHttpResponse
     */
    private void sendHttpResponse(ChannelHandlerContext context, FullHttpRequest fullHttpRequest, DefaultFullHttpResponse defaultFullHttpResponse) {
        if (defaultFullHttpResponse.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(defaultFullHttpResponse.getStatus().toString(), CharsetUtil.UTF_8);
            defaultFullHttpResponse.content().writeBytes(buf);
            buf.release();
        }
        //服务端向客户端发送数据
        ChannelFuture future = context.channel().writeAndFlush(defaultFullHttpResponse);
        if (defaultFullHttpResponse.getStatus().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }

    }

}
