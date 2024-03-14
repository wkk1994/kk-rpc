package com.wkk.learn.kk.rpc.demo.provider;

import com.wkk.demo.kk.rpc.api.Order;
import com.wkk.demo.kk.rpc.api.OrderService;
import com.wkk.learn.kk.rpc.code.annotation.KkProvider;
import org.springframework.stereotype.Component;

/**
 * @author Wangkunkun
 * @date 2024/3/14 21:32
 */
@KkProvider
@Component
public class OrderServiceImpl implements OrderService {
    @Override
    public Order findById(Integer id) {
        Order order = new Order();
        order.setOrderId(id);
        order.setOrderName("name : " + id);
        return order;
    }
}
