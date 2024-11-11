package com.xiuxiu.app.server.services.account.dingding;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.core.log.Logs;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.HttpUtil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.wechat.WeChatUserInfo;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉登录
 * accessKey  应用的 appId
 * timestamp 当前时间戳，单位是毫秒
 * signature 通过 appSecret 计算出来的签名值
 * tmp_auth_code 用户授权给钉钉开放应用的免登授权码，第一步中获取的 code
 */
public class DingDing {
    public static DingDing I;
    private RequestConfig requestConfig;

    private DingDing() {
        this.requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000).setSocketTimeout(5000).setRedirectsEnabled(true).build();
    }

    static {
        I = DingDing.DingDingHolder.instance;
    }

    private static class DingDingHolder {
        private static DingDing instance = new DingDing();

        private DingDingHolder() {
        }
    }

    /**
     * 获取用户基本信息
     *
     * @param code
     * @return
     */
    public Map<String, Object> getUserInfoByCode(String code /* 验证码 */) {
        long timestamp = System.currentTimeMillis();
        String url = "https://oapi.dingtalk.com/sns/getuserinfo_bycode?signature=" + signatureHandle(timestamp, Config.DINGDING_APP_SECRET) + "&timestamp=" + timestamp + "&accessKey=" + Config.DINGDING_APP_KEY;
        Map<String, Object> param = new HashMap<>();
        param.put("tmp_auth_code", code);
        String jsonResult = HttpUtil.post(url, JsonUtil.toJson(param));
        Map<String, Object> result = JsonUtil.fromJson(jsonResult, new TypeReference<HashMap<String, Object>>() {
        });
        return result;
    }

    /**
     * 计算签名值
     *
     * @param timestamp,appSecret
     * @return
     */
    private String signatureHandle(long timestamp, String appSecret) {
        try {
            // 根据timestamp, appSecret计算签名值
            String stringToSign = String.valueOf(timestamp);
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signatureBytes = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String signature = new String(Base64.encodeBase64(signatureBytes));
            String urlEncodeSignature = urlEncode(signature, "UTF-8");
            return urlEncodeSignature;
        } catch (Exception e) {
            Logs.CORE.error("根据timestamp, appSecret计算签名值异常", e);
        }
        return null;
    }

    // encoding参数使用utf-8
    public static String urlEncode(String value, String encoding) {
        if (value == null) {
            return "";
        }
        try {
            String encoded = URLEncoder.encode(value, encoding);
            return encoded.replace("+", "%20").replace("*", "%2A")
                    .replace("~", "%7E").replace("/", "%2F");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("FailedToEncodeUri", e);
        }
    }

}
