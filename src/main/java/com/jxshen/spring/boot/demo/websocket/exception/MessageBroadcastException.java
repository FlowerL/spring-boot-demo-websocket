package com.jxshen.spring.boot.demo.websocket.exception;

/**
 * @author jxshen on 2018/02/26
 */
public class MessageBroadcastException extends RuntimeException {

    public MessageBroadcastException(String message) {
        super(message);
    }
}
