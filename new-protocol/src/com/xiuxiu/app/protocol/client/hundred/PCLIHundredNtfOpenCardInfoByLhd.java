package com.xiuxiu.app.protocol.client.hundred;

import java.util.ArrayList;
import java.util.List;

public class PCLIHundredNtfOpenCardInfoByLhd {
    public long boxId;
    public List<Byte> cards = new ArrayList<>();
    public int winIndex = 0;//1是龙赢，2 是和赢，3 是虎赢
    public boolean bankerValueOverFlow = false;

    @Override
    public String toString() {
        return "PCLIHundredNtfOpenCardInfoByLhd{" +
                "boxId=" + boxId +
                ", cards=" + cards +
                ", winIndex=" + winIndex +
                ", bankerValueOverFlow=" + bankerValueOverFlow +
                '}';
    }
}
