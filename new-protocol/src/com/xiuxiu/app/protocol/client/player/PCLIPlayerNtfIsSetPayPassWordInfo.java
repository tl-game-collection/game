package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfIsSetPayPassWordInfo {
    public int isSet;       // 是否设置支付密码 0:未设置 1:已设置

    public PCLIPlayerNtfIsSetPayPassWordInfo(){}

    public PCLIPlayerNtfIsSetPayPassWordInfo(int isSet){
        this.isSet = isSet;
    }


    @Override
    public String toString() {
        return "PCLIPlayerNtfIsSetPayPassWordInfo{" +
                "isSet=" + isSet +
                '}';
    }
}
