package com.xiuxiu.app.protocol.api.account;

import java.util.ArrayList;
import java.util.List;

public class GetAccountInfo {
    public static class ActionInfo {
        private String action;
        private int startUid;
        private int count;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public int getStartUid() {
            return startUid;
        }

        public void setStartUid(int startUid) {
            this.startUid = startUid;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        @Override
        public String toString() {
            return "ActionInfo{" +
                    "action='" + action + '\'' +
                    ", startUid=" + startUid +
                    ", count=" + count +
                    '}';
        }
    }

    private List<ActionInfo> entities = new ArrayList<>();
    private String sign;

    public List<ActionInfo> getEntities() {
        return entities;
    }

    public void setEntities(List<ActionInfo> entities) {
        this.entities = entities;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "GetAccountInfo{" +
                "entities=" + entities +
                ", sign='" + sign + '\'' +
                '}';
    }
}
