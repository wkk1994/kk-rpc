package com.wkk.learn.kk.rpc.code.ex;

import lombok.Data;

/**
 * rpc异常定义
 * @author Wangkunkun
 * @date 2024/3/27 20:29
 */
@Data
public class RpcException extends RuntimeException{

    private String errorCode;




}
