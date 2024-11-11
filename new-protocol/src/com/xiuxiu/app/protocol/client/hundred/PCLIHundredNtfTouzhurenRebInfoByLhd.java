package com.xiuxiu.app.protocol.client.hundred;

import java.util.ArrayList;
import java.util.List;

public class PCLIHundredNtfTouzhurenRebInfoByLhd {
    public long boixId;
    public List<Integer> rebInfo = new ArrayList<>();
    public int remainReb;
    public int remainRebTotal;

    @Override
    public String toString() {
        return "PCLIHundredNtfTouzhurenRebInfoByLhd{" +
                "boixId=" + boixId +
                ", rebInfo=" + rebInfo +
                ", remainReb=" + remainReb +
                ", remainRebTotal=" + remainRebTotal +
                '}';
    }
}
