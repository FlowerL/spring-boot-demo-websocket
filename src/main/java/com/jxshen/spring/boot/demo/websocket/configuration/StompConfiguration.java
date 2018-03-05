package com.jxshen.spring.boot.demo.websocket.configuration;

import com.jxshen.spring.boot.demo.websocket.handler.StompFrameErrorHandler;
import com.jxshen.spring.boot.demo.websocket.handler.StompHandler;
import com.jxshen.spring.boot.demo.websocket.interceptor.LeaveMutableInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.*;

/**
 * @author jxshen on 2018/02/08
 */
@Configuration
@EnableWebSocketMessageBroker
public class StompConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //定义了一个客户端订阅地址的前缀信息，也就是客户端接收服务端发送消息的前缀信息，SimpleBroker用的是本地内存缓存message，并且不支持ack和receipt等command
        //目前来看只有设置的第一个"topic"会生效
        registry.enableSimpleBroker("/topic", "/queue")
                .setTaskScheduler(messageBrokerSockJsTaskScheduler())   // 设置心跳执行任务线程池，同时设置默认心跳间隔为10s, 10s
                .setHeartbeatValue(new long[] {30000, 30000});   // 自定义心跳间隔设置，覆盖TaskScheduler的默认心跳设置
        // StompBrokerRelay用的外部broker比如RabbitMQ、ActiveMQ
//        registry.enableStompBrokerRelay("/topic", "/queue") // 设置可以订阅的地址，也就是服务器可以发送的地址
//                .setRelayHost(ConfigureUtil.getProperty("BrokerUrl")).setRelayPort(Integer.valueOf(ConfigureUtil.getProperty("BrokerPort"))) // 设置broker的地址及端口号
//                .setSystemHeartbeatReceiveInterval(2000) // 设置心跳信息接收时间间隔
//                .setSystemHeartbeatSendInterval(2000); // 设置心跳信息发送时间间隔
        //定义了服务端接收地址的前缀，也即客户端给服务端发消息的地址前缀，包括client端send和subscribe message的路径前缀
        registry.setApplicationDestinationPrefixes("/app", "/topic", "/user");
        //点对点使用的订阅前缀（客户端订阅路径上会体现出来），不设置的话，默认也是/user/
        registry.setUserDestinationPrefix("/user/");
        // 路径分隔符设定，前缀路径不受影响，仍旧为斜线
//        registry.setPathMatcher(new AntPathMatcher("."));
    }

    // 启用SockJS fallback选项是因为很多浏览器或网络代理不支持websocket，需要将websocket降级为http streaming或者长轮询
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 添加一个/gs-guide-websocket端点，客户端就可以通过这个端点来进行连接；withSockJS作用是添加SockJS支持
        // 注意client第一次连接时会发送http upgrade请求到{endpoint}/info，此处就是/gs-guide-websocket/info确保服务存在
        // 后续websocket连接时直接连endpoint的url
        registry.setErrorHandler(stompFrameErrorHandler())
                .addEndpoint("/websocket")
                .setHandshakeHandler(stompHandler())
