package com.xiuxiu.wechat;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;

public class WeChat {
    private static class WeChatHolder {
        private static WeChat instance = new WeChat();
    }

    public static WeChat I = WeChatHolder.instance;

    private static class WeChatAppInfo {
        private String appId;
        private String appSecret;

        public WeChatAppInfo(String appId, String appSecret) {
            this.appId = appId;
            this.appSecret = appSecret;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }
    }

    private HashMap<Integer, WeChatAppInfo> allAppInfo = new HashMap<>();

    private RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(5000)   //设置连接超时时间
            .setConnectionRequestTimeout(5000) // 设置请求超时时间
            .setSocketTimeout(5000)
            .setRedirectsEnabled(true)//默认允许自动重定向
            .build();

    private WeChat() {
    }

    public void add(int channel, String appId, String appSecret) {
        this.allAppInfo.putIfAbsent(channel, new WeChatAppInfo(appId, appSecret));
    }

    /**
     * [accessToken, openId]
     * @param code
     * @return
     */
    public String[] getAccessTokenAndOpenIdByCode(int channel, String code) {
        WeChatAppInfo appInfo = this.allAppInfo.get(channel);
        if (null == appInfo) {
            return null;
        }
        HashMap<String, Object> param = this.get(Config.WECHAT_ACCESS_TOKEN_URL + "?appid=" + appInfo.appId + "&secret=" + appInfo.appSecret + "&code=" + code + "&grant_type=authorization_code");
        Object accessToken = param.getOrDefault("access_token", null);
        if (null == accessToken) {
            return null;
        }
        Object openId = param.getOrDefault("openid", null);
        if (null == openId) {
            return null;
        }
        return new String[] {accessToken.toString(), openId.toString()};
    }

    public WeChatUserInfo getWeChatUserInfoByAccessToken(String accessToken, String openId) {
        HashMap<String, Object> param = this.get(Config.WECHAT_USER_INFO_URL + "?access_token=" + accessToken + "&openId=" + openId + "&lang=zh_CN");
        if (null != param.get("errcode")) {
            return null;
        }
        WeChatUserInfo info = new WeChatUserInfo();
        info.setUid(param.get("unionid").toString());
        info.setNick(new String(((String) param.get("nickname")).getBytes(Charsetutil.ISO_8859_1), Charsetutil.UTF8));
        info.setSex((byte) (1 == (Integer) param.get("sex") ? 1 : 0));  // 0:女 1;男
        info.setIcon(param.get("headimgurl").toString());
        info.setCity(new String(((String) param.get("city")).getBytes(Charsetutil.ISO_8859_1), Charsetutil.UTF8));
        info.setCountry(new String(((String) param.get("country")).getBytes(Charsetutil.ISO_8859_1), Charsetutil.UTF8));
        return info;
    }

    private HashMap<String, Object> get(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet http = new HttpGet(url);
            http.setConfig(this.requestConfig);
            HttpResponse response = httpClient.execute(http);
            if (200 == response.getStatusLine().getStatusCode()) {
                String json = EntityUtils.toString(response.getEntity());
                //System.out.println("url:" + url + "\nresponse:" + json);
                return JsonUtil.fromJson(json, new TypeReference<HashMap<String, Object>>() {});
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
