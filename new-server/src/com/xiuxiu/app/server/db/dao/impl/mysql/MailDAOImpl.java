package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.MailDAO;
import com.xiuxiu.app.server.db.dao.MailMapper;
import com.xiuxiu.app.server.mail.Mail;
import com.xiuxiu.core.utils.TimeUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class MailDAOImpl implements MailDAO {
    private SqlSessionFactory factory;

    public MailDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean create(Mail mail) {
        try (SqlSession session = this.factory.openSession(true)) {
            MailMapper mapper = session.getMapper(MailMapper.class);
            return 1 == mapper.create(mail);
        } catch (Exception e) {
            Logs.DB.error("创建邮件信息失败 mail:%s", e, mail);
        } finally {

        }
        return false;
    }

    @Override
    public List<Mail> loadByPlayerUid(long playerUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            long time = System.currentTimeMillis() - TimeUtil.ONE_MONTH_MS;
            MailMapper mapper = session.getMapper(MailMapper.class);
            return mapper.loadByPlayerUid(playerUid, time);
        } catch (Exception e) {
            Logs.DB.error("根据玩家uid加载邮件信息失败 playerUid:%d", e, playerUid);
        } finally {

        }
        return null;
    }

    @Override
    public boolean save(Mail mail) {
        try (SqlSession session = this.factory.openSession(true)) {
            MailMapper mapper = session.getMapper(MailMapper.class);
            if (1 != mapper.save(mail)) {
                return 1 == mapper.create(mail);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("创建邮件信息失败 mail:%s", e, mail);
        } finally {

        }
        return false;
    }

    @Override
    public boolean saveAll(List<Mail> mail) {
        return false;
    }
}
