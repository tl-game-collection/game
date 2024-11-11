package com.xiuxiu.app.server.db.dao.impl.mysql;

import java.util.Collections;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.activity.gold.ClubActivityGoldRewardRecord;
import com.xiuxiu.app.server.db.dao.IClubActivityGoldRewardRecordDAO;
import com.xiuxiu.app.server.db.dao.IClubActivityGoldRewardRecordMapper;

public class ClubActivityGoldRewardRecordDAOImpl implements IClubActivityGoldRewardRecordDAO {
    private final SqlSessionFactory factory;

    public ClubActivityGoldRewardRecordDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public ClubActivityGoldRewardRecord load(long uid) {
        return null;
    }

    @Override
    public boolean save(ClubActivityGoldRewardRecord value) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubActivityGoldRewardRecordMapper mapper = session.getMapper(IClubActivityGoldRewardRecordMapper.class);
            if (1 != mapper.createGroupValueRecord(value)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存竞技值操作记录失败 arenaValueRecordInfo:%s", e, value);
        } finally {

        }
        return false;
    }

    @Override
    public int loadCountGold(long clubUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubActivityGoldRewardRecordMapper mapper = session.getMapper(IClubActivityGoldRewardRecordMapper.class);
            Integer value = mapper.loadCountGold(clubUid);
            return null == value ? 0 : value;
        } catch (Exception e) {
            Logs.DB.error("根据玩家ID查询星币记录失败 uid:%d ", e, clubUid);
        } finally {

        }
        return 0;
    }

    @Override
    public List<ClubActivityGoldRewardRecord> loadByClubUid(long clubUid, int begin, int pageSize) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubActivityGoldRewardRecordMapper mapper = session.getMapper(IClubActivityGoldRewardRecordMapper.class);
            return mapper.loadByClubUid(clubUid, begin, pageSize);
        } catch (Exception e) {
            Logs.DB.error("根据群Uid,操作的玩家uid获取任务操作记录信息失败 ex: clubUid:%d", e, clubUid);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<ClubActivityGoldRewardRecord> loadByClubUidAndBoxUid(long clubUid, long boxUid, int begin, int pageSize) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubActivityGoldRewardRecordMapper mapper = session.getMapper(IClubActivityGoldRewardRecordMapper.class);
            return mapper.loadByClubUidAndBoxUid(clubUid, boxUid, begin, pageSize);
        } catch (Exception e) {
            Logs.DB.error("根据群Uid,操作的玩家uid分页获取竞技值操作记录信息失败 clubUid:%d, boxUid:%d ", clubUid, boxUid);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<ClubActivityGoldRewardRecord> loadByClubUidAndBoxUidAndStartTimeAndEndTime(long clubUid, long boxUid,long startTime, long endTime, int begin, int pageSize) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubActivityGoldRewardRecordMapper mapper = session.getMapper(IClubActivityGoldRewardRecordMapper.class);
            return mapper.loadByClubUidAndBoxUidAndStartTimeAndEndTime(clubUid, boxUid, startTime, endTime, begin, pageSize);
        } catch (Exception e) {
            Logs.DB.error("根据群Uid,操作的玩家uid分页获取竞技值操作记录信息失败 clubUid:%d, boxUid:%d, startTime:%d, endTime:%d", e, clubUid, boxUid, startTime, endTime);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }


}
