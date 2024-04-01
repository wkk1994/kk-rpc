package com.wkk.demo.kk.rpc.api;

import lombok.SneakyThrows;

import java.util.List;

/**
 * 用户接口
 * @author Wangkunkun
 * @date 2024/3/12 21:01
 */
public interface UserService {

    User findById(int id);
    long findByLong();
    Long findByLongI();
    int findByInt();
    Integer findByInteger();
    User findById(long id);
    User findById(int id, String name);

    User findById(long id, String name);

    List<Integer> findByListInt();
    int[] findByIntArr();
    Integer[] findByIntegerArr();
    long[] findByLongArr();
    Long[] findByLongIArr();

    int[] findByLongIArr(int[] arr);
    List<User> findByUserList();
    List<User> findByUserList(List<User> users);

    @SneakyThrows
    User setTimeOut(int timeOut);
}
