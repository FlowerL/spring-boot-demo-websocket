package com.jxshen.spring.boot.demo.websocket.handler;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

/**
 * 客户端处理Stomp Session的handler
 * @author jxshen on 2018/02/23
 */
public class ClientStompSessionHandler extends StompSessionHandlerAdapter {

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        // The STOMP protocol also supports receipts where the client must add a "receipt" header to which the server responds with a RECEIPT frame after the send or subscribe are processed.
        // To support this the StompSession offers setAutoReceipt(boolean) that causes a "receipt" header to be added on every subsequent send or subscribe
        session.setAutoReceipt(true);
        // Once the session is established any payload can be sent and that will be serialized with the configured MessageConverter
        session.send("/app/hello", "payload");
        // The subscribe methods require a handler for messages on the subscription and return a Subscription handle that can be used to unsubscribe.
        // For each received message the handler can specify the target Object type the payload should be deserialized to:
        session.subscribe("topic/greetings", new ClientStompFrameHandler());
    }
}
