package com.jxshen.spring.boot.demo.websocket.handler;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * 客户端处理Stomp帧的处理器
 * @author jxshen on 2018/02/23
 */
public class ClientStompFrameHandler implements StompFrameHandler {
    @Override
    public Type getPayloadType(StompHeaders headers) {
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {

    }
}
