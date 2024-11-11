package com.xiuxiu.app.server.room.normal.action;

import com.xiuxiu.app.server.room.normal.IRoomPlayer;

public interface IAction {
    /**
     * 暂停
     */
    void pause();

    /**
     * 恢复
     */
    void recover();

    /**
     * 上线
     * @param player
     */
    void online(IRoomPlayer player);

    /**
     * 下线
     * @param player
     */
    void offline(IRoomPlayer player);

    /**
     * 能否执行
     * @param curTime
     * @return
     */
    boolean canAction(long curTime);

    /**
     * 执行
     * @param timeout
     * @return
     */
    boolean action(boolean timeout);
}
