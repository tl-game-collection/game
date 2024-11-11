package com.xiuxiu.app.protocol.client.poker.cow;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 10:34
 * @comment:
 */
public class PCLIPokerReqCowSelectBankerInfo {
    public int selectState;           // 0 放弃，1 选择要庄

    @Override
    public String toString() {
        return "PCLIPokerReqCowSelectBankerInfo{" +
                "selectState=" + selectState +
                '}';
    }
}
