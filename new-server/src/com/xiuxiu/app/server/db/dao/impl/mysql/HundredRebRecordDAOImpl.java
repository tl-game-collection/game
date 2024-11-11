package com.xiuxiu.app.server.db.dao.impl.mysql;

import java.util.Collections;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.IHundredRebRecordDAO;
import com.xiuxiu.app.server.db.dao.IHundredRebRecordMapper;
import com.xiuxiu.app.server.room.normal.Hundred.HundredRebRecordInfo;

public class HundredRebRecordDAOImpl implements IHundredRebRecordDAO {
    private final SqlSessionFactory factory;

    public HundredRebRecordDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public HundredRebRecordInfo load(long uid) {
        return null;
    }

    @Override
    public boolean save(HundredRebRecordInfo info) {
        try (SqlSession session = this.factory.openSession(true)) {
            IHundredRebRecordMapper mapper = session.getMapper(IHundredRebRecordMapper.class);
            if (1 != mapper.create(info)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存百人场下注记录 hundredRebRecordInfo:%s", e, info);
        } finally {

        }
        return false;
    }

    @Override
    public boolean saveAll(List<HundredRebRecordInfo> list) {
        if (null == list || list.isEmpty()) {
            return false;
        }
        try (SqlSession session = this.factory.openSession(true)) {
            IHundredRebRecordMapper mapper = session.getMapper(IHundredRebRecordMapper.class);
            if (list.size() != mapper.createAll(list)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存百人场下注记录 hundredArenaRebRecordInfo:%s", e, list);
        } finally {

        }
        return false;
    }

    @Override
    public List<HundredRebRecordInfo> loadByPlayerUid(long roomId, long playerUid, int begin, int size) {
        try (SqlSession session = this.factory.openSession(true)) {
            IHundredRebRecordMapper mapper = session.getMapper(IHundredRebRecordMapper.class);
            List<HundredRebRecordInfo> list = mapper.loadByPlayerUid(playerUid, roomId, begin, size);
            return null == list ? Collections.EMPTY_LIST : list;
        } catch (Exception e) {
            Logs.DB.error("根据百人场下注玩家uid获取百人场记录信息失败 arenaUid:%d playerUid:%d, begin:%d, size:%d", e, roomId, playerUid, begin, size);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<HundredRebRecordInfo> loadByClubUid(long clubUid, long startTime,long endTime){
        try (SqlSession session = this.factory.openSession(true)) {
            IHundredRebRecordMapper mapper = session.getMapper(IHundredRebRecordMapper.class);
            List<HundredRebRecordInfo> list = mapper.loadByClubUid(clubUid,startTime,endTime);
            return null == list ? Collections.EMPTY_LIST : list;
        } catch (Exception e) {
            Logs.DB.error("根据百人场下注玩家uid获取百人场记录信息失败 clubUid:%d playerUid:%d, begin:%d, size:%d", e, clubUid,startTime,endTime);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

}
