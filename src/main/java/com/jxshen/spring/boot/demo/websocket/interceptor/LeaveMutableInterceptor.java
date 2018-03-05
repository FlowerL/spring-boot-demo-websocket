package com.jxshen.spring.boot.demo.websocket.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

/**
 * 如果是disconnect请求，则走登出链路，并向route解注
 *
 * @author jxshen on 2018/03/01
 */
public class LeaveMutableInterceptor extends AbstractCommandInterceptor {

    @Override
    protected boolean needProcess(StompHeaderAccessor accessor, Message<?> message, MessageChannel channel) {
        // DISCONNECT frame不能再被修改
        return !StompCommand.DISCONNECT.equals(accessor.getCommand());
    }

    @Override
    protected void process(StompHeaderAccessor accessor, Message<?> message, MessageChannel channel) {
        // not documented anywhere but necessary otherwise NPE in StompSubProtocolHandler!
        accessor.setLeaveMutable(true);
    }
}
