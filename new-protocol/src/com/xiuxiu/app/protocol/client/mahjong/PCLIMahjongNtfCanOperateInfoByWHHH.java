package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfCanOperateInfoByWHHH {
    public List<Operate> ops = new ArrayList<>();

    public static class Operate {
        public boolean bump;
        public boolean bar;
        public boolean hu;
        public boolean eat;
        public byte card;

        @Override
        public String toString() {
            return "Operate{" +
                    "bump=" + bump +
                    ", bar=" + bar +
                    ", hu=" + hu +
                    ", eat=" + eat +
                    ", card=" + card +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PCLIMahjongNtfCanOperateInfoByWHHH{" +
                "ops=" + ops +
                '}';
    }
}
