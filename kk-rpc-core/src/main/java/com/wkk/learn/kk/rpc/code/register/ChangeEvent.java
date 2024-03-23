package com.wkk.learn.kk.rpc.code.register;

import lombok.Data;

import java.util.List;

/**
 * 变更事件
 * @author Wangkunkun
 * @date 2024/3/23 22:01
 */
@Data
public class ChangeEvent {

    List<String> nodes;

    public ChangeEvent(List<String> nodes) {
        this.nodes = nodes;
    }
}
