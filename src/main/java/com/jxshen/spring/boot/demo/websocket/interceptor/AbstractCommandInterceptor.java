package com.jxshen.spring.boot.demo.websocket.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;

/**
 * 请求拦截器基类
 *
 * @author jxshen on 2018/03/01
 */
public abstract class AbstractCommandInterceptor extends ChannelInterceptorAdapter {

    @Override
    public Message<?> preSend(final Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (needProcess(accessor, message, channel)) {
            process(accessor, message, channel);
        }
        return message;
    }

    protected abstract boolean needProcess(final StompHeaderAccessor accessor, final Message<?> message, MessageChannel channel);

    protected abstract void process(final StompHeaderAccessor accessor, final Message<?> message, MessageChannel channel);
}
