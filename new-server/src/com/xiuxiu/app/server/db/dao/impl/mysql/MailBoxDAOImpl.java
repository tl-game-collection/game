package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.chat.MailBox;
import com.xiuxiu.app.server.chat.MailBoxUid;
import com.xiuxiu.app.server.db.dao.IMailBoxDAO;
import com.xiuxiu.app.server.db.dao.IMailBoxMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class MailBoxDAOImpl implements IMailBoxDAO {
    private SqlSessionFactory factory;

    public MailBoxDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public MailBoxUid getLastMsgUidByPlayerUid(long playerUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMailBoxMapper mapper = session.getMapper(IMailBoxMapper.class);
            return mapper.loadMailBoxUidByPlayerUid(playerUid);
        } catch (Exception e) {
            Logs.DB.error("根据玩家uid加载邮件uid消息失败 playerUid:%d", e, playerUid);
        } finally {

        }
        return null;
    }

    @Override
    public boolean saveMailBoxUid(MailBoxUid mailBoxUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMailBoxMapper mapper = session.getMapper(IMailBoxMapper.class);
            if (1 != mapper.updateMailBoxUid(mailBoxUid)) {
                return 1 == mapper.createMailBoxUid(mailBoxUid);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("根据保存邮件uid消息失败 mailBoxUid:%s", e, mailBoxUid);
        } finally {

        }
        return false;
    }

    @Override
    public MailBox load(long uid) {
        return null;
    }

    @Override
    public boolean save(MailBox mailBox) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMailBoxMapper mapper = session.getMapper(IMailBoxMapper.class);
            if (1 != mapper.updateMailBox(mailBox)) {
                return 1 == mapper.createMailBox(mailBox);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("根据保存邮件失败 mailBox:%s", e, mailBox);
        } finally {

        }
        return false;
    }

    @Override
    public List<MailBox> loadByMessageUid(long messageUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMailBoxMapper mapper = session.getMapper(IMailBoxMapper.class);
            return mapper.loadByMessageUid(messageUid);
        } catch (Exception e) {
            Logs.DB.error("根据消息uid加载邮件列表失败 messageUid:%d", e, messageUid);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<MailBox> loadByPlayerUidWithBeginUidAndEndUid(long playerUid, long beginMsgUid, long endMsgUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMailBoxMapper mapper = session.getMapper(IMailBoxMapper.class);
            return mapper.loadByPlayerUidWithBeginAndEnd(playerUid, beginMsgUid, endMsgUid);
        } catch (Exception e) {
            Logs.DB.error("根据玩家uid和消息uid范围加载邮件列表失败 playerUid:%d beginMsgUid:%s endMsgUid:%d", e, playerUid, beginMsgUid, endMsgUid);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<MailBox> loadByPlayerUidAndMsgUids(long playerUid, List<Long> msgUidList) {
        if (null == msgUidList || msgUidList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        try (SqlSession session = this.factory.openSession(true)) {
            IMailBoxMapper mapper = session.getMapper(IMailBoxMapper.class);
            return mapper.loadByPlayerUidAndMsgUids(playerUid, msgUidList);
        } catch (Exception e) {
            Logs.DB.error("根据玩家uid和消息列表加载邮件列表失败 playerUid:%d msgUidList:%s", e, playerUid, msgUidList);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public MailBox loadByPlayerUidAndMsgUid(long playerUid, long msgUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMailBoxMapper mapper = session.getMapper(IMailBoxMapper.class);
            return mapper.loadByPlayerUidAndMsgUid(playerUid, msgUid);
        } catch (Exception e) {
            Logs.DB.error("根据玩家uid和消息id加载邮件列表失败 playerUid:%d msgUid:%d", e, playerUid, msgUid);
        } finally {

        }
        return null;
    }

}
