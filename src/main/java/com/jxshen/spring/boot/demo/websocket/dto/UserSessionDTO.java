package com.jxshen.spring.boot.demo.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 包含用户和sessionId的实体
 *
 * @author jxshen on 2018/03/02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionDTO {

    private UserDTO user;
    private String sessionId;
}
