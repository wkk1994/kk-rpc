package com.wkk.learn.kk.rpc.code.ex;

import lombok.Data;

/**
 * rpc异常定义
 * @author Wangkunkun
 * @date 2024/3/27 20:29
 */
@Data
public class RpcException extends RuntimeException{

    /**
     * 业务异常
     */
    public static final Integer BIZ_ERROR = 1;
    public static final Integer INVOKE_ERROR = 2;
    public static final Integer TYPE_CAST_ERROR = 3;

    private int code;

    public RpcException(int code) {
        this.code = code;
    }

    public RpcException(String message, int code) {
        super(message);
        this.code = code;
    }

    public RpcException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public RpcException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }

    public RpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }
}
