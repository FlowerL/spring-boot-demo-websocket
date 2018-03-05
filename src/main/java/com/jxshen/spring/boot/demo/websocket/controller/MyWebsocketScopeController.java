package com.jxshen.spring.boot.demo.websocket.controller;

import com.jxshen.spring.boot.demo.websocket.dto.MyWebsocketScopeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * As with any custom scope, Spring initializes a new MyBean instance the first time it is accessed from the controller
 * and stores the instance in the WebSocket session attributes.
 * The same instance is returned subsequently until the session ends.
 * WebSocket-scoped beans will have all Spring lifecycle methods invoked as shown in the examples above.
 *
 * @author jxshen on 2018/02/23
 */
@RestController
public class MyWebsocketScopeController {

    private final MyWebsocketScopeBean myWebsocketScopeBean;

    @Autowired
    public MyWebsocketScopeController(MyWebsocketScopeBean myWebsocketScopeBean) {
        this.myWebsocketScopeBean = myWebsocketScopeBean;
    }

    @MessageMapping("/scope/test")
    public void handle() {
        // this.myWebsocketScopeBean from the current WebSocket session
    }
}
