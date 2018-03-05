package com.jxshen.spring.boot.demo.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jxshen on 2018/01/10
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseResult<T> {

    public static final int CODE_SUCCESS = 200;
    public static final int CODE_FAIL = 400;
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_INTERNAL_SERVER_ERROR = 500;
    private int code;
    private String error;
    private T data;

    public static <E> BaseResultBuilder<E> builder() {
        return new BaseResultBuilder<>();
    }

    public static class BaseResultBuilder<P> {

        private int code;
        private String error;
        private P data;

        public BaseResultBuilder<P> code(int code) {
            this.code = code;
            return this;
        }

        public BaseResultBuilder<P> error(String error) {
            this.error = error;
            return this;
        }

        public BaseResultBuilder<P> data(P data) {
            this.data = data;
            return this;
        }

        public BaseResult<P> build() {
            return new BaseResult<>(code, error, data);
        }

    }

    public static <E> BaseResult<E> getResult(E data) {
        BaseResultBuilder<E> builder = BaseResult.builder();
        return builder.code(data != null ? CODE_SUCCESS : CODE_NOT_FOUND)
                .data(data)
                .build();
    }

    public static <E> BaseResult<E> getFailResult(String error) {
        BaseResultBuilder<E> builder = BaseResult.builder();
        return builder.code(CODE_FAIL)
                .error(error)
                .build();
    }

    public static <E> BaseResult<E> getFailResult(String error, int code) {
        BaseResultBuilder<E> builder = BaseResult.builder();
        return builder.code(code)
                .error(error)
                .build();
    }

}
