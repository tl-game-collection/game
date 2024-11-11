package com.xiuxiu.core.net.filter;

/**
 * 攻击记录
 * 
 * @author Administrator
 *
 */
public class FloodRecode {

    /**
     * 每秒最大包数量
     */
    public static final int MAX_SECEND_PACKS = 64;

    /**
     * 每分钟最大包数量
     */
    public static final int MAX_MINUTE_PACKS = 1024;

    /**
     * 上次检查命令次数的时间戳
     */
    public long lastPackTime = 0;
    /**
     * 秒累计数据包数量
     */
    public int lastSecendPacks = 0;
    /**
     * 分累计数据包数量
     */
    public int lastMinutePacks = 0;

    

}
