package com.xiuxiu.app.server.room.record.mahjong2;

public class XZDDResultRecordAction extends ResultRecordAction {
    public static class ScoreInfo extends ResultRecordAction.ScoreInfo {
    }

    public static class PlayerInfo extends ResultRecordAction.PlayerInfo {
        protected boolean isChaHuaZhu;          // 是否查花猪
        protected boolean isChaDaJiao;          // 是否查大叫
        protected int huaZhuValue;              // 查花猪分数
        protected int daJiaoValue;              // 查大叫分数

        public boolean isChaHuaZhu() {
            return isChaHuaZhu;
        }

        public void setChaHuaZhu(boolean chaHuaZhu) {
            isChaHuaZhu = chaHuaZhu;
        }

        public boolean isChaDaJiao() {
            return isChaDaJiao;
        }

        public void setChaDaJiao(boolean chaDaJiao) {
            isChaDaJiao = chaDaJiao;
        }

        public int getHuaZhuValue() {
            return huaZhuValue;
        }

        public void setHuaZhuValue(int huaZhuValue) {
            this.huaZhuValue = huaZhuValue;
        }

        public int getDaJiaoValue() {
            return daJiaoValue;
        }

        public void setDaJiaoValue(int daJiaoValue) {
            this.daJiaoValue = daJiaoValue;
        }
    }

    public XZDDResultRecordAction() {
        super();
    }
}
