package com.wkk.learn.kk.rpc.code;

import com.wkk.learn.kk.rpc.code.ex.RpcException;
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


    private RpcException exception;

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

    public static RpcResponse fail(RpcException exception) {
        return RpcResponse.builder().code(-1).success(false).data(exception.getMessage()).exception(exception).build();
    }
}
