package com.jxshen.spring.boot.demo.websocket.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.RequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

/**
 * @author jxshen on 2018/02/13
 */
public class StompHandler extends DefaultHandshakeHandler {

    public StompHandler() {
    }

    public StompHandler(RequestUpgradeStrategy requestUpgradeStrategy) {
        super(requestUpgradeStrategy);
    }

    // 在handshake阶段给stomp messagge设置simpUser的头部，即关联user到websocket session
    // 默认实现直接返回ServerHttpRequest的principal
    // 如果结合了spring security，那么到determineUser时，principal已经时认证的用户
    // 否则principal是null。如果到connect阶段user还没有被设置，那么client的后续frame都不会生效
    // 所以如果connect frame中不进行用户认证的话，这里用request的sessionId代替user
    // 也即这种方式user和sessionId是一对一的，一个用户只有一个session.
    // 比较好的做法是handshakehandler保持默认，connect frame中进行认证（设置user）
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        URI uri = request.getURI();
        String id = null;
        if (uri != null) {
            String query = uri.getPath();
            String[] pairs = query.split("/");
            id = pairs[3];
        }
        STOMPPrincipal principal = STOMPPrincipal.builder()
                .name(id)
                .build();
        System.out.println(id);
        return principal;

        //        throw new UnauthenticationException("用户未认证, 握手失败");
//        URI uri = request.getURI();
//        String id = null;
//        if (uri != null) {
//            String query = uri.getPath();
//            String[] pairs = query.split("/");
//            id = pairs[3];
//        }
//        final String name = id;
//        Principal principal = new Principal() {
//            @Override
//            public String getName() {
//                return name;
//            }
//        };
//        System.out.println(id);
//        return principal;
////        return request.getPrincipal();

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class STOMPPrincipal implements Principal {

        private String name;
    }
}
