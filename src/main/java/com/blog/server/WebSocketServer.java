package com.blog.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket 服务端
 * 处理客户端连接、消息推送
 *
 * @author worlt
 */
@Component
@ServerEndpoint("/ws/{userId}")
@Slf4j
public class WebSocketServer {

    // 静态变量，用来记录当前在线连接数。应该设计成线程安全的。
    private static int onlineCount = 0;

    // concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    // private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new
    // CopyOnWriteArraySet<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 接收 userId
    private String userId = "";

    // 存放每个客户端对应的 WebSocketServer 对象，Key 为 userId
    // 使用 ConcurrentHashMap 保证线程安全
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;

        // 如果已经存在该用户的连接，先移除旧的（或者考虑允许多端登录，这里简单起见覆盖）
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            // webSocketSet.remove((WebSocketServer)webSocketMap.get(userId)); // 如果维护了Set
            // 之前的连接不一定要关闭，或者关闭它防止幽灵连接
            try {
                // webSocketMap.get(userId).session.close(); // 可选：踢下线
            } catch (Exception e) {
                log.error("关闭旧连接异常");
            }
        } else {
            addOnlineCount(); // 在线数加1
        }

        webSocketMap.put(userId, this);
        log.info("用户连接: userId={}, 当前在线人数: {}", userId, getOnlineCount());

        try {
            sendMessage("连接成功");
        } catch (IOException e) {
            log.error("用户: {}, 网络异常", userId);
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            subOnlineCount(); // 在线数减1
        }
        log.info("用户退出: userId={}, 当前在线人数: {}", userId, getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到用户消息: userId={}, 报文: {}", userId, message);
        // 可以根据业务需求处理客户端发来的消息，例如心跳检测 pong
        if ("ping".equals(message)) {
            try {
                sendMessage("pong");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户Websocket错误: userId={}, 原因: {}", userId, error.getMessage());
        // error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 发送自定义消息给指定用户
     */
    public static void sendInfo(String message, String userId) {
        log.debug("推送消息到用户: userId={}, 内容: {}", userId, message);
        if (userId != null && webSocketMap.containsKey(userId)) {
            try {
                webSocketMap.get(userId).sendMessage(message);
            } catch (IOException e) {
                log.error("推送消息失败: userId={}, 异常: {}", userId, e.getMessage());
                // 可以在这里处理移除断开的连接
            }
        } else {
            log.debug("用户不在线: userId={}", userId);
        }
    }

    /**
     * 判断用户是否在线
     */
    public static boolean isOnline(String userId) {
        return webSocketMap.containsKey(userId);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
