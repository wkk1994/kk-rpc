package com.example.kk.rpc.demo.zk;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.test.InstanceSpec;
import org.apache.curator.test.TestingCluster;

/**
 * 启动一个zookeeper
 * @author Wangkunkun
 * @date 2024/3/27 20:20
 */
@Slf4j
public class ZookeeperServer {

    TestingCluster cluster;

    @SneakyThrows
    public void start() {
        InstanceSpec instanceSpec = new InstanceSpec(null, 2182,
                -1, -1, true,
                -1, -1, -1);
        cluster = new TestingCluster(instanceSpec);
        log.info("ZookeeperServer starting...");
        cluster.start();
        cluster.getServers().forEach(s -> System.out.println(s.getInstanceSpec()));
        log.info("ZookeeperServer started...");
    }

    @SneakyThrows
    public void stop() {
        if(cluster != null) {
            cluster.stop();
        }
        log.info("ZookeeperServer stop.");
    }
}
