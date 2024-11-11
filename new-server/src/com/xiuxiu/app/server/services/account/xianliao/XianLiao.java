package com.xiuxiu.app.server.services.account.xianliao;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XianLiao {
    public static XianLiao I;
    private HashMap<Integer, XianLiaoAppInfo> allAppInfo;
    private RequestConfig requestConfig;

    private XianLiao() {
        this.allAppInfo = new HashMap();
        this.requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000).setSocketTimeout(5000).setRedirectsEnabled(true).build();
    }

    static {
        I = XianLiao.XianLiaoHolder.instance;
    }

    private static class XianLiaoHolder {
        private static XianLiao instance = new XianLiao();

        private XianLiaoHolder() {
        }
    }

    public String[] getAccessTokenByCode(String code){
        try {
            //设置post请求参数
            XianLiao.XianLiaoAppInfo appInfo = new XianLiao.XianLiaoAppInfo();

            List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
            list.add(new BasicNameValuePair("appid", appInfo.appId));
            list.add(new BasicNameValuePair("appsecret", appInfo.appSecret));
            list.add(new BasicNameValuePair("grant_type", "authorization_code"));
            list.add(new BasicNameValuePair("code", code));
            HashMap<String, Object> param = this.post("https://ssgw.updrips.com/oauth2/accessToken",list);
            HashMap data = JsonUtil.fromJson(param.get("data").toString(), new TypeReference<HashMap<String, Object>>() {
            });
            String accessToken = data.get("access_token").toString();
            String refreshToken = data.get("refresh_token").toString();
            String expiresIn = data.get("expires_in").toString();
            String[] m_str = {"","",""};
            if ((int)param.get("err_code") != 0) {
                return null;
            } else {
                if (accessToken != null) {
                    m_str[0] = accessToken.toString();
                }
                if (refreshToken != null) {
                    m_str[1] = refreshToken.toString();
                }
                if (expiresIn != null) {
                    m_str[2] = expiresIn.toString();
                }
                return m_str;
            }
        } catch (Exception e) {

        }

        return null;
    }

    private HashMap<String, Object> post(String url, List param) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            Throwable var3 = null;

            String json;
            try {
                HttpPost httpPost = new HttpPost(url);
                httpPost.setConfig(this.requestConfig);
                httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
                httpPost.setEntity(new UrlEncodedFormEntity(param,"UTF-8"));
                HttpResponse response = httpClient.execute(httpPost);
                if (200 == response.getStatusLine().getStatusCode()) {
                    json = EntityUtils.toString(response.getEntity());
                    HashMap var7 = JsonUtil.fromJson(json, new TypeReference<HashMap<String, Object>>() {
                    });
                    return var7;
                }
                json = null;
            } catch (Throwable var18) {
                var3 = var18;
                throw var18;
            } finally {
                if (httpClient != null) {
                    if (var3 != null) {
                        try {
                            httpClient.close();
                        } catch (Throwable var17) {
                            var3.addSuppressed(var17);
                        }
                    } else {
                        httpClient.close();
                    }
                }

            }
            return null;
        } catch (IOException var20) {
            var20.printStackTrace();
            return null;
        }
    }

    public XianLiaoUserInfo getXianLiaoUserInfoByAccessToken(String accessToken) {
        List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
        list.add(new BasicNameValuePair("access_token", accessToken));

        HashMap<String, Object> param = this.post("https://ssgw.updrips.com/resource/user/getUserInfo",list);
        if ((int)param.get("err_code") != 0) {
            return null;
        } else {
            XianLiaoUserInfo info = new XianLiaoUserInfo();
            HashMap var7 = JsonUtil.fromJson(param.get("data").toString(), new TypeReference<HashMap<String, Object>>() {
            });

            info.setOpenId(var7.get("openId").toString());
            info.setNickName(var7.get("nickName").toString());
            info.setOriginalAvatar(new String(((String)var7.get("originalAvatar")).getBytes(Charsetutil.ISO_8859_1), Charsetutil.UTF8));
            info.setSmallAvatar(new String(((String)var7.get("smallAvatar")).getBytes(Charsetutil.ISO_8859_1), Charsetutil.UTF8));
            info.setGender((int)var7.get("gender"));
            return info;
        }
    }

    private static class XianLiaoAppInfo {
        private String appId;
        private String appSecret;

        XianLiaoAppInfo() {
            this.appId = "MH6sum7ubmi3QjAv";
            this.appSecret = "d5mmGclCPYpfvTVX";
        }

        XianLiaoAppInfo(String appId, String appSecret) {
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
}
