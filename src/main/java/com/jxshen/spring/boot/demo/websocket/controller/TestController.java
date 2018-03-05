package com.jxshen.spring.boot.demo.websocket.controller;

import com.jxshen.spring.boot.demo.websocket.bo.MessageHeadersBO;
import com.jxshen.spring.boot.demo.websocket.dto.BaseResult;
import com.jxshen.spring.boot.demo.websocket.dto.GreetingDTO;
import com.jxshen.spring.boot.demo.websocket.dto.HelloMessageDTO;
import com.jxshen.spring.boot.demo.websocket.exception.MessageBroadcastException;
import com.jxshen.spring.boot.demo.websocket.exception.MessageSessionException;
import com.jxshen.spring.boot.demo.websocket.exception.MessageUserException;
import com.jxshen.spring.boot.demo.websocket.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * 测试客户端，生产环境要注释掉@RestController
 *
 * @author jxshen on 2018/02/27
 */
@RestController
public class TestController {

    @Autowired
    private SimpMessagingTemplate simpTemplate;

    @Autowired
    @Qualifier("clientOutboundChannel")
    private MessageChannel clientOutboundChannel;

    // @SendTo广播给所有用户的所有session
    @MessageMapping("/annotation/broadcast")
    @SendTo(value = "/topic/greetings")
    public GreetingDTO annotationBroadcast(HelloMessageDTO messageDTO, Principal user, StompHeaderAccessor accessor, MessageHeaders heraders) {
        return GreetingDTO.builder()
                .content("Hello, anntation broadcast: " + messageDTO.getName() + "!")
                .build();
    }

    // @SendToUser广播给单个用户的所有session
    @MessageMapping("/annotation/user")
    @SendToUser(value = "/topic/greetings")
    public GreetingDTO annotationUser(HelloMessageDTO messageDTO, Principal user, StompHeaderAccessor accessor, MessageHeaders heraders) {
        return GreetingDTO.builder()
                .content("Hello, anntation user: " + messageDTO.getName() + "!")
                .build();
    }

    // @SendToUser定向给单个用户的单个session，要设置broadcast为false
    @MessageMapping("/annotation/session")
    @SendToUser(value = "/topic/greetings", broadcast = false)
    public GreetingDTO annotationSession(HelloMessageDTO messageDTO, Principal user, StompHeaderAccessor accessor, MessageHeaders heraders) {
        return GreetingDTO.builder()
                .content("Hello, , anntation session: " + messageDTO.getName() + "!")
                .build();
    }

    // convertAndSend广播给所有用户的所有session
    @MessageMapping("/template/broadcast")
    public void templateBroadcast(HelloMessageDTO messageDTO, Principal user, StompHeaderAccessor accessor, MessageHeaders heraders) {
        simpTemplate.convertAndSend("/topic/greetings",
                GreetingDTO.builder()
                        .content("Hello, template broadcast: " + messageDTO.getName() + "!")
                        .build());
    }

    // convertAndSendToUser广播给单个用户的所有session, 设置user但不设置messageHeaders里的simpSessionId
    @MessageMapping("/template/user")
    public void templateUser(HelloMessageDTO messageDTO, Principal user, StompHeaderAccessor accessor, MessageHeaders heraders) {
        simpTemplate.convertAndSendToUser(user.getName(), "/topic/greetings",
                GreetingDTO.builder()
                        .content("Hello, template user: " + messageDTO.getName() + "!")
                        .build());
    }

    // convertAndSendToUser定向给单个用户的单个session，设置user，同时设置messageHeaders里的simpSessionId
    @MessageMapping("/template/session")
    public void templateSession(HelloMessageDTO messageDTO, Principal user, StompHeaderAccessor accessor, MessageHeaders heraders) {
        simpTemplate.convertAndSendToUser(accessor.getSessionId(), "/topic/greetings",
                GreetingDTO.builder()
                        .content("Hello, template session: " + messageDTO.getName() + "!")
                        .build(),
                accessor.getMessageHeaders());
    }

    // 抛异常广播给所有user的所有session
    @MessageMapping("/exception/broadcast")
    public void exceptionBroadcast(HelloMessageDTO messageDTO, Principal user, StompHeaderAccessor accessor, MessageHeaders heraders) {
        throw new MessageBroadcastException("simulate broadcast exception");
    }

