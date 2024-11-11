package com.xiuxiu.app.protocol.client.player;

import java.util.ArrayList;
import java.util.List;

public class PCLIPlayerNtfVisitCardInfo {
    public static class VisitCardInfo {
        public String desc;
        public String imgPath;

        public VisitCardInfo() {

        }

        public VisitCardInfo(String desc, String imgPath) {
            this.desc = desc;
            this.imgPath = imgPath;
        }

        @Override
        public String toString() {
            return "VisitCardInfo{" +
                    "desc='" + desc + '\'' +
                    ", imgPath='" + imgPath + '\'' +
                    '}';
        }
    }
    public List<VisitCardInfo> list = new ArrayList<>();
    /**
     * 银行卡号
     */
    public String bankCard;
    /**
     * 银行卡持卡人
     */
    public String bankCardHolder;

    @Override
    public String toString() {
        return "PCLIPlayerNtfVisitCardInfo{" +
                "list=" + list +
                '}';
    }
}
