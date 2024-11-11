package com.xiuxiu.app.server.db.dao.impl.mysql;

import java.util.Collections;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubInfo;
import com.xiuxiu.app.server.db.dao.IClubInfoDAO;
import com.xiuxiu.app.server.db.dao.IClubInfoMapper;

public class ClubInfoDAOImpl implements IClubInfoDAO {
    private final SqlSessionFactory factory;

    public ClubInfoDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public ClubInfo loadByUid(long clubInfoUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubInfoMapper mapper = session.getMapper(IClubInfoMapper.class);
            return mapper.loadByUid(clubInfoUid);
        } catch (Exception e) {
            Logs.DB.error("根据群组Uid加载群组信息失败 clubInfoUid:%d", e, clubInfoUid);
        } finally {

        }
        return null;
    }

    @Override
    public List<ClubInfo> loadAll() {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubInfoMapper mapper = session.getMapper(IClubInfoMapper.class);
            return mapper.loadAll();
        } catch (Exception e) {
            Logs.DB.error("加载所有群组信息失败", e);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Long> loadByPage(long limit, long offset) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubInfoMapper mapper = session.getMapper(IClubInfoMapper.class);
            return mapper.loadByPage(limit, offset);
        } catch (Exception e) {
            Logs.DB.error("加载所有群组ID失败", e);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public int isExistName(int clubType, String name) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubInfoMapper mapper = session.getMapper(IClubInfoMapper.class);
            return mapper.isExistName(clubType, name);
        } catch (Exception e) {
            Logs.DB.error("查找群名称失败", e);
        } finally {

        }
        return -1;
    }

    @Override
    public List<ClubInfo> loadByOwnerId(long ownerId) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubInfoMapper mapper = session.getMapper(IClubInfoMapper.class);
            return mapper.loadByOwnerId(ownerId);
        } catch (Exception e) {
            Logs.DB.error("加载所有群组信息失败", e);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public ClubInfo load(long uid) {
        return null;
    }

    @Override
    public boolean save(ClubInfo value) {
        try (SqlSession session = this.factory.openSession(true)) {
            IClubInfoMapper mapper = session.getMapper(IClubInfoMapper.class);
            if (1 != mapper.save(value)) {
                return 1 == mapper.create(value);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存俱乐部信息失败 ClubInfo:%s", e, value);
        } finally {

        }
        return false;
    }
}
