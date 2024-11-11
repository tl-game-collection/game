package com.xiuxiu.app.server.room.record.mahjong2;

public class YCXLResultRecordAction extends ResultRecordAction {
    public static class ScoreInfo extends ResultRecordAction.ScoreInfo {
    }

    public static class PlayerInfo extends ResultRecordAction.PlayerInfo {
        protected boolean isChaHuaZhu;          // 是否查花猪
        protected boolean isChaDaJiao;          // 是否查大叫
        protected int chaValue;                 // 查大叫/查花猪分数

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

        public int getChaValue() {
            return chaValue;
        }

        public void setChaValue(int chaValue) {
            this.chaValue = chaValue;
        }
    }

    public YCXLResultRecordAction() {
        super();
    }
}
