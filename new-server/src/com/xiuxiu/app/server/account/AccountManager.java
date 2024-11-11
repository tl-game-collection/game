package com.xiuxiu.app.server.account;

import com.xiuxiu.app.server.BaseManager;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.db.IDBLoad;
import com.xiuxiu.app.server.db.dao.IBaseDAO;
import com.xiuxiu.core.utils.NumberUtils;
import com.xiuxiu.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AccountManager extends BaseManager {
	public static AccountManager I = AccountManagerHolder.INSTANCE;

	private static class AccountManagerHolder {
		private static final AccountManager INSTANCE = new AccountManager();
	}

	transient private List<AccountUid> accountUidMakers = new ArrayList<>();

	private AccountManager() {
	}

	public void init(){
		loadAccountUidMaker();
	}

	public Account create(byte type, String phone, String passwd, String mac, String phoneVer, String phoneOsVer) {
		return this.create(type, phone, passwd, mac, phoneVer, phoneOsVer, "");
	}

	public Account create(byte type, String phone, String passwd, String mac, String phoneVer, String phoneOsVer, String platformToken) {
		return this.create(type, phone, passwd, mac, phoneVer, phoneOsVer, platformToken, "", "", (byte) 1, "",0);
	}

	public Account create(byte type, String phone, String passwd, String mac, String phoneVer, String phoneOsVer, String platformToken,
			String name, String icon, byte sex, String city, long accountUid) {
		Account account = new Account();
		accountUid = makeAccountUid(accountUid);
		if ( 0 >= accountUid) {
			return null;
		}
		account.setUid(accountUid);
		account.setCreateTime(System.currentTimeMillis());
		account.setPhone(phone);
		account.setPasswd(passwd);
		account.setMac(mac);
		account.setPhoneVer(phoneVer);
		account.setPhoneOsVer(phoneOsVer);
		account.setOtherPlatformToken(platformToken);
		account.setName(name);
		account.setIcon(icon);
		account.setSex(sex);
		account.setCity(city);
		account.setType(type);
		account.setDirty(true);
		if (DBManager.I.update(account)) {
			return account;
		}
		return null;
	}

	public Account getAccountByPhone(String phone) {
		try {
			return DBManager.I.load(ETableType.TB_ACCOUNT, new IDBLoad<Account>() {
				@Override
				public String getRedisKey() {
					return ETableType.TB_ACCOUNT_PHONE.getRedisKey() + phone;
				}

				@Override
				public Account loadOne(IBaseDAO<Account> dao) {
					return DBManager.I.getAccountDao().getAccountByPhone(phone);
				}
			});
		} catch (Exception e) {
			Logs.ACCOUNT.error(e);
		}
		return null;
	}

	public Account getAccountByUid(long accountUid) {
		try {
			return DBManager.I.load(accountUid, ETableType.TB_ACCOUNT);
		} catch (Exception e) {
			Logs.ACCOUNT.error(e);
		}
		return null;
	}

	public Account getAccountByOtherPlatformToken(String platformToken) {
		try {
			return DBManager.I.load(ETableType.TB_ACCOUNT, new IDBLoad<Account>() {
				@Override
				public String getRedisKey() {
					return ETableType.TB_ACCOUNT_TOKEN.getRedisKey() + platformToken;
				}

				@Override
				public Account loadOne(IBaseDAO<Account> dao) {
					return DBManager.I.getAccountDao().getAccountByOtherPlatformToken(platformToken);
				}
			});
		} catch (Exception e) {
			Logs.ACCOUNT.error(e);
		}
		return null;
	}

	public boolean updateOtherPlatformToken(long accountUid, String newOtherPlatformToken) {
		Account account = DBManager.I.load(accountUid, ETableType.TB_ACCOUNT);
		if (null != account && 3 == account.getType()) {
			String oldOtherPlatformToken = account.getOtherPlatformToken();
			account.setOtherPlatformToken(newOtherPlatformToken);
			account.setDirty(true);
			List<String> keys = new ArrayList<>();
			keys.add(ETableType.TB_ACCOUNT.getRedisKey() + account.getUid());
			if (!StringUtil.isEmptyOrNull(account.getPhone())) {
				keys.add(ETableType.TB_ACCOUNT_PHONE.getRedisKey() + account.getPhone());
			}
			if (!StringUtil.isEmptyOrNull(account.getOtherPlatformToken())) {
				keys.add(ETableType.TB_ACCOUNT_TOKEN.getRedisKey() + account.getOtherPlatformToken());
				keys.add(ETableType.TB_ACCOUNT_TOKEN.getRedisKey() + oldOtherPlatformToken);
			}
			if (!DBManager.I.update(account, keys)) {
				account.setOtherPlatformToken(oldOtherPlatformToken);
				account.setDirty(false);
				return false;
			}
			return true;
		}
		return false;
	}

	public boolean bindPhone(long accountUid, String newPhone) {
		Account account = DBManager.I.load(accountUid, ETableType.TB_ACCOUNT);
		if (null != account) {
			String oldPhone = account.getPhone();
			account.setPhone(newPhone);
			account.setDirty(true);
			List<String> keys = new ArrayList<>();
			keys.add(ETableType.TB_ACCOUNT.getRedisKey() + account.getUid());
			if (!StringUtil.isEmptyOrNull(account.getPhone())) {
				keys.add(ETableType.TB_ACCOUNT_PHONE.getRedisKey() + account.getPhone());
			}
			if (!StringUtil.isEmptyOrNull(account.getOtherPlatformToken())) {
				keys.add(ETableType.TB_ACCOUNT_TOKEN.getRedisKey() + account.getOtherPlatformToken());
			}
			if (!DBManager.I.update(account, keys)) {
				account.setPhone(oldPhone);
				account.setDirty(false);
				return false;
			}else {
				if (!StringUtil.isEmptyOrNull(oldPhone)) {
					DBManager.I.setExpire(account, ETableType.TB_ACCOUNT_PHONE.getRedisKey() + oldPhone, 1, TimeUnit.SECONDS);
				}
			}
			return true;
		}
		return false;
	}

	public boolean updatePasswdByUid(long accountUid, String newPasswd) {
		Account account = DBManager.I.load(accountUid, ETableType.TB_ACCOUNT);
		if (null != account) {
			String oldPasswd = account.getPasswd();
			account.setPasswd(newPasswd);
			account.setDirty(true);
			List<String> keys = new ArrayList<>();
			keys.add(ETableType.TB_ACCOUNT.getRedisKey() + account.getUid());
			if (!StringUtil.isEmptyOrNull(account.getPhone())) {
				keys.add(ETableType.TB_ACCOUNT_PHONE.getRedisKey() + account.getPhone());
			}
			if (!StringUtil.isEmptyOrNull(account.getOtherPlatformToken())) {
				keys.add(ETableType.TB_ACCOUNT_TOKEN.getRedisKey() + account.getOtherPlatformToken());
			}
			if (!DBManager.I.update(account, keys)) {
				account.setPasswd(oldPasswd);
				account.setDirty(false);
				return false;
			}
			return true;
		}
		return false;
	}

	public boolean updatePayPasswdByUid(long accountUid, String newPayPasswd) {
		Account account = DBManager.I.load(accountUid, ETableType.TB_ACCOUNT);
		if (null != account) {
			String oldPayPasswd = account.getPayPassword();
			account.setPayPassword(newPayPasswd);
			account.setDirty(true);
			List<String> keys = new ArrayList<>();
			keys.add(ETableType.TB_ACCOUNT.getRedisKey() + account.getUid());
			if (!StringUtil.isEmptyOrNull(account.getPhone())) {
				keys.add(ETableType.TB_ACCOUNT_PHONE.getRedisKey() + account.getPhone());
			}
			if (!StringUtil.isEmptyOrNull(account.getOtherPlatformToken())) {
				keys.add(ETableType.TB_ACCOUNT_TOKEN.getRedisKey() + account.getOtherPlatformToken());
			}
			if (!DBManager.I.update(account, keys)) {
				account.setPayPassword(oldPayPasswd);
				account.setDirty(false);
				return false;
			}
			return true;
		}
		return false;
	}

	public boolean updateRealNameByUid(long accountUid, String name, String identityCard) {
		Account account = DBManager.I.load(accountUid, ETableType.TB_ACCOUNT);
		if (null != account) {
			String oldName = account.getName();
			String oldId = account.getIdentityCard();
			account.setName(name);
			account.setIdentityCard(identityCard);
			account.setDirty(true);
			List<String> keys = new ArrayList<>();
			keys.add(ETableType.TB_ACCOUNT.getRedisKey() + account.getUid());
			if (!StringUtil.isEmptyOrNull(account.getPhone())) {
				keys.add(ETableType.TB_ACCOUNT_PHONE.getRedisKey() + account.getPhone());
			}
			if (!StringUtil.isEmptyOrNull(account.getOtherPlatformToken())) {
				keys.add(ETableType.TB_ACCOUNT_TOKEN.getRedisKey() + account.getOtherPlatformToken());
			}
			if (!DBManager.I.update(account, keys)) {
				account.setName(oldName);
				account.setIdentityCard(oldId);
				account.setDirty(false);
				return false;
			}
			return true;
		}
		return false;
	}

	public boolean ban(long accountUid, boolean ban) {
		Account account = DBManager.I.load(accountUid, ETableType.TB_ACCOUNT);
		if (null != account) {
			int oldState = account.getState();
			if (ban) {
				if (0 != account.getState()) {
					return false;
				}
				account.setState(2);
			} else {
				if (2 != account.getState()) {
					return false;
				}
				account.setState(0);
			}
			account.setDirty(true);
			List<String> keys = new ArrayList<>();
			keys.add(ETableType.TB_ACCOUNT.getRedisKey() + account.getUid());
			if (!StringUtil.isEmptyOrNull(account.getPhone())) {
				keys.add(ETableType.TB_ACCOUNT_PHONE.getRedisKey() + account.getPhone());
			}
			if (!StringUtil.isEmptyOrNull(account.getOtherPlatformToken())) {
				keys.add(ETableType.TB_ACCOUNT_TOKEN.getRedisKey() + account.getOtherPlatformToken());
			}
			if (!DBManager.I.update(account, keys)) {
				account.setState(oldState);
				account.setDirty(false);
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * 更新钱包免密支付状态
	 * 
	 * @param accountUid
	 *            账号UID
	 * @param noNeedPayPassword
	 *            免密支付状态 0: 关闭, 1: 开启
	 * @return boolean
	 */
	public boolean updateNoNeedPayPasswdByUid(long accountUid, int noNeedPayPassword) {
		Account account = DBManager.I.load(accountUid, ETableType.TB_ACCOUNT);
		if (null != account) {
			int oldNoNeedPassword = account.getNoNeedPayPassword();
			account.setNoNeedPayPassword(noNeedPayPassword);
			account.setDirty(true);
			List<String> keys = new ArrayList<>();
			keys.add(ETableType.TB_ACCOUNT.getRedisKey() + account.getUid());
			if (!StringUtil.isEmptyOrNull(account.getPhone())) {
				keys.add(ETableType.TB_ACCOUNT_PHONE.getRedisKey() + account.getPhone());
			}
			if (!StringUtil.isEmptyOrNull(account.getOtherPlatformToken())) {
				keys.add(ETableType.TB_ACCOUNT_TOKEN.getRedisKey() + account.getOtherPlatformToken());
			}
			if (!DBManager.I.update(account, keys)) {
				account.setNoNeedPayPassword(oldNoNeedPassword);
				account.setDirty(false);
				return false;
			}
			return true;
		}
		return false;
	}

	private long makeAccountUid(long defaultAccountUid){
		if (defaultAccountUid > 0){
			AccountUid accountUidMaker = new AccountUid();
			accountUidMaker.setUid(defaultAccountUid);
			accountUidMaker.setGood(NumberUtils.isGoodNumber(defaultAccountUid) ? 1 : 0);
			accountUidMaker.setState(0);
			accountUidMaker.setDirty(true);
			accountUidMaker.save();
			return defaultAccountUid;
		}
		synchronized (this.accountUidMakers){
			int size = accountUidMakers.size();
			if (size <= 0){
				loadAccountUidMaker();
			}
			size = accountUidMakers.size();
			if (size <= 0){
				return 0;
			}
			AccountUid accountUidMaker = this.accountUidMakers.remove(size-1);
			accountUidMaker.setState(0);
			accountUidMaker.setDirty(true);
			accountUidMaker.save();
			return accountUidMaker.getUid();
		}
	}

	private void loadAccountUidMaker(){
		accountUidMakers = DBManager.I.getAccountUidDao().loadUnused(false,10000);
		if (null == accountUidMakers){
			accountUidMakers = new ArrayList<>();
		}
	}

	@Override
	public int save() {
		return 0;
	}

	@Override
	public int shutdown() {
		return 0;
	}
}
