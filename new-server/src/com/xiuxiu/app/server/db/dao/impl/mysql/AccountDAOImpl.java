package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.db.dao.AccountDAO;
import com.xiuxiu.app.server.db.dao.AccountMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class AccountDAOImpl implements AccountDAO {
    private final SqlSessionFactory factory;

    public AccountDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public Account load(long accountUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            AccountMapper mapper = session.getMapper(AccountMapper.class);
            return mapper.getAccountByUid(accountUid);
        } catch (Exception e) {
            Logs.DB.error("根据账号Uid获取账号信息失败 accountUid:%s", e, accountUid);
        } finally {

        }
        return null;
    }

    @Override
    public boolean save(Account account) {
        try (SqlSession session = this.factory.openSession(true)) {
            AccountMapper mapper = session.getMapper(AccountMapper.class);
            if (1 != mapper.save(account)) {
                return 1 == mapper.create(account);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存账号信息失败 account:%s", e, account);
        } finally {

        }
        return false;
    }

    @Override
    public Account getAccountByPhone(String phone) {
        try (SqlSession session = this.factory.openSession(true)) {
            AccountMapper mapper = session.getMapper(AccountMapper.class);
            return mapper.getAccountByPhone(phone);
        } catch (Exception e) {
            Logs.DB.error("根据手机号获取账号信息失败 phone:%s", e, phone);
        } finally {

        }
        return null;
    }

    @Override
    public Account getAccountByOtherPlatformToken(String platformToken) {
        try (SqlSession session = this.factory.openSession(true)) {
            AccountMapper mapper = session.getMapper(AccountMapper.class);
            return mapper.getAccountByOtherPlatformToken(platformToken);
        } catch (Exception e) {
            Logs.DB.error("根据其他平台标识获取账号信息失败 token:%s", e, platformToken);
        } finally {

        }
        return null;
    }

    @Override
    public int savePayPasswd(Account account) {
        try (SqlSession session = this.factory.openSession(true)) {
            AccountMapper mapper = session.getMapper(AccountMapper.class);
            return mapper.savePayPasswd(account);
        } catch (Exception e) {
            Logs.DB.error("修改支付密码失败 token:%s", e, account);
        } finally {

        }
        return 0;
    }

    @Override
    public List<Account> loadAccountsByUidStartFrom(long uid, int limitSize) {
        try (SqlSession session = this.factory.openSession(true)) {
            AccountMapper mapper = session.getMapper(AccountMapper.class);
            return mapper.loadAccountsByUidStartFrom(uid, limitSize);
        } catch (Exception e) {
            Logs.DB.error("获取账号信息失败", e);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean banAccount(long uid, boolean ban) {
        try (SqlSession session = this.factory.openSession(true)) {
            AccountMapper mapper = session.getMapper(AccountMapper.class);
            if (ban) {
                return 1 == mapper.banAccount(uid);
            } else {
                return 1 == mapper.unbanAccount(uid);
            }
        } catch (Exception e) {
            Logs.DB.error("封号/解除封号失败 uid:%d", e, uid);
        } finally {

        }
        return false;
    }

    @Override
    public boolean update(Account account) {
        try (SqlSession session = this.factory.openSession(true)) {
            AccountMapper mapper = session.getMapper(AccountMapper.class);
            mapper.update(account);
            return true;
        } catch (Exception e) {
            Logs.DB.error("更新账号信息失败 account:%s", e, account);
        } finally {

        }
        return false;
    }

}
