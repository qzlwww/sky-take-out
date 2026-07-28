package com.sky.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@ServerEndpoint("/ws/{token}")
public class WebSocketServer {

    private static Map<String, Session> sessionMap = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        log.info("WebSocket连接建立：{}", token);
        sessionMap.put(token, session);
    }

    @OnClose
    public void onClose(@PathParam("token") String token) {
        log.info("WebSocket连接关闭：{}", token);
        sessionMap.remove(token);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("token") String token) {
        log.info("收到消息：{}, 来自：{}", message, token);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket错误：{}", error.getMessage());
    }

    public void sendToAllClient(String message) {
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                log.error("消息发送失败：{}", e.getMessage());
            }
        }
    }
}
