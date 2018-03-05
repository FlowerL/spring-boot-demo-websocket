package com.jxshen.spring.boot.demo.websocket.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jxshen on 2018/02/07
 */
public class WebsocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketHandler.class);
    private static final List<WebSocketSession> users = new ArrayList<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.println(message.getPayload());
        TextMessage msg = new TextMessage(message.getPayload());
        session.sendMessage(msg);
    }

    // Websocket连接建立
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        logger.info("成功建立Websocket连接");
        users.add(session);
        String username = session.getAttributes().get("user").toString();
        // 判断session中用户信息
        if(username != null){
            session.sendMessage(new TextMessage("已成功建立Websocket通信"));
        }
    }

    // 当连接出错时，主动关闭当前连接，并从会话列表中删除该会话
    @Override
    public void handleTransportError(WebSocketSession session, Throwable error) throws IOException {
        if(session.isOpen()){
            session.close();
        }
        logger.error("连接出现错误:"+error.toString());
        users.remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus arg1)
            throws Exception {
        logger.debug("Websocket连接已关闭");
        users.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给所有在线用户发送消息
     *
     * @param message
     */
    public void sendMessageToUsers(TextMessage message) {
        for (WebSocketSession user : users) {
            try {
                if (user.isOpen()) {
                    user.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 给某个用户发送消息
     *
     * @param userName
     * @param message
     */
    public void sendMessageToUser(String userName, TextMessage message) {
        for (WebSocketSession user : users) {
            if (user.getAttributes().get("user").equals(userName)) {
                try {
                    if (user.isOpen()) {
                        user.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
