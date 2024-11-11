package com.xiuxiu.app.server.table;

import com.xiuxiu.core.utils.FileUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SharkMultiple {
    public static class SharkMultipleInfo {
        protected int id;
        protected String typeName;
        protected int mul;
        protected double rate;

        public int getId() {return id;}
        public void setId(int id) {this.id = id;}
        public String getTypeName() {return typeName;}
        public void setTypeName(String typeName) {this.typeName = typeName;}
        public int getMul() { return mul;}
        public void setMul(int mul) {this.mul = mul;}
        public double getRate() {return rate;}
        public void setRate(double rate) {this.rate = rate;}
    }

    protected List<SharkMultipleInfo> list = new ArrayList<>();
    protected HashMap<Integer, SharkMultipleInfo> map = new HashMap<>();
    public List<SharkMultipleInfo> getList() {
        return this.list;
    }
    public HashMap<Integer, SharkMultipleInfo> getMap() {
        return this.map;
    }
    public void read(String path) {
        String content = FileUtil.readFileString(path + "/SharkMultiple.txt");
        if (StringUtil.isEmptyOrNull(content)) {
            return;
        }
        content = content.replaceAll("\r\n", "\n");
        String[] allLine = content.split("\n");
        for (int i = 0, len = allLine.length; i < len; ++i) {
            String line = allLine[i];
            String[] cel = line.split("\t");
            SharkMultipleInfo info = new SharkMultipleInfo();
            info.setId(Integer.valueOf(cel[0]));
            info.setMul(Integer.valueOf(cel[1]));
            info.setTypeName(cel[2]);
            info.setRate(Double.valueOf(cel[3]));
            this.list.add(info);
            this.map.put(Integer.valueOf(cel[0]), info);
        }
    }
}
