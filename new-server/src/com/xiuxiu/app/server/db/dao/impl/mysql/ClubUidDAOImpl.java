package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubUid;
import com.xiuxiu.app.server.db.dao.IClubUidDAO;
import com.xiuxiu.app.server.db.dao.IClubUidMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class ClubUidDAOImpl implements IClubUidDAO {
    private final SqlSessionFactory factory;

    public ClubUidDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public ClubUid load(long uid) {
        return null;
    }

    @Override
    public boolean save(ClubUid clubUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubUidMapper mapper = session.getMapper(IClubUidMapper.class);
            if (1 != mapper.update(clubUid)) {
                return 1 == mapper.create(clubUid);
            }else{
                return true;
            }
        } catch (Exception e) {
            Logs.DB.error("保存亲友圈uid失败 uid:%s", e, clubUid);
        } finally {

        }
        return false;
    }

    @Override
    public List<ClubUid> loadUnused(boolean good, int cnt) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubUidMapper mapper = session.getMapper(IClubUidMapper.class);
            return mapper.loadUnused(good ? 1 : 0,cnt);
        } catch (Exception e) {
            Logs.DB.error("获取亲友圈uid失败 good:%s", e, good);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

}
