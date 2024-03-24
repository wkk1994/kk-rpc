package com.wkk.learn.kk.rpc.code.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 实例信息
 * @author Wangkunkun
 * @date 2024/3/24 21:57
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstanceMeta {

    private String schema;

    private String host;

    private Integer port;

    private String context;

    private Map<String, String> parameters;


    public String toPath() {
        return String.format("%s_%s", this.host, this.port);
    }

    public String toUrl() {
        return String.format("%s://%s:%s%s", this.schema, this.host, this.port, this.context);
    }

    public static InstanceMeta mapByHttp(String path) {
        String[] paths = path.split("_");
        return InstanceMeta.builder().schema("http").host(paths[0]).port(Integer.valueOf(paths[1])).context("").build();
    }
}
