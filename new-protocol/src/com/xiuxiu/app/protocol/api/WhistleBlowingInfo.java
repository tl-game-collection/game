package com.xiuxiu.app.protocol.api;

import java.util.List;

public class WhistleBlowingInfo {
    protected int type;         // 1: 玩家, 2: 群
    protected long uid;         // 玩家类型对应玩家uid, 群类型对应群uid
    protected String wbContent; // 举报内容
    protected List<String> imgList; // 图片路径

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getWbContent() {
        return wbContent;
    }

    public void setWbContent(String wbContent) {
        this.wbContent = wbContent;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

    @Override
    public String toString() {
        return "WhistleBlowingInfo{" +
                "type=" + type +
                ", uid=" + uid +
                ", wbContent='" + wbContent + '\'' +
                ", imgList=" + imgList +
                '}';
    }
}
