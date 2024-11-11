package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.IUpDownGoldOrderDAO;
import com.xiuxiu.app.server.db.dao.IUpDownGoldOrderMapper;
import com.xiuxiu.app.server.order.UpDownGoldOrder;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class UpDownGoldOrderDAOImpl implements IUpDownGoldOrderDAO {
    private final SqlSessionFactory factory;

    public UpDownGoldOrderDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean save(UpDownGoldOrder info) {
        try (SqlSession session = this.factory.openSession(true)) {
            IUpDownGoldOrderMapper mapper = session.getMapper(IUpDownGoldOrderMapper.class);
            if (1 == mapper.save(info)) {
                return true;
            }
            if (1 == mapper.create(info)) {
                return true;
            }
        } catch (Exception e) {
            Logs.DB.error("保存上下分订单成功 upDownGoldOrder:%s", e, info);
        } finally {

        }
        return false;
    }

    @Override
    public UpDownGoldOrder load(long uid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IUpDownGoldOrderMapper mapper = session.getMapper(IUpDownGoldOrderMapper.class);
            UpDownGoldOrder order = mapper.load(uid);
            return order;
        } catch (Exception e) {
            Logs.DB.error("根据订单ID查询上下分订单失败 uid:%d", e, uid);
        } finally {

        }
        return null;
    }

    @Override
    public List<UpDownGoldOrder> loadByParms(long playerUid, long uid, long clubUid, int state, long createAt, int begin, int size) {
        try (SqlSession session = this.factory.openSession(true)) {
            IUpDownGoldOrderMapper mapper = session.getMapper(IUpDownGoldOrderMapper.class);
            List<UpDownGoldOrder> tempList = mapper.loadByParms(playerUid, uid, clubUid, state, createAt, begin, size);
            return tempList == null ? Collections.EMPTY_LIST : tempList;
        } catch (Exception e) {
            Logs.DB.error("根据多个条件查询下分订单失败 uid:%d", e, uid);
        } finally {

        }
        return  Collections.EMPTY_LIST;
    }

    @Override
    public List<UpDownGoldOrder> loadByPlayerUidAndState(long playerUid, long clubUid, long state, int begin, int size, long minTime) {
        try (SqlSession session = this.factory.openSession(true)) {
            IUpDownGoldOrderMapper mapper = session.getMapper(IUpDownGoldOrderMapper.class);
            List<UpDownGoldOrder> tempList = mapper.loadByPlayerUidAndState(playerUid, clubUid, state, begin, size,minTime);
            return tempList == null ? Collections.EMPTY_LIST : tempList;
        } catch (Exception e) {
            Logs.DB.error("根据玩家uid和state查询下分订单失败", e);
        } finally {

        }
        return  Collections.EMPTY_LIST;
    }

    @Override
    public List<UpDownGoldOrder> loadByOptPlayerUidAndState(long optPlayerUid, long mainClubUid, long state, int begin, int size,long minTime) {
        try (SqlSession session = this.factory.openSession(true)) {
            IUpDownGoldOrderMapper mapper = session.getMapper(IUpDownGoldOrderMapper.class);
            List<UpDownGoldOrder> tempList = mapper.loadByOptPlayerUidAndState(optPlayerUid, mainClubUid, state, begin, size,minTime);
            return tempList == null ? Collections.EMPTY_LIST : tempList;
        } catch (Exception e) {
            Logs.DB.error("根据财务uid和state查询下分订单失败", e);
        } finally {

        }
        return  Collections.EMPTY_LIST;
    }

    @Override
    public List<UpDownGoldOrder> loadByState(int state) {
        try (SqlSession session = this.factory.openSession(true)) {
            IUpDownGoldOrderMapper mapper = session.getMapper(IUpDownGoldOrderMapper.class);
            List<UpDownGoldOrder> tempList = mapper.loadByState(state);
            return tempList == null ? Collections.EMPTY_LIST : tempList;
        } catch (Exception e) {
            Logs.DB.error("根据state查询下分订单失败", e);
        } finally {

        }
        return  Collections.EMPTY_LIST;
    }

    @Override
    public List<UpDownGoldOrder> loadByMainClubUid(long mainClubUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IUpDownGoldOrderMapper mapper = session.getMapper(IUpDownGoldOrderMapper.class);
            List<UpDownGoldOrder> tempList = mapper.loadByMainClubUid(mainClubUid);
            return tempList == null ? Collections.EMPTY_LIST : tempList;
        } catch (Exception e) {
            Logs.DB.error("根据mainClubUid查询下分订单失败", e);
        } finally {

        }
        return  Collections.EMPTY_LIST;
    }
}
