package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubInfo;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.db.dao.IClubInfoMapper;
import com.xiuxiu.app.server.db.dao.IClubMemberDAO;
import com.xiuxiu.app.server.db.dao.IClubMemberMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class ClubMemberDAOImpl implements IClubMemberDAO {
    private final SqlSessionFactory factory;

    public ClubMemberDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public ClubMember load(long uid) {
        return null;
    }

    @Override
    public boolean save(ClubMember value) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubMemberMapper mapper = session.getMapper(IClubMemberMapper.class);
            if (1 != mapper.save(value)) {
                return 1 == mapper.create(value);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存信息失败 league:%s", e, value);
        } finally {

        }
        return false;
    }

    @Override
    public boolean delByClubUidAndPlayerUid(long clubUid, long playerUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubMemberMapper mapper = session.getMapper(IClubMemberMapper.class);
            return 1 == mapper.delByClubUidAndPlayerUid(clubUid,playerUid);
        } catch (Exception e) {
            Logs.DB.error("删除屏蔽失败", e);
        } finally {

        }
        return false;
    }

    @Override
    public List<ClubMember> loadAllMemberByClubUid(long clubUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubMemberMapper mapper = session.getMapper(IClubMemberMapper.class);
            return mapper.loadAllMemberByClubUid(clubUid);
        } catch (Exception e) {
            Logs.DB.error("根据群clubUid加载信息失败 clubUid:%d", e, clubUid);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

	@Override
	public boolean deleteByUid(long uid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubMemberMapper mapper = session.getMapper(IClubMemberMapper.class);
            return 1 == mapper.deleteByUid(uid);
        } catch (Exception e) {
            Logs.DB.error("删除失败", e);
        } finally {

        }
        return false;
    }
	
	@Override
    public List<ClubMember> loadAllClubByPlayerUid(long playerUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubMemberMapper mapper = session.getMapper(IClubMemberMapper.class);
            return mapper.loadAllClubByPlayerUid(playerUid);
        } catch (Exception e) {
            Logs.DB.error("根据群playerUid加载信息失败 playerUid:%d", e, playerUid);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }
	
}
