package com.jxshen.spring.boot.demo.websocket.dto;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * It is also possible to declare a Spring-managed bean in the websocket scope.
 * WebSocket-scoped beans can be injected into controllers and any channel interceptors registered on the "clientInboundChannel".
 * Those are typically singletons and live longer than any individual WebSocket session.
 * Therefore you will need to use a scope proxy mode for WebSocket-scoped beans
 *
 * @author jxshen on 2018/02/23
 */
@Component
@Scope(scopeName = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MyWebsocketScopeBean {

    @PostConstruct
    public void init() {
        // Invoked after dependencies injected
    }

    // ...

    @PreDestroy
    public void destroy() {
        // Invoked when the WebSocket session ends
    }
}
