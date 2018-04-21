package com.jxshen.spring.boot.demo.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author jxshen on 2018/04/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {

    private Long id;
    private Long pid;
    private Long vid;
    private String pidName;
    private String vidName;
    private Date createTime;
    private Date updateTime;
}
