package com.xiuxiu.app.server.player;

import java.io.Serializable;

public class VisitCardInfo implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6801492099725615095L;
    protected String desc;
    protected String imgUrl;

    public VisitCardInfo() {

    }

    public VisitCardInfo(String desc, String imgUrl) {
        this.desc = desc;
        this.imgUrl = imgUrl;
    }

    public void setDescAndImgUrl(String desc, String imgUrl) {
        this.desc = desc;
        this.imgUrl = imgUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
