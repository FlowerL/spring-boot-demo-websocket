package com.jxshen.spring.boot.demo.websocket.configuration;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用SpringBoot应用，boot会自动注册默认的一些converters，用户只需要定制或者提供自定义的converter就行，
 * 不用管默认converters的注册
 *
 * @author jxshen on 2018/01/30
 */
@Configuration
public class WebConfiguration {

//    @Bean
//    public HttpMessageConverters fastJsonHttpMessageConverters() {
//        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
//        FastJsonConfig fastJsonConfig = new FastJsonConfig();
////       fastJsonConfig.setSerializerFeatures(SerializerFeature.BrowserCompatible);
////       fastJsonConfig.setSerializerFeatures(SerializerFeature.BrowserSecure);
//        fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
//// 	   fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteMapNullValue);
//        SerializeConfig config = new SerializeConfig();
//        config.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
//        fastJsonConfig.setSerializeConfig(config);
//        fastConverter.setFastJsonConfig(fastJsonConfig);
//        StringHttpMessageConverter converter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
//        return new HttpMessageConverters(converter, fastConverter);
//    }

//    @Bean
//    public HttpMessageConverter<String> stringHttpMessageConverter() {
//        StringHttpMessageConverter converter = new StringHttpMessageConverter(
//                Charset.forName("UTF-8"));
//        return converter;
//    }

//    @Bean
    public HttpMessageConverter<String> stringHttpMessageConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(
                Charset.forName("UTF-8"));
        return converter;
    }

    /** fastjson */
//    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
//        fastJsonConfig.setSerializerFeatures(SerializerFeature.BrowserCompatible);
//        fastJsonConfig.setSerializerFeatures(SerializerFeature.BrowserSecure);
//        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteMapNullValue);
        fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
        fastJsonConfig.setSerializeConfig(serializeConfig);
        fastConverter.setFastJsonConfig(fastJsonConfig);
        return fastConverter;
    }

    /** jackson */
//    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);  // 值为null的字段不参与序列化
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectMapper.setDateFormat(sdf); // 设置自定义时间格式
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); //反序列化时字符串中有一个Field在POJO中不存在，不报错，默认跳过
        return objectMapper;
    }

    /** jackson为springboot默认用的converter，使用MappingJackson2HttpMessageConverter或者只提供自定义的ObjectMapper
     *  都可以制定jackson，并且不会改变jackson在HttpMessageConverters里面的顺序。jackson会在后面，最前面是ByteHttpMessageConverter
     *  和StringHttpMessageConverter。所以这样的配置下，ResponseBody直接返回String是不带引号的
     * */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);  // 值为null的字段不参与序列化
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectMapper.setDateFormat(sdf); // 设置自定义时间格式
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonConverter.setObjectMapper(objectMapper);
        return jsonConverter;
    }

}
