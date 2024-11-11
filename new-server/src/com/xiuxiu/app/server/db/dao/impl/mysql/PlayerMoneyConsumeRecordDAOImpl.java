package com.xiuxiu.app.server.db.dao.impl.mysql;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.IPlayerMoneyConsumeRecordDAO;
import com.xiuxiu.app.server.db.dao.IPlayerMoneyConsumeRecordMapper;
import com.xiuxiu.app.server.statistics.consume.PlayerMoneyConsumeRecord;

public class PlayerMoneyConsumeRecordDAOImpl implements IPlayerMoneyConsumeRecordDAO {
    
    private final SqlSessionFactory factory;

    public PlayerMoneyConsumeRecordDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }
    
    @Override
    public boolean save(PlayerMoneyConsumeRecord playerMoneyConsumeRecord) {
        try (SqlSession session = this.factory.openSession(true)) {
            IPlayerMoneyConsumeRecordMapper mapper = session.getMapper(IPlayerMoneyConsumeRecordMapper.class);
            if (1 == mapper.save(playerMoneyConsumeRecord)) {
                return true;
            }
            if (1 == mapper.create(playerMoneyConsumeRecord)) {
                return true;
            }
        } catch (Exception e) {
            Logs.DB.error("playerMoneyConsumeRecord失败 Info:%s", e, playerMoneyConsumeRecord);
        } finally {

        }
        return false;
    }

    @Override
    public PlayerMoneyConsumeRecord load(long playerUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IPlayerMoneyConsumeRecordMapper mapper = session.getMapper(IPlayerMoneyConsumeRecordMapper.class);
            return mapper.loadByPlayerUid(playerUid);
        } catch (Exception e) {
            Logs.DB.error("根据玩家Uid加载玩家房卡消耗数量统计信息失败 playerUid:%d", e, playerUid);
        } finally {

        }
        return null;
    }

}
