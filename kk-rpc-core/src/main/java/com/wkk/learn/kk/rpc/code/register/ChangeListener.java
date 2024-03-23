package com.wkk.learn.kk.rpc.code.register;

/**
 * 节点变更通知
 * @author Wangkunkun
 * @date 2024/3/23 22:00
 */
public interface ChangeListener {
    void fire(ChangeEvent changeEvent);
}
