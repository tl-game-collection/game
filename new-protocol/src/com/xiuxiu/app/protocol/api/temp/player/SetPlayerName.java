package com.xiuxiu.app.protocol.api.temp.player;

/**
 * 修改玩家昵称
 * @date 2020/1/22
 * @author luocheng
 */
public class SetPlayerName {
    public long playerUid;          //玩家id
    public String newName;            //要修改的玩家名字
    public String sign;             // md5(playerUid + newName + key)

    @Override
    public String toString() {
        return "SetPlayerName{" +
                "playerUid=" + playerUid +
                ", newName='" + newName + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
