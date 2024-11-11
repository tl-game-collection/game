package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongReqShuaiPai {
    public List<Byte> card = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIMahjongReqShuaiPai{" +
                "card=" + card +
                '}';
    }
}
