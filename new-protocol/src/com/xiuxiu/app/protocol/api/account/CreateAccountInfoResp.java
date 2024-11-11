package com.xiuxiu.app.protocol.api.account;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class CreateAccountInfoResp extends ErrorMsg {
    private List<Long> created = new ArrayList<>();
    private String sign;

    public List<Long> getCreated() {
        return created;
    }

    public void setCreated(List<Long> created) {
        this.created = created;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "CreateAccountInfoResp{" +
                "created=" + created +
                ", sign='" + sign + '\'' +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
