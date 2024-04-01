package com.example.kk.rpc.demo.provider;

import com.wkk.demo.kk.rpc.api.User;
import com.wkk.demo.kk.rpc.api.UserService;
import com.wkk.learn.kk.rpc.code.annotation.KkProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Wangkunkun
 * @date 2024/3/12 21:11
 */
@KkProvider
@Component
public class UserServiceImpl implements UserService {

    @Autowired
    Environment environment;
    @Override
    public User findById(int id) {
        if(id == 404) {
            throw new RuntimeException("Not Found");
        }
        return new User(id, environment.getProperty("server.port") + "-KK-" + System.currentTimeMillis());
    }

    @Override
    public long findByLong() {
        return 100;
    }

    @Override
    public Long findByLongI() {
        return 1000L;
    }

    @Override
    public int findByInt() {
        return 10;
    }

    @Override
    public Integer findByInteger() {
        return Integer.valueOf(12);
    }

    @Override
    public User findById(long id) {
        return new User(Math.toIntExact(id), "KK-long-" + System.currentTimeMillis());
    }

    @Override
    public User findById(int id, String name) {
        return new User(id, name);
    }

    @Override
    public User findById(long id, String name) {
        return new User(Math.toIntExact(id), "long " + name);
    }

    @Override
    public List<Integer> findByListInt() {
        List<Integer> list = new LinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        return list;
    }

    @Override
    public int[] findByIntArr() {
        return new int[]{12,23,34};
    }

    @Override
    public Integer[] findByIntegerArr() {
        return new Integer[]{12,23,34};
    }

    @Override
    public long[] findByLongArr() {
        return new long[]{1,2,34,434};
    }

    @Override
    public Long[] findByLongIArr() {
        return new Long[]{1213L,2341L,23423L};
    }

    @Override
    public int[] findByLongIArr(int[] arr) {
        return arr;
    }

    @Override
    public List<User> findByUserList() {
        List<User> users = new ArrayList<>();
        users.add(findById(11));
        users.add(findById(22));
        return users;
    }

    @Override
    public List<User> findByUserList(List<User> users) {
        return users;
    }

    @Override
    public User setTimeOut(int timeOut) {
        return null;
    }
}
