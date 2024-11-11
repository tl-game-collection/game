package com.xiuxiu.app.server.db.dao.impl.mysql;

import java.util.Collections;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.activity.ClubActivity;
import com.xiuxiu.app.server.db.dao.IClubActivityDAO;
import com.xiuxiu.app.server.db.dao.IClubActivityMapper;

public class ClubActivityDAOImpl implements IClubActivityDAO {
    private final SqlSessionFactory factory;

    public ClubActivityDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public List<ClubActivity> loadAll() {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubActivityMapper mapper = session.getMapper(IClubActivityMapper.class);
            return mapper.loadAll();
        } catch (Exception e) {
            Logs.DB.error("加载所有群组活动信息失败", e);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public ClubActivity load(long uid) {
        return null;
    }

    @Override
    public boolean save(ClubActivity clubActivity) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubActivityMapper mapper = session.getMapper(IClubActivityMapper.class);
            if (1 != mapper.update(clubActivity)) {
                return 1 == mapper.create(clubActivity);
            }
            Logs.DB.debug("保存群组活动信息成功 group:%s", clubActivity);
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存群组活动信息失败 group:%s", e, clubActivity);
        } finally {

        }
        return false;
    }
}
