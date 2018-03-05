package com.jxshen.spring.boot.demo.websocket.handler;

import com.jxshen.spring.boot.demo.websocket.dto.BaseResult;
import com.jxshen.spring.boot.demo.websocket.util.ObjectUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

/**
 * messageChannel抛出exception后向client发送ERROR frame的处理机制
 * server直接调用clientOutboundChannel.send()发送的ERROR frame也会经过这个handler。
 * 相当于定向处理ERROR，无论同步异步
 *
 * @author jxshen on 2018/02/26
 */
public class StompFrameErrorHandler extends StompSubProtocolErrorHandler {

    protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor,
                                             byte[] errorPayload, Throwable cause, StompHeaderAccessor clientHeaderAccessor) {

        // cause为null一般是server调用clientOutboundChannel.send主动向client发送ERROR frame来关闭连接
        if (cause != null) {
            Throwable rootCause = getRootCause(cause);
            if (errorPayload.length < 1) {
                errorPayload = ObjectUtil.getJSONStringBytes(BaseResult.getFailResult(rootCause.getMessage()));
            }
        }
        return MessageBuilder.createMessage(errorPayload, errorHeaderAccessor.getMessageHeaders());
    }

    private static Throwable getRootCause(Throwable cause) {
        Throwable rootCause = cause;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

}
