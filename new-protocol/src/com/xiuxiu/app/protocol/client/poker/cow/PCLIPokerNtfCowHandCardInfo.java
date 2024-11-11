package com.xiuxiu.app.protocol.client.poker.cow;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 10:00
 * @comment:
 */
public class PCLIPokerNtfCowHandCardInfo {
    public List<Byte> handCard = new ArrayList<>();
    public int sendCardCount = 1;
    @Override
    public String toString() {
        return "PCLIPokerNtfCowHandCardInfo{" +
                "handCard=" + handCard +
                ", sendCardCount=" + sendCardCount +
                '}';
    }
}
