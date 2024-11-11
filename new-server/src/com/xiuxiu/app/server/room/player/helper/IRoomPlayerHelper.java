package com.xiuxiu.app.server.room.player.helper;

/**
 * 定义房间玩家小助手接口
 * 
 * @author Administrator
 *
 */
public interface IRoomPlayerHelper {

    /**
     * 获取分数
     * @return
     */
    int getScore();
    
    /**
     * 初始化
     */
    void init();
    
    /**
     * 获取当前局数
     * @return
     */
    int getCurBureau();
}
