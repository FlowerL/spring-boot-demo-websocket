package com.jxshen.spring.boot.demo.websocket.util;

import com.alibaba.fastjson.JSON;
import com.jxshen.spring.boot.demo.websocket.constant.EncodeConstant;

import java.nio.charset.Charset;

/**
 * @author jxshen on 2018/03/01
 */
public class ObjectUtil {

    public static byte[] getJSONStringBytes(Object obj) {
        return JSON.toJSONString(obj).getBytes(Charset.forName(EncodeConstant.UTF_8));
    }
}
