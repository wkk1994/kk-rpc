package com.wkk.learn.kk.rpc.code.provider;

import com.wkk.learn.kk.rpc.code.RpcRequest;
import com.wkk.learn.kk.rpc.code.RpcResponse;
import com.wkk.learn.kk.rpc.code.ex.RpcException;
import com.wkk.learn.kk.rpc.code.meta.MethodDesc;
import com.wkk.learn.kk.rpc.code.meta.ServiceDesc;
import com.wkk.learn.kk.rpc.code.meta.TypeUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 生产者执行器
 * @author Wangkunkun
 * @date 2024/3/24 21:05
 */
public class ProviderInvoke {

    private ProviderBootstrap providerBootstrap;

    public ProviderInvoke(ProviderBootstrap providerBootstrap) {
        this.providerBootstrap = providerBootstrap;
    }

    public RpcResponse invoke(RpcRequest request) {
        ServiceDesc serviceDesc = providerBootstrap.getSkeleton().get(request.getService());
        if(serviceDesc == null) {
            return RpcResponse.fail("Not Found Service");
        }
        Object invoke = null;
        try {
            MethodDesc methodDesc = serviceDesc.getMethods().get(request.getMethodSign());
            if(methodDesc == null) {
                return RpcResponse.fail("Not Found Method");
            }
            Object[] argTarget = null;
            if(request.getArgs() != null) {
                argTarget = new Object[request.getArgs().length];
                for (int i = 0; i < request.getArgs().length; i++) {
                    argTarget[i] = TypeUtil.cast(request.getArgs()[i], methodDesc.getMethod().getParameterTypes()[i]);
                }
            }
            invoke = methodDesc.getMethod().invoke(serviceDesc.getService(), argTarget);
            return RpcResponse.success(invoke);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if(targetException != null && targetException instanceof RpcException) {
                return RpcResponse.fail((RpcException) targetException);
            }
            return RpcResponse.fail(new RpcException(e.getTargetException(), RpcException.INVOKE_ERROR));
        } catch (Exception e) {
            return RpcResponse.fail(new RpcException(e, RpcException.INVOKE_ERROR));
        }
    }
}