    // 抛异常广播给单个user的所有session
    @MessageMapping("/exception/user")
    public void exceptionUser(HelloMessageDTO messageDTO, Principal user, StompHeaderAccessor accessor, MessageHeaders heraders) {
        throw new MessageUserException("simulate user exception");
    }

    // 抛异常广播给单个user的单个session
    @MessageMapping("/exception/session")
    public void exceptionSession(HelloMessageDTO messageDTO, Principal user, StompHeaderAccessor accessor, MessageHeaders heraders) {
        throw new MessageSessionException("simulate session exception");
    }

    // 拦截Controller里所有@MessageMapping和@SubscribeMapping的exception并发送给对应的订阅者，可以用@SendTo或者template
    @MessageExceptionHandler(RuntimeException.class)
    public void onError(RuntimeException e, Principal user, StompHeaderAccessor accessor) {
        if (e instanceof MessageBroadcastException) {
            simpTemplate.convertAndSend("/topic/greetings",
                    BaseResult.getFailResult(e.getMessage()));
        }
        if (e instanceof MessageUserException) {
            simpTemplate.convertAndSendToUser(user.getName(), "/topic/greetings",
                    BaseResult.getFailResult(e.getMessage()));
        }
        if (e instanceof MessageSessionException) {
            simpTemplate.convertAndSendToUser(user.getName(), "/topic/greetings",
                    BaseResult.getFailResult(e.getMessage()),
                    accessor.getMessageHeaders());
        }
//        return BaseResult.getFailResult(e.getMessage());
    }

    @SubscribeMapping("/topic/greetings")
    public void subscribeUser(Principal p, StompHeaderAccessor accessor) {
        System.out.println("subscribe frame with name: " + p.getName() + " sessionId: " + accessor.getSessionId());
    }

    @SubscribeMapping("/greetings")
    public void subscribeTopic(Principal p, StompHeaderAccessor accessor) {
        System.out.println("subscribe frame with name: " + p.getName() + " sessionId: " + accessor.getSessionId());
    }

    // 主动广播给所有的user的所有session
    @RequestMapping("/active/broadcast/{content}")
    public String activeBroadcast(@PathVariable("content")String content) {
        simpTemplate.convertAndSend("/topic/greetings",
                GreetingDTO.builder()
                        .content("Hello, active broadcast: " + content + "!")
                        .build());
        return "success";
    }

    /*
     * 在user authenticated情况下(frame有simpUser的headers或者accessor.setUser()已经设置过Principal实体)
     * convertAndSendToUser的第一个参数user即是simpUser的值或者accessor.getUser().getName()
     * 当然也可以用header为simpSessionId的值或accessor.getSessionId()作为第一个参数，表示只发给该user的特定session。
     * 如果是unanthennticated user, 那么只能用sessionId，发给某个特定session
     */
    // 主动广播给单个user的所有session
    @RequestMapping("/active/user/{user}")
    public String activeUser(@PathVariable("user")String user) {
        simpTemplate.convertAndSendToUser(user, "/topic/greetings",
                GreetingDTO.builder()
                        .content("Hello, active user: " + user + "!")
                        .build());
        return "success";
    }

    // 主动广播给单个user的单个session
    @RequestMapping("/active/session/{user}/{sessionId}")
    public String activeSession(@PathVariable("user")String user, @PathVariable("sessionId")String sessionId) {
        simpTemplate.convertAndSendToUser(user, "/topic/greetings",
                GreetingDTO.builder()
                        .content("Hello, active session: " + sessionId + "!")
                        .build(),
                MessageHeadersBO.createForMessageCommand(sessionId));
        return "success";
    }

    // 主动关闭单个user的单个session。ERROR frame只能定向到具体的session。
    @RequestMapping("/close/session/{sessionId}")
    public String closeSession(@PathVariable("sessionId")String sessionId) {
        clientOutboundChannel.send(MessageBuilder.createMessage(
                ObjectUtil.getJSONStringBytes(GreetingDTO.builder()
                        .content("Hello, close session: " + sessionId + "!")
                        .build()),
                MessageHeadersBO.createForErrorCommand(sessionId, "服务器关闭连接")),
                10000);
        return "success";
    }

}
