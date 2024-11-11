package com.xiuxiu.app.server.player;

import com.xiuxiu.app.protocol.client.PCLIPlayerBriefInfo;
import com.xiuxiu.app.server.constant.EMoneyType;
import com.xiuxiu.app.server.statistics.constant.EMoneyExpendType;

public interface IPlayer {
    /**
     * 获取玩家Uid
     * @return
     */
    long getUid();

    /**
     * 获取玩家名字
     * @return
     */
    String getName();

    /**
     * 获取玩家头像
     * @return
     */
    String getIcon();

    /**
     * 获取玩家性别
     * @return
     */
    byte getSex();

    /**
     * 获取playerUid别名
     * @param playerUid
     * @return
     */
    String getAlias(long playerUid);

    /**
     * 获取纬度
     * @return
     */
    double getLat();

    /**
     * 获取经度
     * @return
     */
    double getLng();

    /**
     * 是否在线
     * @return
     */
    boolean isOnline();

    /**
     * 更改房间id
     * @param roomId
     * @param roomType
     */
    void changeRoomId(int roomId, int roomType);

    /**
     * 获取货币
     * @param type
     * @return
     */
    int getMoneyByType(EMoneyType type);

    /**
     * 获取简易信息
     * @param player
     * @return
     */
    PCLIPlayerBriefInfo getPlayerBriefInfo(IPlayer player);

    /**
     * 是否有货币
     * @param type
     * @param value
     * @return
     */
    boolean hasMoney(EMoneyType type, Number value);

    /**
     * 添加货币
     * @param type
     * @param value
     * @param optPlayer
     * @return
     */
    boolean addMoney(EMoneyType type, Number value, long optPlayer, long fromUid, EMoneyExpendType expendType, long operatorUid);

    /**
     * 发送数据到客户端
     * @param commandId
     * @param message
     * @return
     */
    boolean send(int commandId, Object message);
}
