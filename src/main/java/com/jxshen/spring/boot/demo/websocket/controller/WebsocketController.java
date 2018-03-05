package com.jxshen.spring.boot.demo.websocket.controller;

import com.jxshen.spring.boot.demo.websocket.handler.WebsocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.socket.TextMessage;

import javax.servlet.http.HttpSession;

/**
 * WebSocket: SockJS方案
 * @author jxshen on 2018/02/08
 */
@Controller
public class WebsocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketController.class);

    @Autowired
    private WebsocketHandler websocketHandler;

    // 服务端Spring MVC拦截该HTTP请求，将HTTP Session载入Websocket Session中，建立会话
    @RequestMapping(value="/login")
    public String login(HttpSession session){
        logger.info("用户登录建立Websocket连接");
        session.setAttribute("user", "raycloud");
        return "home";
    }

    // 模拟服务端发送消息，其中可实现消息的广发或指定对象发送
    @RequestMapping(value = "/chat", method = RequestMethod.GET)
    public String sendMessage(){
        double rand = Math.ceil(Math.random()*100);
        websocketHandler.sendMessageToUser("raycloud", new TextMessage("Websocket测试消息" + rand));
        return "message";
    }
}
