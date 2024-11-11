package com.xiuxiu.app.protocol.api.temp.club;

/**
 * @auther: yuyunfei
 * @date: 2020/1/5 14:04
 * @comment:
 */
public class KickOutClub {
    public long clubUid;    // 俱乐部UID
    public String sign;     // md5(clubUid + key)

    @Override
    public String toString() {
        return "KickOutClub{" +
                ", clubUid=" + clubUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
