package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubMemberExt;
import com.xiuxiu.app.server.db.dao.IClubMemberExtDAO;
import com.xiuxiu.app.server.db.dao.IClubMemberExtMapper;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class ClubMemberExtDAOImpl implements IClubMemberExtDAO {
    private final SqlSessionFactory factory;

    public ClubMemberExtDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public ClubMemberExt load(long uid) {
        return null;
    }

    @Override
    public boolean save(ClubMemberExt value) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubMemberExtMapper mapper = session.getMapper(IClubMemberExtMapper.class);
            if (1 != mapper.save(value)) {
                return 1 == mapper.create(value);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存 ClubMemberExt 信息失败 league:%s", e, value);
        } finally {

        }
        return false;
    }

    @Override
    public List<ClubMemberExt> loadAll(long clubUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubMemberExtMapper mapper = session.getMapper(IClubMemberExtMapper.class);
            return mapper.loadAll(clubUid);
        } catch (Exception e) {
            Logs.DB.error("加载 ClubMemberExt 信息失败", e);
        } finally {

        }
        return null;
    }
}
