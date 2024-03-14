package com.wkk.learn.kk.rpc.demo.provider;

import com.wkk.demo.kk.rpc.api.User;
import com.wkk.demo.kk.rpc.api.UserService;
import com.wkk.learn.kk.rpc.code.annotation.KkProvider;
import org.springframework.stereotype.Component;

/**
 * @author Wangkunkun
 * @date 2024/3/12 21:11
 */
@KkProvider
@Component
public class UserServiceImpl implements UserService {

    @Override
    public User findById(Integer id) {
        if(id == 404) {
            throw new RuntimeException("Not Found");
        }
        return new User(id, "KK-" + System.currentTimeMillis());
    }
}
