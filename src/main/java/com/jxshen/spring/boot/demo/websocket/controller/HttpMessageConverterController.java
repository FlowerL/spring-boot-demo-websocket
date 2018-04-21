package com.jxshen.spring.boot.demo.websocket.controller;

import com.jxshen.spring.boot.demo.websocket.dto.BaseResult;
import com.jxshen.spring.boot.demo.websocket.dto.ItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 测试不同的HttpMessageConverter对json序列化的效果
 *
 * @author jxshen on 2018/04/21
 */
@RestController
public class HttpMessageConverterController {

    @Autowired
    private HttpMessageConverters httpMessageConverters;

    @RequestMapping("/single/string/get")
    public String getSingleString() {
        return "singleString";
    }

    @RequestMapping("item/get")
    public BaseResult<ItemDTO> getItem() {
        ItemDTO itemDTO = ItemDTO.builder()
                .id(1L)
                .pid(1L)
                .pidName("pidName1")
                .createTime(new Date())
                .build();
        return BaseResult.getResult(itemDTO);
    }
}
