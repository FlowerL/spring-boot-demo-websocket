package com.jxshen.spring.boot.demo.websocket.interceptor;

import com.jxshen.spring.boot.demo.websocket.bo.MessageHeadersBO;
import com.jxshen.spring.boot.demo.websocket.dto.GreetingDTO;
import com.jxshen.spring.boot.demo.websocket.dto.UserDTO;
import com.jxshen.spring.boot.demo.websocket.dto.UserSessionDTO;
import com.jxshen.spring.boot.demo.websocket.util.ObjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

/**
 * 认证拦截器<br/>
 * 如果是connect请求，则走登陆链路，并向route注册
 *
 * @author jxshen on 2018/02/16
 */
@Component
public class AuthenticationInterceptor extends AbstractCommandInterceptor {

    @Autowired
    @Qualifier("clientOutboundChannel")
    private MessageChannel clientOutboundChannel;

    @Override
    protected boolean needProcess(StompHeaderAccessor accessor, Message<?> message, MessageChannel channel) {
        return StompCommand.CONNECT.equals(accessor.getCommand());
    }

    @Override
    protected void process(StompHeaderAccessor accessor, Message<?> message, MessageChannel channel) {
        // todo 如果是unathenicated user（在handshake阶段没识别用户），则抛异常关闭连接。现在暂时用userId代替，后续要到数据库验证
        UserDTO userDTO = null;
        byte[] errorPayload = null;
        if (accessor.getUser() == null || StringUtils.isBlank(accessor.getUser().getName())) {
            String username = (String) message.getHeaders().get(StompHeaderAccessor.NATIVE_HEADERS, LinkedMultiValueMap.class).get("client-id").get(0);
            userDTO = UserDTO.builder().name(username).build();
            accessor.setUser(userDTO);
        }
        // 模拟认证错误的客户，以后要去掉
        if ("error".equals(accessor.getPasscode())) {
            errorPayload = ObjectUtil.getJSONStringBytes(GreetingDTO.builder()
                    .content("authenticate error: " + accessor.getSessionId() + "!")
                    .build());
            clientOutboundChannel.send(MessageBuilder.createMessage(errorPayload,
                    MessageHeadersBO.createForErrorCommand(accessor.getSessionId(), "服务器关闭连接")),
                    10000);
        }
        // 向WebSocket路由服务注册
        UserSessionDTO userSessionDTO = UserSessionDTO.builder()
                .user(userDTO)
                .sessionId(accessor.getSessionId())
                .build();
        boolean registerSuccess = true;
        if (!registerSuccess) {
            errorPayload = ObjectUtil.getJSONStringBytes(GreetingDTO.builder()
                    .content("register error: " + accessor.getSessionId() + "!")
                    .build());
            clientOutboundChannel.send(MessageBuilder.createMessage(errorPayload,
                    MessageHeadersBO.createForErrorCommand(accessor.getSessionId(), "注册到路由错误")),
                    10000);
        }
    }
}
