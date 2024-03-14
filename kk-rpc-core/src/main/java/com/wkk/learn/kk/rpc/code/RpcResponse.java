package com.wkk.learn.kk.rpc.code;

import lombok.*;

/**
 * @author Wangkunkun
 * @date 2024/3/12 21:16
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 成功标示
     */
    private boolean success;


    private Exception exception;

    /**
     * 返回数据
     */
    private T data;

    public static RpcResponse success(Object t) {
        return RpcResponse.builder().code(0).success(true).data(t).build();
    }

    public static RpcResponse fail(String errorMsg) {
        return RpcResponse.builder().code(-1).success(false).data(errorMsg).build();
    }

    public static RpcResponse fail(Exception exception) {
        return RpcResponse.builder().code(-1).success(false).data(exception.getMessage()).exception(exception).build();
    }
}
