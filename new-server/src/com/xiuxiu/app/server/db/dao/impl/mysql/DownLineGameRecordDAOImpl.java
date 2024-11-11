package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.IClubUidMapper;
import com.xiuxiu.app.server.db.dao.IDownLineGameRecordDAO;
import com.xiuxiu.app.server.db.dao.IDownLineGameRecordMapper;
import com.xiuxiu.app.server.statistics.DownLineGameRecord;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class DownLineGameRecordDAOImpl implements IDownLineGameRecordDAO {
    private final SqlSessionFactory factory;

    public DownLineGameRecordDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public DownLineGameRecord load(long uid) {
        return null;
    }

    @Override
    public boolean save(DownLineGameRecord value) {
        try (SqlSession session = this.factory.openSession(true)) {
            IDownLineGameRecordMapper mapper = session.getMapper(IDownLineGameRecordMapper.class);
            if (1 != mapper.update(value)) {
                return 1 == mapper.create(value);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存某一天下级游戏数据失败 downLineGameRecord:%s", e, value);
        } finally {

        }
        return false;
    }

    @Override
    public List<DownLineGameRecord> loadOneDayRecord(long zeroTime) {
        try (SqlSession session = this.factory.openSession(true)) {
            IDownLineGameRecordMapper mapper = session.getMapper(IDownLineGameRecordMapper.class);
            return mapper.loadOneDayRecord(zeroTime);
        } catch (Exception e) {
            Logs.DB.error("获取某一天下级游戏数据失败 zeroTime:%d", e, zeroTime);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }
}
