package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.account.AccountUid;
import com.xiuxiu.app.server.db.dao.IAccountUidDAO;
import com.xiuxiu.app.server.db.dao.IAccountUidMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class AccountUidDAOImpl implements IAccountUidDAO {
    private final SqlSessionFactory factory;

    public AccountUidDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public AccountUid load(long uid) {
        return null;
    }

    @Override
    public boolean save(AccountUid accountUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IAccountUidMapper mapper = session.getMapper(IAccountUidMapper.class);
            if (1 != mapper.update(accountUid)) {
                return 1 == mapper.create(accountUid);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存账号uid失败 accountUid:%s", e, accountUid);
        } finally {

        }
        return false;
    }

    @Override
    public List<AccountUid> loadUnused(boolean good, int cnt) {
        try (SqlSession session = this.factory.openSession(true)) {
            IAccountUidMapper mapper = session.getMapper(IAccountUidMapper.class);
            return mapper.loadUnused(good ? 1 : 0,cnt);
        } catch (Exception e) {
            Logs.DB.error("获取AccountUid失败 good:%s", e, good);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }
}
