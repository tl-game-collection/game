package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqChangeEmotion {
    public byte emotion;       // 情感 0: 保密, 1: 单身, 2: 恋爱中, 3: 已婚, 4:  同性

    @Override
    public String toString() {
        return "PCLIPlayerReqChangeEmotion{" +
                "emotion=" + emotion +
                '}';
    }
}