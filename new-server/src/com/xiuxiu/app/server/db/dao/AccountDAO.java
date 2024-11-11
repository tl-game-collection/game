package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.account.Account;

import java.util.List;

public interface AccountDAO extends IBaseDAO<Account> {
    Account getAccountByPhone(String phone);
    Account getAccountByOtherPlatformToken(String platformToken);
    int savePayPasswd(Account account);
    List<Account> loadAccountsByUidStartFrom(long uid, int limitSize);
    boolean banAccount(long uid, boolean ban);
    boolean update(Account account);
}
