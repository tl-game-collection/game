package com.xiuxiu.xpush;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.xiuxiu.core.thread.ConsumeThread;

public class XPushManager {
    private static class XPushManagerHolder {
        private static XPushManager instance = new XPushManager();
    }

    public static XPushManager I = XPushManagerHolder.instance;

    private XPushThread pushThread = new XPushThread();
    private JPushClient client;
    private boolean production = false;

    private XPushManager() {
    }

    public void init(String appKey, String masterSecret, boolean production) {
        this.production = production;
        this.pushThread.start();
        ClientConfig config = ClientConfig.getInstance();
//        this.client = new JPushClient(masterSecret, appKey, null, config);
        //String authCode = ServiceHelper.getBasicAuthorization(appKey, masterSecret);
        //NettyHttpClient httpClient = new NettyHttpClient(authCode, null, config);
        //this.client.getPushClient().setHttpClient(httpClient);
    }

    public void shutdown() {
        if (null != this.client) {
            this.client.close();
        }
        this.pushThread.stop();
    }

    public void push(String tagName, String sendName, int type, String msg) {
        this.pushThread.add(new XPushInfo(msg, sendName, tagName, type));
    }

    public static class XPushInfo {
        protected String message;
        protected String sendName;
        protected String tagName;
        protected int type;

        public XPushInfo() {

        }

        public XPushInfo(String message, String sendName, String tagName, int type) {
            this.message = message;
            this.sendName = sendName;
            this.tagName = tagName;
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSendName() {
            return sendName;
        }

        public void setSendName(String sendName) {
            this.sendName = sendName;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    private static class XPushThread extends ConsumeThread<XPushInfo> {
        public XPushThread() {
            super("XPushThread");
        }

        @Override
        protected void exec(XPushInfo info) {

            PushPayload payload = PushPayload.newBuilder()
                    .setPlatform(Platform.all())
                    .setAudience(Audience.alias(info.getTagName()))
                    .setNotification(Notification.newBuilder()
                            .setAlert(info.getMessage())
                            .addPlatformNotification(AndroidNotification.newBuilder()
                                    .setTitle(info.getSendName()).addExtra("type", info.getType()).build())
                            .addPlatformNotification(IosNotification.newBuilder()
                                    .incrBadge(1).addExtra("type", info.getType()).build()).build())
                    .setMessage(Message.content(info.getMessage()))
                    .setOptions(Options.newBuilder().setApnsProduction(XPushManager.I.production).build())
                    .build();
            if (null != XPushManager.I.client) {
                try {
                    XPushManager.I.client.sendPush(payload);
                } catch (APIConnectionException e) {
                    e.printStackTrace();
                } catch (APIRequestException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
