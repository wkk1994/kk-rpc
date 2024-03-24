package com.wkk.learn.kk.rpc.code.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务信息
 * @author Wangkunkun
 * @date 2024/3/24 22:14
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMeta {

    private String app;

    private String namespace;

    private String env;

    private String name;

    public String toPath() {
        return String.format("%s_%s_%s_%s", this.app, this.namespace, this.env, this.name);
    }

}
