package com.jxshen.spring.boot.demo.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jxshen on 2018/02/08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HelloMessageDTO {
    private String name;
}
