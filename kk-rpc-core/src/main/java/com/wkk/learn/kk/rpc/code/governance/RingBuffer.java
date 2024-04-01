package com.wkk.learn.kk.rpc.code.governance;

import java.util.Arrays;

/**
 * 一个环实现，用于实现滑动窗口，并发不安全
 * @author Wangkunkun
 * @date 2024/4/1 22:14
 */
public class RingBuffer {

    private int[] ring;

    public RingBuffer(int size) {
        this.ring = new int[size];
    }

    /**
     * 重置环数据
     */
    public void rest() {
        Arrays.fill(this.ring, 0);
    }

    /**
     * 环指定位置数据增长为num
     * @param index
     * @param num
     */
    public void incr(long index, int num) {
        this.ring[Math.toIntExact(index % this.ring.length)] += num;
    }

    /**
     * 从指定位置到指定位置数据重置
     * @param beginIndex
     * @param endIndex
     */
    public void rest(long beginIndex, long endIndex) {
        for (long i = beginIndex; i < endIndex; i++) {
            this.ring[Math.toIntExact(beginIndex % this.ring.length)] = 0;
        }
    }

    /**
     * 获取环总计
     * @return
     */
    public int sum() {
        return Arrays.stream(this.ring).sum();
    }

    public int getSize() {
        return this.ring.length;
    }
}
