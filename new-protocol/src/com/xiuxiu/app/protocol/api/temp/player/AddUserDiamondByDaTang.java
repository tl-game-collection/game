package com.xiuxiu.app.protocol.api.temp.player;

public class AddUserDiamondByDaTang {
    public long uid;                    // 玩家id
    public int amount;                  // 房卡
    public int type;                    // 类型 对应 EMoneyExpendType
    public long fromUid;                // 亲友圈ID 或者 联盟ID 没有为-1
    public long operatorUid;            // 被赠送的玩家id
    public String sign; // md5(uid + amount + type + key)

    @Override
    public String toString() {
        return "AddUserDiamondByDaTang{" +
                "uid=" + uid +
                ", amount=" + amount +
                ", fromUid=" + fromUid +
                ", operatorUid=" + operatorUid +
                ", type=" + type +
                ", sign='" + sign + '\'' +
                '}';
    }
}
