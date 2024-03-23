package com.wkk.learn.kk.rpc.code.register;

import java.util.List;

/**
 * 注册中心
 * @author Wangkunkun
 * @date 2024/3/21 23:04
 */
public interface RegistryCenter {

    void start();

    void stop();

    void register(String service, String instance);

    void unregister(String service, String instance);

    List<String> fetchAll(String service);
    void subscribe(String service, ChangeListener changeListener);

    class StaticRegistryCenter implements RegistryCenter {

        List<String> providers;

        public StaticRegistryCenter(List<String> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, String instance) {

        }

        @Override
        public void unregister(String service, String instance) {

        }

        @Override
        public List<String> fetchAll(String service) {
            return this.providers;
        }

        @Override
        public void subscribe(String service, ChangeListener changeListener) {

        }
    }
}
