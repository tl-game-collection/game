package com.xiuxiu.app.protocol.client.poker;

/**
 * @auther: yuyunfei
 * @date: 2019-07-22 21:21
 * @comment:
 */
public class PCLIPokerNetAutoMode {
    public long autoPlayerUid;
    public boolean auto; // true:托管，false:解除托管

    public PCLIPokerNetAutoMode(){

    }

    public PCLIPokerNetAutoMode(long autoPlayerUid,  boolean auto) {
        this.autoPlayerUid = autoPlayerUid;
        this.auto = auto;
    }

    @Override
    public String toString() {
        return "PCLIPokerNetAutoMode{" +
                "autoPlayerUid=" + autoPlayerUid +
                ",auto=" + auto +
                '}';
    }
}
