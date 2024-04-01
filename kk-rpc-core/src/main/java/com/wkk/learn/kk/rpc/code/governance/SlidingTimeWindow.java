package com.wkk.learn.kk.rpc.code.governance;

/**
 * 滑动窗口实现
 * @author Wangkunkun
 * @date 2024/4/1 22:13
 */
public class SlidingTimeWindow {

    private long currentTime = -1;

    private RingBuffer ringBuffer;

    public SlidingTimeWindow(int size) {
        ringBuffer = new RingBuffer(size);
    }

    /**
     * 记录一次
     * @param millis 毫秒
     */
    public synchronized void record(long millis) {
        long timeSecond = millis / 1000;
        long diffSecond = timeSecond - this.currentTime;
        if(this.currentTime == -1) {
            // 第一次调用，初始化数据
            this.currentTime = timeSecond;
            this.ringBuffer.incr(timeSecond, 1);
        }else if(diffSecond < this.ringBuffer.getSize() ){
            this.ringBuffer.rest(this.currentTime, timeSecond);
            this.ringBuffer.incr(timeSecond, 1);
        }else {
            // 差距超过环，重置环
            this.initRing(timeSecond);
            this.ringBuffer.incr(timeSecond, 1);
        }
    }

    private void initRing(long timeSecond) {
        this.ringBuffer.rest();
        this.currentTime = timeSecond;
    }


    public int getSum() {
        return this.ringBuffer.sum();
    }
}
