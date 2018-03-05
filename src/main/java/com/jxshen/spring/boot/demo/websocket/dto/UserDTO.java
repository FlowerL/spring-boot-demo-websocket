package com.jxshen.spring.boot.demo.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.security.Principal;

/**
 * 与WebSocket Session关联的用户，用于认证
 *
 * @author jxshen on 2018/03/01
 */
@ToString
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public class UserDTO implements Principal {

    private String name;

    @Override
    public String getName() {
        return name;
    }
}
