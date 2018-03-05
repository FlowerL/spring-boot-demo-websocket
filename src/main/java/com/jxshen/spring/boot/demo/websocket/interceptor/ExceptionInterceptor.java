package com.jxshen.spring.boot.demo.websocket.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

/**
 * @author jxshen on 2018/02/17
 */
public class ExceptionInterceptor extends ChannelInterceptorAdapter {

    @Override
    public Message<?> preSend(Message message, MessageChannel channel) {
        throw new RuntimeException(ExceptionInterceptor.class.getSimpleName() + "error");
    }

}
