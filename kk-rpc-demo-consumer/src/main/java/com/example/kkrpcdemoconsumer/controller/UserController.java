package com.example.kkrpcdemoconsumer.controller;

import com.wkk.demo.kk.rpc.api.User;
import com.wkk.demo.kk.rpc.api.UserService;
import com.wkk.learn.kk.rpc.code.annotation.KkConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * user controller
 * @author Wangkunkun
 * @date 2024/3/21 22:13
 */
@RestController
public class UserController {

    @KkConsumer
    private UserService userService;

    @GetMapping("/")
    public User getById(@RequestParam Integer id) {
        return userService.findById(id);
    }
}
