package com.xiuxiu.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.xiuxiu.core.utils.HttpUtil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.RandomUtil;
import com.xiuxiu.core.utils.StringUtil;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SMSManager {
    private static class SMSManagerHolder {
        private static SMSManager instance = new SMSManager();
    }

    public static SMSManager I = SMSManagerHolder.instance;

    protected RedissonClient redissonClient;
    protected long timeout = 5;
    protected TimeUnit timeUnit = TimeUnit.MINUTES;
    protected SmsConfig smsConfig;

    private IAcsClient smsAcsClient;

    private SMSManager() {
    }

    public void init(RedissonClient redissonClient, SmsConfig smsConfig) {
        this.redissonClient = redissonClient;
        this.smsConfig = smsConfig;

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", this.smsConfig.accessKeyId, this.smsConfig.accessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", this.smsConfig.product, this.smsConfig.domain);
            this.smsAcsClient = new DefaultAcsClient(profile);
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    public void setTimeout(long timeout, TimeUnit timeUnit) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    public boolean setAuthCode(String key, String value) {
        RBucket<String> bucket = this.redissonClient.getBucket(key);
        bucket.set(value);
        bucket.expire(this.timeout, this.timeUnit);
        return true;
    }

    public String getAuthCode(String key) {
        RBucket<String> bucket = this.redissonClient.getBucket(key);
        return bucket.get();
    }

    public String generateAuthCode(String phone) {
        String authCode = this.getAuthCode(phone);
        if (StringUtil.isEmptyOrNull(authCode)) {
            authCode = String.valueOf(RandomUtil.random(1000, 9999));
        }
        String err = "FAIL";
        if ("ali".equalsIgnoreCase(this.smsConfig.type)) {
            err = this.sendSmsWithAli(phone, authCode);
        } else if ("ihuyi".equalsIgnoreCase(this.smsConfig.type)) {
            err = this.sendSmsWithIHuYi(phone, authCode);
        } else if ("fgcs".equalsIgnoreCase(this.smsConfig.type)) {
            err = this.sendSmsWithFGCS(phone, authCode);
        }
        if (err.equals("OK")) {
            this.setAuthCode(phone, authCode);
            return authCode;
        }
        return err;
    }

    protected String sendSmsWithAli(String phone, String authCode) {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(phone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(this.smsConfig.signName);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(this.smsConfig.templateCode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam("{\"code\":" + authCode +  "}");

        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        //request.setOutId("yourOutId");

        //hint 此处可能会抛出异常，注意catch
        try {
            SendSmsResponse response = this.smsAcsClient.getAcsResponse(request);
            if(response.getCode() != null && response.getCode().equals("OK")) {
                return "OK";
            }
            if (response.getCode() != null && response.getCode().equals("MOBILE_NUMBER_ILLEGAL")) {
                return "MOBILE_NUMBER_ILLEGAL";
            }
            return "FAIL";
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return "FAIL";
    }

    protected String sendSmsWithIHuYi(String phone, String authCode) {
        HashMap<String, String> data = new HashMap<>();
        data.put("account", this.smsConfig.accessKeyId);
        data.put("password", this.smsConfig.accessKeySecret);
        data.put("mobile", phone);
        data.put("content", String.format("您的验证码是：%s。请不要把验证码泄露给其他人。", authCode));
        data.put("format", "json");
        String respStr = HttpUtil.postWithForm("http://106.ihuyi.com/webservice/sms.php?method=Submit", StringUtil.hashMap2QueryString(data));
        if (null == respStr) {
            return "FAIL";
        }
        HashMap<String, Object> resp = JsonUtil.fromJson(respStr, HashMap.class);
        if (null == resp) {
            return "FAIL";
        }
        int ret = (int) resp.get("code");
        if (2 == ret) {
            return "OK";
        }
        if (406 == ret) {
            return "MOBILE_NUMBER_ILLEGAL";
        }
        return "FAIL";
    }

    private String sendSmsWithFGCS(String phone, String authCode) {
        HashMap<String, String> data = new HashMap<>();
        data.put("Account", this.smsConfig.accessKeyId);
        data.put("Pwd", this.smsConfig.accessKeySecret);
        data.put("Mobile", phone);
        data.put("Content", authCode);
        data.put("TemplateId", this.smsConfig.templateCode);
        data.put("SignId", this.smsConfig.signName);
        String respStr = HttpUtil.postWithForm("http://api.feige.ee/SmsService/Template", StringUtil.hashMap2QueryString(data));
        if (null == respStr) {
            return "FAIL";
        }
        HashMap<String, Object> resp = JsonUtil.fromJson(respStr, HashMap.class);
        if (null == resp) {
            return "FAIL";
        }
        if (1 == (int) resp.getOrDefault("InvalidCount", 0)) {
            return "MOBILE_NUMBER_ILLEGAL";
        } else if (1 == (int) resp.getOrDefault("SuccessCount", 0)) {
            return "OK";
        }
        return "FAIL";
    }

}
