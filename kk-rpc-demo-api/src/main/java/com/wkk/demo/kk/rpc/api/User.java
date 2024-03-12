package com.wkk.demo.kk.rpc.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户类
 * @author Wangkunkun
 * @date 2024/3/12 21:01
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * id
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

}
