package com.jxshen.spring.boot.demo.websocket.exception;

/**
 * @author jxshen on 2018/02/26
 */
public class UnauthenticationException extends RuntimeException {

    public UnauthenticationException(String message) {
        super(message);
    }
}
