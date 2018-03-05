package com.jxshen.spring.boot.demo.websocket.client;

import com.jxshen.spring.boot.demo.websocket.handler.ClientStompSessionHandler;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

/**
 * STOMP over WebSocket client and a STOMP over TCP client
 * @author jxshen on 2018/02/23
 */
public class DefaultStompClient {

    public static void main(String[] args) {
        // 1.Begin create and cofigure WebSocketStompClient==========================
        // StandardWebSocketClient和SockJsClient二选一
        WebSocketClient webSocketClient = new StandardWebSocketClient();
//        WebSocketClient webSocketClient = new SockJsClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new StringMessageConverter());
        // 客户端执行心跳任务的线程池
        // When using WebSocketStompClient for performance tests to simulate thousands of clients from the same machine consider turning off heartbeats
        // since each connection schedules its own heartbeat tasks and that’s not optimized for a a large number of clients running on the same machine.
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("ClientHeartBeat-");
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        stompClient.setTaskScheduler(scheduler);
        stompClient.setDefaultHeartbeat(new long[] {10000, 10000}); // 设置默认心跳间隔

        // 2.Establish a connection and provide a handler for the STOMP session=========
        String url = "ws://127.0.0.1:3001//gs-guide-websocket";
        StompSessionHandler sessionHandler = new ClientStompSessionHandler();
        stompClient.connect(url, sessionHandler);



    }
}
