package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.mail.Mail;

import java.util.List;

public interface MailDAO {
    boolean create(Mail mail);
    List<Mail> loadByPlayerUid(long playerUid);
    boolean save(Mail mail);
    boolean saveAll(List<Mail> mail);
}
