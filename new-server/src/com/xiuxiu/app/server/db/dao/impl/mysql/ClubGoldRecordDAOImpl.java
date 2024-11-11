package com.xiuxiu.app.server.db.dao.impl.mysql;

import java.util.Collections;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubGoldRecord;
import com.xiuxiu.app.server.db.dao.IClubGoldRecordDAO;
import com.xiuxiu.app.server.db.dao.IClubGoldRecordMapper;

public class ClubGoldRecordDAOImpl implements IClubGoldRecordDAO {
    private final SqlSessionFactory factory;

    public ClubGoldRecordDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean save(ClubGoldRecord info) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubGoldRecordMapper mapper = session.getMapper(IClubGoldRecordMapper.class);
            if (1 == mapper.create(info)) {
                return true;
            }
        } catch (Exception e) {
            Logs.DB.error("保存星币记录失败 ClubGoldRecord:%s", e, info);
        } finally {

        }
        return false;
    }

    @Override
    public ClubGoldRecord load(long uid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubGoldRecordMapper mapper = session.getMapper(IClubGoldRecordMapper.class);
            ClubGoldRecord record = mapper.load(uid);
            return record;
        } catch (Exception e) {
            Logs.DB.error("根据ID查询星币记录失败 uid:%d", e, uid);
        } finally {

        }
        return null;
    }

//    @Override
//    public int loadClubGoldRecordCounInMoney(long leagueUid, int action) {
//        try (SqlSession session = this.factory.openSession(true)) {
//            IClubGoldRecordMapper mapper = session.getMapper(IClubGoldRecordMapper.class);
//            Integer value = mapper.loadClubGoldRecordCounInMoney(leagueUid, action);
//            return null == value ? 0 : value;
//        } catch (Exception e) {
//            Logs.DB.error("根据玩家ID查询星币记录失败 uid:%d ", e, leagueUid);
//        } finally {
//
//        }
//        return 0;
//    }

    @Override
    public List<ClubGoldRecord> loadClubGoldRecordByClubUid(long clubUid, int action, int begin, int pageSize) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubGoldRecordMapper mapper = session.getMapper(IClubGoldRecordMapper.class);
            List<ClubGoldRecord> list = mapper.loadClubGoldRecordByClubUid(clubUid, action, begin, pageSize);
            return null == list ? Collections.EMPTY_LIST : list;
        } catch (Exception e) {
            Logs.DB.error("根据玩家ID查询星币记录失败 uid:%d", e, clubUid);
        } finally {

        }
        return null;
    }

    @Override
    public List<ClubGoldRecord> loadClubGoldRecordByClubUidAndTime(long clubUid, int action, long time, int begin, int pageSize) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubGoldRecordMapper mapper = session.getMapper(IClubGoldRecordMapper.class);
            List<ClubGoldRecord> list = mapper.loadClubGoldRecordByClubUidAndTime(clubUid, action, time, begin, pageSize);
            return null == list ? Collections.EMPTY_LIST : list;
        } catch (Exception e) {
            Logs.DB.error("根据玩家ID查询星币记录失败 uid:%d", e, clubUid);
        } finally {

        }
        return null;
    }

    @Override
    public List<ClubGoldRecord> loadSetGoldRecord(long clubUid, long playerUid, int begin, int pageSize, long minTime) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubGoldRecordMapper mapper = session.getMapper(IClubGoldRecordMapper.class);
            List<ClubGoldRecord> list = mapper.loadSetGoldRecord(clubUid, playerUid, begin, pageSize,minTime);
            return null == list ? Collections.EMPTY_LIST : list;
        } catch (Exception e) {
            Logs.DB.error("根据玩家ID分页查询星币记录失败 uid:%d", e, playerUid);
        } finally {

        }
        return null;
    }
}
