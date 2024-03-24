package com.wkk.learn.kk.rpc.code.register;

import com.wkk.learn.kk.rpc.code.meta.InstanceMeta;
import com.wkk.learn.kk.rpc.code.meta.ServiceMeta;

import java.util.List;

/**
 * 注册中心
 * @author Wangkunkun
 * @date 2024/3/21 23:04
 */
public interface RegistryCenter {

    void start();

    void stop();

    void register(ServiceMeta service, InstanceMeta instance);

    void unregister(ServiceMeta service, InstanceMeta instance);

    List<InstanceMeta> fetchAll(ServiceMeta service);
    void subscribe(ServiceMeta service, ChangeListener changeListener);

    class StaticRegistryCenter implements RegistryCenter {

        List<InstanceMeta> providers;

        public StaticRegistryCenter(List<InstanceMeta> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public void unregister(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(ServiceMeta service) {
            return this.providers;
        }

        @Override
        public void subscribe(ServiceMeta service, ChangeListener changeListener) {

        }
    }
}
