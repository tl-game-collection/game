package com.xiuxiu.app.protocol.client.poker.cow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 11:48
 * @comment:
 */
public class PCLIPokerNtfGetCowRobotInfo {
    public List<Byte> cards = new ArrayList<>(); //手牌
    public int fourCardType;   //前4张牌牌型(0.无牛 1-10代表牛几 11炸弹)
    public boolean isBiggerN9; //最终牌型是否大于牛9
    public int fiveCardType;   //5张牌牌型
    public Map<Long,Boolean> result = new HashMap<>(); //和所有人比牌结果(uid,bool)

    @Override
    public String toString() {
        return "PCLIPokerNtfGetCowRobotInfo{" +
                "cards=" + cards +
                ", fourCardType=" + fourCardType +
                ", isBiggerN9=" + isBiggerN9 +
                ", fiveCardType=" + fiveCardType +
                ", result=" + result +
                '}';
    }
}
