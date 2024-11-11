package com.xiuxiu.app.server.table;

import java.util.HashMap;

public class TbPai9Manager {
    private static class TbPai9ManagerHolder {
        private static TbPai9Manager instance = new TbPai9Manager();
    }

    public static TbPai9Manager I = new TbPai9Manager();

    private TbPai9 pai9;

    private HashMap<Integer, TbPai9.TbPai9Info> allType = new HashMap<>();

    public void init(TbPai9 pai9) {
        this.pai9 = pai9;
        for (TbPai9.TbPai9Info item : pai9.list) {
            int c1 = item.card1;
            int c2 = item.card2;
            if (c1 > c2) {
                c1 = c1 ^ c2;
                c2 = c1 ^ c2;
                c1 = c1 ^ c2;
            }
            this.allType.put(c1 << 16 | c2, item);
        }
    }

    public TbPai9.TbPai9Info getType(int card1, int card2) {
        int c1 = card1;
        int c2 = card2;
        if (c1 > c2) {
            c1 = c1 ^ c2;
            c2 = c1 ^ c2;
            c1 = c1 ^ c2;
        }
        return this.allType.get(c1 << 16 | c2);
    }
}
