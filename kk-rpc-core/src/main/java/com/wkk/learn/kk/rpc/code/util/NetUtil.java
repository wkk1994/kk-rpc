package com.wkk.learn.kk.rpc.code.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author Wangkunkun
 * @date 2024/3/27 23:55
 */
@Slf4j
public class NetUtil {

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    public static void main(String[] args) {
        System.out.println(getLocalHost());
    }

    /**
     * 获取本机地址
     * @return ipv4
     */
    public static String getLocalHost() {
        List<NetworkInterface> networkInterfaces = getValidNetworkInterfaces();
        Optional<InetAddress> inetAddressOps = getLocalAddress0(networkInterfaces);
        if(inetAddressOps.isEmpty()) {
            log.error("get local host empty");
            throw new RuntimeException("get local host empty");
        }
        return inetAddressOps.get().getHostAddress();
    }

    /**
     * 获取第一个有效的ip地址
     * @param networkInterfaces
     * @return
     */
    private static Optional<InetAddress> getLocalAddress0(List<NetworkInterface> networkInterfaces) {
        InetAddress result = null;
        // TODO 为啥要这样判断？
        for (NetworkInterface networkInterface : networkInterfaces) {
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if(isValidInetAddress(inetAddress)) {
                    result = inetAddress;
                    break;
                }
            }
        }
        return Optional.of(result);
    }

    /**
     * 判断地址有效性
     * @param inetAddress
     * @return
     */
    @SneakyThrows
    private static boolean isValidInetAddress(InetAddress inetAddress) {
        if(inetAddress instanceof Inet6Address) {
            // 不支持ipv6
            return false;
        }
        if (inetAddress.isLoopbackAddress()
                || "0.0.0.0".equals(inetAddress.getHostAddress()) || "127.0.0.1".equals(inetAddress.getHostAddress())) {
            return false;
        }
        return IP_PATTERN.matcher(inetAddress.getHostAddress()).matches() && inetAddress.isReachable(100);
    }

    /**
     * 获取有效的网络接口
     *
     * @return
     */
    @SneakyThrows
    private static List<NetworkInterface> getValidNetworkInterfaces() {
        List<NetworkInterface> networkInterfaces = new LinkedList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if(networkInterface.isLoopback() || networkInterface.isVirtual()
                    || !networkInterface.isUp()) {
                continue;
            }
            networkInterfaces.add(networkInterface);
        }
        return networkInterfaces;

    }
}
