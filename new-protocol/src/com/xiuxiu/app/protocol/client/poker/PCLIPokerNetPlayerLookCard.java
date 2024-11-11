package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther: yuyunfei
 * @date: 2019-07-22 21:21
 * @comment:
 */
public class PCLIPokerNetPlayerLookCard {
    public long playerUid;
    public int cardType;
    public List<Byte> card = new ArrayList<>();                  // 结果牌
    public List<Byte> handCard = new ArrayList<>();                  // 手牌
    public int cardDouble;
    /** 额外牌型 */
    public int cardTypeExtra;
 
    @Override
    public String toString() {
        return "PCLIPokerNetPlayerLookCard{" +
                "playerUid=" + playerUid +
                "card"+card+
                "cardType"+cardType+
                "cardDouble"+cardDouble+
                "cardTypeExtra"+cardTypeExtra+
                '}';
    }
}
