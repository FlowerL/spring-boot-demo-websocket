package com.jxshen.spring.boot.demo.websocket.configuration;

import com.jxshen.spring.boot.demo.websocket.handler.WebsocketHandler;
import com.jxshen.spring.boot.demo.websocket.interceptor.WebSocketInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * WebSocket: SockJS方案
 * @author jxshen on 2018/02/07
 */
@Configuration
@EnableWebSocket
public class WebsocketConfiguration implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        System.out.println("register websocket handler");
        webSocketHandlerRegistry.addHandler(myHandler(), "/websocket-origin")
                .addInterceptors(webSocketInterceptor()).withSockJS();
    }

    // tomcat容器下控制websocket消息缓存大小，空闲超时时间，session超时时间等
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192 * 4);
        container.setMaxBinaryMessageBufferSize(8192 * 4);
//        container.setMaxSessionIdleTimeout(60000);
        return container;
    }

    // jetty配置websocket消息缓存大小，空闲超时时间的做法
//    @Bean
//    public DefaultHandshakeHandler handshakeHandler() {
//
//        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
//        policy.setInputBufferSize(8192);
//        policy.setIdleTimeout(600000);
//
//        return new DefaultHandshakeHandler(
//                new JettyRequestUpgradeStrategy(new WebSocketServerFactory(policy)));
//    }

    @Bean
    public WebsocketHandler myHandler() {
        return new WebsocketHandler();
    }

    @Bean
    public WebSocketInterceptor webSocketInterceptor() {
        return new WebSocketInterceptor();
    }
}
