package com.wkk.learn.kk.rpc.code.register.zk;

import com.wkk.learn.kk.rpc.code.meta.ServiceMeta;
import com.wkk.learn.kk.rpc.code.register.ChangeEvent;
import com.wkk.learn.kk.rpc.code.register.ChangeListener;
import com.wkk.learn.kk.rpc.code.meta.InstanceMeta;
import com.wkk.learn.kk.rpc.code.register.RegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * zk注册中心实现
 * @author Wangkunkun
 * @date 2024/3/23 20:12
 */
@Slf4j
public class ZkRegistryCenter implements RegistryCenter {

    private CuratorFramework client = null;

    @Override
    public void start() {
        // 定义重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .namespace("kk-rpc")
                .retryPolicy(retryPolicy).build();
        client.start();
        log.info("zookeeper registry center start...");

    }

    @Override
    public void stop() {
        client.close();
        log.info("zookeeper registry center stop...");
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service.toPath();
        try {
            // 创建服务的持久化节点
            if (client.checkExists().forPath(servicePath) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            // 创建实例的临时节点
            String instancePath = servicePath + "/" + instance.toPath();
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "instance".getBytes());
            log.info("zookeeper registry instancePath: {}", instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service.toPath();
        try {
            // 服务节点不存在，直接返回
            if (client.checkExists().forPath(servicePath) == null) {
                return;
            }
            // 删除实例的临时节点
            String instancePath = servicePath + "/" + instance.toPath();
            client.delete().forPath(instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        String servicePath = "/" + service.toPath();
        try {
            log.error("fetchAll servicePath: {}", servicePath);
            List<String> nodes = this.client.getChildren().forPath(servicePath);
            log.info("zookeeper registry fetch node : {}", nodes);
            if(nodes == null) {
                return null;
            }
            return nodes.stream().map(node -> InstanceMeta.mapByHttp(node)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("fetchAll error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(ServiceMeta service, ChangeListener changeListener) {
        log.info("zookeeper subscribe...");
        // zk树结构监听，最大深度设置为2
        TreeCache cache = TreeCache.newBuilder(client, "/" + service.toPath())
                .setCacheData(true).setMaxDepth(2).build();
        cache.getListenable().addListener((curatorFramework, event) -> {
            log.info("zookeeper node change: {}", event);
            List<InstanceMeta> nodes = fetchAll(service);
            changeListener.fire(new ChangeEvent(nodes));
        });
        try {
            cache.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