//                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins("*")
                .withSockJS();
    }

    /*
     * While the workload for the "clientInboundChannel" is possible to predict 
     * — after all it is based on what the application does 
     * — how to configure the "clientOutboundChannel" is harder as it is based on factors beyond the control of the application.
     * For this reason there are two additional properties related to the sending of messages.
     * Those are the "sendTimeLimit" and the "sendBufferSizeLimit".
     * Those are used to configure how long a send is allowed to take and how much data can be buffered when sending messages to a client.
     *
     * The general idea is that at any given time only a single thread may be used to send to a client.
     * All additional messages meanwhile get buffered and you can use these properties to decide
     * how long sending a message is allowed to take and how much data can be buffered in the mean time
     *
     * The WebSocket transport configuration shown above can also be used to configure the maximum allowed size for incoming STOMP messages.
     * Although in theory a WebSocket message can be almost unlimited in size, in practice WebSocket servers impose limits 
     * — for example, 8K on Tomcat and 64K on Jetty. For this reason STOMP clients such as stomp.js split larger STOMP messages at 16K boundaries
     * and send them as multiple WebSocket messages thus requiring the server to buffer and re-assemble.
     *
     * Spring’s STOMP over WebSocket support does this so applications can configure the maximum size
     * for STOMP messages irrespective of WebSocket server specific message sizes.
     * Do keep in mind that the WebSocket message size will be automatically adjusted
     * if necessary to ensure they can carry 16K WebSocket messages at a minimum.
     *
     * 消息输出参数配置
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(8192 * 4) //设置接收消息字节数大小   the maximum allowed size for incoming STOMP messages
                .setSendBufferSizeLimit(8192 * 4)//设置发送消息缓存大小
                .setSendTimeLimit(40000); //设置发送消息时间限制毫秒
    }

    /*
     * The obvious place to start is to configure the thread pools backing the "clientInboundChannel" and the "clientOutboundChannel".
     * By default both are configured at twice the number of available processors.
     * If the handling of messages in annotated methods is mainly CPU bound then the number of threads for the "clientInboundChannel" should remain close to the number of processors.
     * If the work they do is more IO bound and requires blocking or waiting on a database or other external system then the thread pool size will need to be increased.
     * ThreadPoolExecutor has 3 important properties. Those are the core and the max thread pool size as well as the capacity for the queue to store tasks for which there are no available threads.
     * A common point of confusion is that configuring the core pool size (e.g. 10) and max pool size (e.g. 20) results in a thread pool with 10 to 20 threads.
     * In fact if the capacity is left at its default value of Integer.MAX_VALUE then the thread pool will never increase beyond the core pool size since all additional tasks will be queued.
     *
     * 输入通道参数设置，也即异步执行消息通道里面消息的线程池
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration
//                .interceptors(new ExceptionInterceptor())
                .taskExecutor()   // 处理输入消息的线程池配置
                .corePoolSize(8) //设置消息输入通道的核心线程池线程数
                .maxPoolSize(8)//最大线程数=核心线程数，默认的无界队列情况下，corePoolSize不会超过自己设置的上线，因为额外的任务会直接被入队
//                .queueCapacity(Integer.MAX_VALUE)  // 配置backup queue的大小，默认就是无界队列capacity=Integer.MAX_VALUE
                .keepAliveSeconds(60); //线程活动时间
    }

    /*
     * On the "clientOutboundChannel" side it is all about sending messages to WebSocket clients.
     * If clients are on a fast network then the number of threads should remain close to the number of available processors.
     * If they are slow or on low bandwidth they will take longer to consume messages and put a burden on the thread pool.
     * Therefore increasing the thread pool size will be necessary
     *
     * 输出通道参数设置。
     */
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration
                // 对server向client端正常发送的frame比如CONNECTED，RECEIPT等进行拦截处理
                // 不包括ERROR frame，这个由StompSubProtocolErrorHandler来处理
//                .interceptors()
                .taskExecutor()   // 处理输出消息的线程池配置
                .corePoolSize(8)
                .maxPoolSize(8);
    }

    // Connect时候进行用户认证。关联的configuration顺序要在spring security之前
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(authenticationInterceptor());
//    }

    @Bean
    public StompHandler stompHandler() {
        return new StompHandler();
    }

//    @Bean
//    public AuthenticationInterceptor authenticationInterceptor() {
//        return new AuthenticationInterceptor();
//    }

    // 执行心跳任务的线程池
    @Bean(name = "taskScheduler")
    public ThreadPoolTaskScheduler messageBrokerSockJsTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("SockJS-");
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        return scheduler;
    }

    @Bean
    public StompFrameErrorHandler stompFrameErrorHandler() {
        return new StompFrameErrorHandler();
    }

    @Bean
    public LeaveMutableInterceptor leaveMutableInterceptor() {
        return new LeaveMutableInterceptor();
    }

}
