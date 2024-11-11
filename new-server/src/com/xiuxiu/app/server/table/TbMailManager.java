package com.xiuxiu.app.server.table;

import com.xiuxiu.app.server.mail.Mail;
import com.xiuxiu.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TbMailManager {
    private static class TbMailManagerHolder {
        private static TbMailManager instance = new TbMailManager();
    }

    public static TbMailManager I = TbMailManagerHolder.instance;

    private TbMail tbMail;

    private List<TbMail.TbMailInfo> allFirstMail = new ArrayList<>();

    private TbMailManager() {
    }

    public void init(TbMail tbMail) {
        this.tbMail = tbMail;

        for (TbMail.TbMailInfo item : this.tbMail.list) {
            if (1 == item.getType()) {
                this.allFirstMail.add(item);
            }
        }
    }

    public List<Mail> getFirstMail() {
        List<Mail> list = new ArrayList<>();
        for (TbMail.TbMailInfo info : this.allFirstMail) {
            Mail mail = new Mail();
            mail.setSenderPlayerUid(-1);
            mail.setTitle(info.getTitle());
            mail.setContent(info.getContent());
            mail.setState(0);
            mail.setSendTime(System.currentTimeMillis());
            if (!StringUtil.isEmptyOrNull(info.getItem())) {
                String[] items = info.getItem().split(";");
                for (int i = 0, len = items.length; i < len; ++i) {
                    String[] item = items[i].split(",");
                    if (2 != item.length) {
                        continue;
                    }
                    mail.getItem().put(Integer.valueOf(item[0]), Integer.valueOf(item[1]));
                }
            }

            mail.setItemState(1);
            list.add(mail);
        }
        return list;
    }
}
