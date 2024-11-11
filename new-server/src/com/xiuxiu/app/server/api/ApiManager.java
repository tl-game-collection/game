package com.xiuxiu.app.server.api;

import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.core.utils.*;

public class ApiManager {
    private static class ApiManagerHolder {
        private static final ApiManager INSTANCE = new ApiManager();
    }

    public static ApiManager I = ApiManagerHolder.INSTANCE;

    private static class ApiUserInfo {
        public String name = "";
        public String passwd = "";
        public String token = "";
        public boolean login = false;
        public long lastOpTime = -1;
    }

    private ApiUserInfo curApiUserInfo = new ApiUserInfo();

    private ApiManager() {
    }

    private static class DingDingPostInfo {
        private static class Text {
            public String content = "";
        }
        public String msgtype = "text";
        public Text text = new Text();
    }

    public synchronized void getApiUserInfo() {
        this.curApiUserInfo.login = false;
        this.curApiUserInfo.name = getRandomStr(8);
        this.curApiUserInfo.passwd = getRandomStr(16);
        this.curApiUserInfo.token = getRandomStr(32);
        DingDingPostInfo info = new DingDingPostInfo();
        info.text.content = String.format("[用户信息] %s %s", this.curApiUserInfo.name, this.curApiUserInfo.passwd);
        HttpUtil.post(Config.DINGDING_ROBOT_URL, JsonUtil.toJson(info));
        Logs.API.debug("[TOKEN] %s", this.curApiUserInfo.token);
    }

    public synchronized boolean isVerifyLoginInfo(String name, String passwd) {
        if (StringUtil.isEmptyOrNull(name) || StringUtil.isEmptyOrNull(passwd)) {
            return false;
        }
        return this.curApiUserInfo.name.equals(name) && MD5Util.getMD5(this.curApiUserInfo.passwd).equals(passwd);
    }

    public synchronized boolean isVerifyToken(String token) {
        if (StringUtil.isEmptyOrNull(token)) {
            return false;
        }
        if (!this.curApiUserInfo.login) {
            return false;
        }
        return this.curApiUserInfo.token.equals(token);
    }

    public synchronized String getName() {
        return this.curApiUserInfo.name;
    }

    public synchronized String getToken() {
        return this.curApiUserInfo.token;
    }

    public synchronized boolean login() {
        if (this.curApiUserInfo.login) {
            return false;
        }
        this.curApiUserInfo.login = true;
        return true;
    }

    public synchronized void logout() {
        if (this.curApiUserInfo.login) {
            this.curApiUserInfo.login = false;
            this.curApiUserInfo.name = "";
            this.curApiUserInfo.passwd = "";
            this.curApiUserInfo.token = "";
        }
    }

    public synchronized void updateOpTime() {
        this.curApiUserInfo.lastOpTime = System.currentTimeMillis();
    }

    public boolean isOnline() {
        return this.curApiUserInfo.login;
    }

    public long getLastOpTime() {
        return this.curApiUserInfo.lastOpTime;
    }

    private static String getRandomStr(int len) {
        StringBuilder uid = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int type = RandomUtil.random(3);
            switch (type){
                case 0:
                    uid.append(RandomUtil.random(10));
                    break;
                case 1:
                    uid.append((char)(RandomUtil.random(65, 90)));
                    break;
                case 2:
                    uid.append((char)(RandomUtil.random(97, 122)));
                    break;
                default:
                    break;
            }
        }
        return uid.toString();
    }
}
