package com.xiuxiu.app.protocol.client.player;

/**
 *
 */
public class PCLIPlayerNtfSearch {
   public long uid;         // 玩家Uid
   public String name;      // 玩家名称
   public String icon;      // 玩家头像
   public int count;        // 玩家俱乐部个数

    @Override
    public String toString() {
        return "PCLIPlayerNtfSearch{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", count=" + count +
                '}';
    }
}
