package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.INicknameDAO;
import com.xiuxiu.app.server.db.dao.INicknameMapper;
import com.xiuxiu.app.server.player.Nickname;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class NicknameDAOImpl implements INicknameDAO {
    private final SqlSessionFactory factory;

    public NicknameDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public Nickname getOne() {
        try (SqlSession session = this.factory.openSession(true)) {
            INicknameMapper mapper = session.getMapper(INicknameMapper.class);
            return mapper.loadOne();
        } catch (Exception e) {
            Logs.DB.error("获取Nickname失败", e);
        }
        return null;
    }

    @Override
    public boolean save(Nickname nickname) {
        try (SqlSession session = this.factory.openSession(true)) {
            INicknameMapper mapper = session.getMapper(INicknameMapper.class);
            mapper.save(nickname);
            return true;
        } catch (Exception e) {
            Logs.DB.error("更新Nickname失败", e);
        }
        return false;
    }

    @Override
    public Nickname load(long uid) {
        return null;
    }
}
