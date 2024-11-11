package com.xiuxiu.app.server.services.account.xianliao;

public class XianLiaoUserInfo {
    private String openId;
    private String nickName;
    private String originalAvatar;
    private String smallAvatar;
    private int gender;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getOriginalAvatar() {
        return originalAvatar;
    }

    public void setOriginalAvatar(String originalAvatar) {
        this.originalAvatar = originalAvatar;
    }

    public String getSmallAvatar() {
        return smallAvatar;
    }

    public void setSmallAvatar(String smallAvatar) {
        this.smallAvatar = smallAvatar;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "XianLiaoUserInfo{" +
                "openId='" + openId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", originalAvatar='" + originalAvatar + '\'' +
                ", smallAvatar='" + smallAvatar + '\'' +
                ", gender=" + gender +
                '}';
    }
}
