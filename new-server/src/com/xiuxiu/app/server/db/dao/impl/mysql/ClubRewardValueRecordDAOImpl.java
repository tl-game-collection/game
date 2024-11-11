package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.IClubRewardValueRecordDAO;
import com.xiuxiu.app.server.db.dao.IClubRewardValueRecordMapper;
import com.xiuxiu.app.server.club.ClubRewardValueRecord;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class ClubRewardValueRecordDAOImpl implements IClubRewardValueRecordDAO {
    private final SqlSessionFactory factory;

    public ClubRewardValueRecordDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean save(ClubRewardValueRecord info) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubRewardValueRecordMapper mapper = session.getMapper(IClubRewardValueRecordMapper.class);
            if (1 == mapper.create(info)) {
                return true;
            }
        } catch (Exception e) {
            Logs.DB.error("保存奖励分记录失败 leagueRecord:%s", e, info);
        } finally {

        }
        return false;
    }

    @Override
    public ClubRewardValueRecord load(long uid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubRewardValueRecordMapper mapper = session.getMapper(IClubRewardValueRecordMapper.class);
            ClubRewardValueRecord record = mapper.load(uid);
            return record;
        } catch (Exception e) {
            Logs.DB.error("根据ID查询奖励分记录失败 uid:%d", e, uid);
        } finally {

        }
        return null;
    }

    @Override
    public List<ClubRewardValueRecord> loadByPage(long clubUid, long playerUid, long time, int begin, int pageSize) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubRewardValueRecordMapper mapper = session.getMapper(IClubRewardValueRecordMapper.class);
            List<ClubRewardValueRecord> list = mapper.loadByPage(clubUid, playerUid, time,begin, pageSize);
            return null == list ? Collections.EMPTY_LIST : list;
        } catch (Exception e) {
            Logs.DB.error("根据玩家ID分页查询奖励分记录失败 uid:%d", e, clubUid);
        } finally {

        }
        return null;
    }
    @Override
    public List<ClubRewardValueRecord> loadByClubUid(long clubUid, long playerUid,int begin, int pageSize) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubRewardValueRecordMapper mapper = session.getMapper(IClubRewardValueRecordMapper.class);
            List<ClubRewardValueRecord> list = mapper.loadByClubUid(clubUid,playerUid, begin, pageSize);
            return null == list ? Collections.EMPTY_LIST : list;
        } catch (Exception e) {
            Logs.DB.error("根据玩家ID分页查询奖励分记录失败 uid:%d", e, clubUid);
        } finally {

        }
        return null;
    }
    
    @Override
    public List<ClubRewardValueRecord> loadDayDetails(long optTime) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubRewardValueRecordMapper mapper = session.getMapper(IClubRewardValueRecordMapper.class);
            List<ClubRewardValueRecord> list = mapper.loadDayDetails(optTime);
            return null == list ? Collections.EMPTY_LIST : list;
        } catch (Exception e) {
            Logs.DB.error("查询每日记录 uid:%d", e, optTime);
        } finally {

        }
        return null;
    }

}
