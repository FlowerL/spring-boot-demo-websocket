package com.jxshen.spring.boot.demo.websocket.bo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.security.Principal;

/**
 * @author jxshen on 2018/03/01
 */
public class MessageHeadersBO {

    public static MessageHeaders createMessageHeaders(
            StompCommand command, String sessionId, Principal user,
            String messageId, String message, Boolean leaveMutable) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(command);

        headerAccessor.setSessionId(StringUtils.isBlank(sessionId) ? null : sessionId);
        headerAccessor.setUser(user);
        headerAccessor.setMessageId(StringUtils.isBlank(messageId) ? null : messageId);
        headerAccessor.setMessage(StringUtils.isBlank(message) ? null : message);
        headerAccessor.setLeaveMutable(leaveMutable);

        return headerAccessor.getMessageHeaders();
    }

    public static MessageHeaders createForMessageCommand(String sessionId) {
        return createMessageHeaders(StompCommand.MESSAGE, sessionId, null, null, null, true);
    }

    public static MessageHeaders createForErrorCommand(String sessionId) {
        return createMessageHeaders(StompCommand.ERROR, sessionId, null, null, null, false);
    }

    public static MessageHeaders createForErrorCommand(String sessionId, String errMsg) {
        return createMessageHeaders(StompCommand.ERROR, sessionId, null, null, errMsg, false);
    }
}
