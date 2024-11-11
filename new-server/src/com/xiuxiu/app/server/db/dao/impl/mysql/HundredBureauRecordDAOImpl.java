package com.xiuxiu.app.server.db.dao.impl.mysql;

import java.util.Collections;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.IHundredBureauRecordDAO;
import com.xiuxiu.app.server.db.dao.IHundredBureauRecordMapper;
import com.xiuxiu.app.server.room.normal.Hundred.HundredBureauRecordInfo;

public class HundredBureauRecordDAOImpl implements IHundredBureauRecordDAO {
    private final SqlSessionFactory factory;

    public HundredBureauRecordDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public HundredBureauRecordInfo load(long uid) {
        return null;
    }

    @Override
    public boolean save(HundredBureauRecordInfo info) {
        try (SqlSession session = this.factory.openSession(true)) {
            IHundredBureauRecordMapper mapper = session.getMapper(IHundredBureauRecordMapper.class);
            if (1 != mapper.create(info)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存百人场每局记录 HundredBureauRecordInfo:%s", e, info);
        } finally {

        }
        return false;
    }

    @Override
    public List<HundredBureauRecordInfo> loadByRoomId(long roomId, int begin, int size) {
        try (SqlSession session = this.factory.openSession(true)) {
            IHundredBureauRecordMapper mapper = session.getMapper(IHundredBureauRecordMapper.class);
            List<HundredBureauRecordInfo> list = mapper.loadByRoomId(roomId, begin, size);
            return null == list ? Collections.EMPTY_LIST : list;
        } catch (Exception e) {
            Logs.DB.error("根据百人场uid获取百人场每局记录信息失败 roomId:%s, begin:%d, size:%d", e, roomId, begin, size);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }
    
    @Override
    public List<HundredBureauRecordInfo> loadBankerByRoomId(long roomId,long bankerUid, int begin, int size) {
        try (SqlSession session = this.factory.openSession(true)) {
            IHundredBureauRecordMapper mapper = session.getMapper(IHundredBureauRecordMapper.class);
            List<HundredBureauRecordInfo> list = mapper.loadBankerByRoomId(roomId,bankerUid, begin, size);
            return null == list ? Collections.EMPTY_LIST : list;
        } catch (Exception e) {
            Logs.DB.error("根据百人场uid获取百人场每局记录信息失败 roomUid:%s, begin:%d, size:%d", e, roomId, begin, size);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

}
