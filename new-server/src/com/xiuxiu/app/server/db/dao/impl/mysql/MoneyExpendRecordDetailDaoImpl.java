package com.xiuxiu.app.server.db.dao.impl.mysql;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.IMoneyExpendRecordDetailDao;
import com.xiuxiu.app.server.db.dao.IMoneyExpendRecordDetailMapper;
import com.xiuxiu.app.server.statistics.moneyrecord.MoneyExpendRecordDetail;

public class MoneyExpendRecordDetailDaoImpl implements IMoneyExpendRecordDetailDao {
    private SqlSessionFactory factory;

    public MoneyExpendRecordDetailDaoImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public MoneyExpendRecordDetail load(long uid) {
        return null;
    }

    @Override
    public boolean save(MoneyExpendRecordDetail value) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMoneyExpendRecordDetailMapper mapper = session.getMapper(IMoneyExpendRecordDetailMapper.class);
            if (1 != mapper.save(value)) {
                return 1 == mapper.create(value);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存玩家房卡消耗/增加详情失败 TodayStatistics:%s", e);
        } finally {

        }
        return false;
    }

    @Override
    public float getTotalConsumeByClubUid(long clubUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMoneyExpendRecordDetailMapper mapper = session.getMapper(IMoneyExpendRecordDetailMapper.class);
            Float result = mapper.getTotalConsumeByClubUid(clubUid);
            return null == result ? 0 : result;
        } catch (Exception e) {
            Logs.DB.error("获取玩家房卡消耗/增加详情总数 :%d", e, clubUid);
        } finally {

        }
        return 0f;
    }

    @Override
    public List<MoneyExpendRecordDetail> getByClubUid(long clubUid, int beginPag, int endPag) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMoneyExpendRecordDetailMapper mapper = session.getMapper(IMoneyExpendRecordDetailMapper.class);
            return mapper.getByClubUid(clubUid, beginPag, endPag);
        } catch (Exception e) {
            Logs.DB.error("获取玩家房卡消耗/增加详情列表 :%d", e, clubUid);
        } finally {

        }
        return null;
    }

    @Override
    public List<MoneyExpendRecordDetail> getByClubUidAndTime(long clubUid, String time, int beginPag, int endPag) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMoneyExpendRecordDetailMapper mapper = session.getMapper(IMoneyExpendRecordDetailMapper.class);
            return mapper.getByClubUidAndTime(clubUid, time, beginPag, endPag);
        } catch (Exception e) {
            Logs.DB.error("获取玩家房卡消耗/增加详情列表 :%d time:%s", e, clubUid, time);
        } finally {

        }
        return null;
    }

    @Override
    public int batchInsert(List<MoneyExpendRecordDetail> valueList) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMoneyExpendRecordDetailMapper mapper = session.getMapper(IMoneyExpendRecordDetailMapper.class);
            return mapper.batchInsert(valueList);
        } catch (Exception e) {
            Logs.DB.error("批量添加玩家房卡消耗/增加详情列表", e);
        } finally {

        }
        return 0;
    }

    @Override
    public int countByClubUid(long clubUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMoneyExpendRecordDetailMapper mapper = session.getMapper(IMoneyExpendRecordDetailMapper.class);
            return mapper.countByClubUid(clubUid);
        } catch (Exception e) {
            Logs.DB.error("查找某群数据数量", e);
        } finally {
        }
        return 0;
    }

    @Override
    public int countByClubUidAndTime(long clubUid, String time) {
        try (SqlSession session = this.factory.openSession(true)) {
            IMoneyExpendRecordDetailMapper mapper = session.getMapper(IMoneyExpendRecordDetailMapper.class);
            return mapper.countByClubUidAndTime(clubUid, time);
        } catch (Exception e) {
            Logs.DB.error("查找某群某天消耗数据数量", e);
        } finally {
        }
        return 0;
    }

}
