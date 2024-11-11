package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.chat.MailBox;
import com.xiuxiu.app.server.chat.MailBoxUid;

import java.util.List;

public interface IMailBoxDAO extends IBaseDAO<MailBox> {
    /**
     * 获取最后一条消息uid
     * @param playerUid
     * @return
     */
    MailBoxUid getLastMsgUidByPlayerUid(long playerUid);

    /**
     * 保存邮件uid消息
     * @param mailBoxUid
     * @return
     */
    boolean saveMailBoxUid(MailBoxUid mailBoxUid);

    /**
     * 根据消息uid获取邮件
     * @param messageUid
     * @return
     */
    List<MailBox> loadByMessageUid(long messageUid);

    /**
     * 根据玩家uid和消息uid范围获取邮件
     * @param playerUid
     * @param beginMsgUid
     * @param endMsgUid
     * @return
     */
    List<MailBox> loadByPlayerUidWithBeginUidAndEndUid(long playerUid, long beginMsgUid, long endMsgUid);

    /**
     * 根据玩家uid和消息uid列表获取邮件
     * @param playerUid
     * @param msgUidList
     * @return
     */
    List<MailBox> loadByPlayerUidAndMsgUids(long playerUid, List<Long> msgUidList);

    /**
     * 根据玩家uid和消息uid获取邮件
     * @param playerUid
     * @param msgUid
     * @return
     */
    MailBox loadByPlayerUidAndMsgUid(long playerUid, long msgUid);

}
