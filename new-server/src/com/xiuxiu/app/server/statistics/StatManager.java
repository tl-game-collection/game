package com.xiuxiu.app.server.statistics;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.db.dao.ILogAccountDAO;
import com.xiuxiu.app.server.db.dao.ILogAccountRemainDAO;
import com.xiuxiu.core.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatManager {
    private static class StatManagerHolder {
        private static StatManager instance = new StatManager();
    }

    public static StatManager I = StatManagerHolder.instance;

    private final int DEFAULT_COUNT_PER_TIME = 100; // 一次同步的记录数
    private final long PLAYER_UID_STARTER = 20000;
    private long lastRegisterUid = 0; // 最后被同步的注册用户的UID
    private long times = 0;

    private StatManager() {
    }

    public int syncOnce() {
        if (this.times++ <= 0 || (this.lastRegisterUid <= 0 && !this.loadLastSynchronizedRecord())) {
            return 0;
        }

        return this.syncRegistered(this.lastRegisterUid + 1, DEFAULT_COUNT_PER_TIME);
    }

    private int syncRegistered(long startUid, int count) {
        int result = 0;
        try {
            List<Account> accounts = DBManager.I.getAccountDao().loadAccountsByUidStartFrom(startUid, count);
            if (accounts.isEmpty()) {
                return 0;
            }

            List<LogAccount> logs = new ArrayList<>(accounts.size());
            long maxUid = this.lastRegisterUid;
            for (Account account : accounts) {
                LogAccount log = new LogAccount();
                log.setUid(UIDManager.I.getAndInc(UIDType.LOG_ACCOUNT));
                log.setTargetUid(account.getUid());
                log.setAction(EAccountAction.REGISTER.code());
                log.setTimestamp(account.getCreateTime() / 1000);
                log.setAccountType(account.getType());
                log.setServerId(0);
                log.setDeviceModel("");
                log.setDeviceSn("");
                log.setOsVersion(account.getPhoneOsVer() != null ? account.getPhoneOsVer() : "");
                log.setAddress(account.getCity() != null ? account.getCity() : "");
                log.setAppVersion("");
                log.setChannelId(0);
                log.setMobileNumber(account.getPhone() != null ? account.getPhone() : "");
                logs.add(log);
                maxUid = Math.max(maxUid, account.getUid());
            }

            DBManager.I.getLogAccountDAO().createMultiple(logs);
            this.lastRegisterUid = Math.max(maxUid, this.lastRegisterUid);
            result = accounts.size();
        } catch (Exception e) {
            Logs.ACCOUNT.warn("同步数据出现异常: %s，等待下次尝试...", e.getMessage());
        }
        return result;
    }

    private boolean loadLastSynchronizedRecord() {
        try {
            ILogAccountDAO dao = DBManager.I.getLogAccountDAO();
            LogAccount record = dao.getLastRecordByAction(EAccountAction.REGISTER.code());
            this.lastRegisterUid = record == null ? PLAYER_UID_STARTER : record.getTargetUid();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Map<String, Object>> getDailyActive(long timeBegin, long timeEnd) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            ILogAccountDAO dao = DBManager.I.getLogAccountDAO();
            result = dao.getDailyActive(timeBegin, timeEnd);
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public List<LogAccountRemain> getDailyLogAccountRemain(long timeBegin, long timeEnd) {
        List<LogAccountRemain> result = new ArrayList<>();
        try {
            ILogAccountRemainDAO dao = DBManager.I.getLogAccountRemainDAO();
            result = dao.load(timeBegin, timeEnd);
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public void calcYesterdayRemind(){
        // 获取今日开始时间戳
        long todayZeroTimestamp = TimeUtil.getZeroTimestampWithToday()/1000;
        // 获取昨日开始时间戳
        long day1ZeroTimestamp = (TimeUtil.getZeroTimestampWithToday() - TimeUtil.ONE_DAY_MS)/1000;
        long day2ZeroTimestamp = (TimeUtil.getZeroTimestampWithToday() - TimeUtil.ONE_DAY_MS*2)/1000;
        long day3ZeroTimestamp = (TimeUtil.getZeroTimestampWithToday() - TimeUtil.ONE_DAY_MS*3)/1000;
        long day4ZeroTimestamp = (TimeUtil.getZeroTimestampWithToday() - TimeUtil.ONE_DAY_MS*4)/1000;
        long day5ZeroTimestamp = (TimeUtil.getZeroTimestampWithToday() - TimeUtil.ONE_DAY_MS*5)/1000;
        long day6ZeroTimestamp = (TimeUtil.getZeroTimestampWithToday() - TimeUtil.ONE_DAY_MS*6)/1000;
        long day7ZeroTimestamp = (TimeUtil.getZeroTimestampWithToday() - TimeUtil.ONE_DAY_MS*7)/1000;
        long day8ZeroTimestamp = (TimeUtil.getZeroTimestampWithToday() - TimeUtil.ONE_DAY_MS*8)/1000;
        long day13ZeroTimestamp = (TimeUtil.getZeroTimestampWithToday() - TimeUtil.ONE_DAY_MS*13)/1000;
        long day14ZeroTimestamp = (TimeUtil.getZeroTimestampWithToday() - TimeUtil.ONE_DAY_MS*14)/1000;
        long day30ZeroTimestamp = (TimeUtil.getZeroTimestampWithToday() - TimeUtil.ONE_MONTH_MS)/1000;
        // 获取昨日注册Ids
        try {
            ILogAccountDAO dao = DBManager.I.getLogAccountDAO();
            List<Long> ids = dao.loadYesterdayRemain(day1ZeroTimestamp, todayZeroTimestamp);
            int registerNum = dao.loadRegisterNumByTime(day1ZeroTimestamp, todayZeroTimestamp);
            int day_2 = dao.loadLoginByTimeAndTargetUids(day3ZeroTimestamp, day2ZeroTimestamp, ids);
            int day_3 = dao.loadLoginByTimeAndTargetUids(day4ZeroTimestamp, day3ZeroTimestamp, ids);
            int day_4 = dao.loadLoginByTimeAndTargetUids(day5ZeroTimestamp, day4ZeroTimestamp, ids);
            int day_5 = dao.loadLoginByTimeAndTargetUids(day6ZeroTimestamp, day5ZeroTimestamp, ids);
            int day_6 = dao.loadLoginByTimeAndTargetUids(day7ZeroTimestamp, day6ZeroTimestamp, ids);
            int day_7 = dao.loadLoginByTimeAndTargetUids(day8ZeroTimestamp, day7ZeroTimestamp, ids);
            int day_14 = dao.loadLoginByTimeAndTargetUids(day14ZeroTimestamp, day13ZeroTimestamp, ids);
            int day_30 = dao.loadLoginByTimeAndTargetUids(day30ZeroTimestamp, day1ZeroTimestamp, ids);
            LogAccountRemain log = new LogAccountRemain();
            log.setUid(UIDManager.I.getAndInc(UIDType.LOG_ACCOUNT_REMAIN));
            log.setDate(day1ZeroTimestamp);
            log.setRegisterNum(registerNum);
            log.setDay_2(day_2);
            log.setDay_3(day_3);
            log.setDay_4(day_4);
            log.setDay_5(day_5);
            log.setDay_6(day_6);
            log.setDay_7(day_7);
            log.setDay_14(day_14);
            log.setDay_30(day_30);
            DBManager.I.getLogAccountRemainDAO().save(log);
        } catch (Exception e) {

        }
    }

}
