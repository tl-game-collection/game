package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.PlayerDAO;
import com.xiuxiu.app.server.db.dao.PlayerMapper;
import com.xiuxiu.app.server.player.Player;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class PlayerDAOImpl implements PlayerDAO {
    private final SqlSessionFactory factory;

    public PlayerDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public Player load(long playerUid) {
        try (SqlSession session = this.factory.openSession(true)) {
            PlayerMapper mapper = session.getMapper(PlayerMapper.class);
            return mapper.loadByUid(playerUid);
        } catch (Exception e) {
            Logs.DB.error("根据玩家Uid加载玩家信息失败 playerUid:%d", e, playerUid);
        } finally {

        }
        return null;
    }

    @Override
    public List<Player> loadByUids(List<Long> playerUids) {
        try (SqlSession session = this.factory.openSession(true)) {
            PlayerMapper mapper = session.getMapper(PlayerMapper.class);
            return mapper.loadByUids(playerUids);
        } catch (Exception e) {
            Logs.DB.error("根据玩家Uid列表加载玩家信息失败 playerUids:%s", e, playerUids);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Long> loadAllUid() {
        try (SqlSession session = this.factory.openSession(true)) {
            PlayerMapper mapper = session.getMapper(PlayerMapper.class);
            return mapper.loadAllUid();
        } catch (Exception e) {
            Logs.DB.error("加载玩家uid信息失败", e);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Player> loadAllPlayer(int page, int pageSize) {
        try (SqlSession session = this.factory.openSession(true)) {
            PlayerMapper mapper = session.getMapper(PlayerMapper.class);
            return mapper.loadAllPlayer(page, pageSize);
        } catch (Exception e) {
            Logs.DB.error("加载玩家信息失败", e);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public void resetAllRobotLoginOutTime(long time) {
        try (SqlSession session = this.factory.openSession(true)) {
            PlayerMapper mapper = session.getMapper(PlayerMapper.class);
            mapper.resetAllRobotLoginOutTime(time);
        } catch (Exception e) {
            Logs.DB.error("复位机器人上次登出时间失败", e);
        } finally {

        }
    }

    @Override
    public boolean save(Player player) {
        try (SqlSession session = this.factory.openSession(true)) {
            PlayerMapper mapper = session.getMapper(PlayerMapper.class);
            if (1 != mapper.save(player)) {
                return 1 == mapper.create(player);
            }
            Logs.DB.debug("保存玩家信息成功 player:%s", player);
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存玩家信息失败 player:%s", e, player);
        } finally {

        }
        return false;
    }
    
    @Override
    public List<Player> loadAllRobot(int page, int pageSize) {
        try (SqlSession session = this.factory.openSession(true)) {
            PlayerMapper mapper = session.getMapper(PlayerMapper.class);
            return mapper.loadAllRobot(page, pageSize);
        } catch (Exception e) {
            Logs.DB.error("加载玩家信息失败", e);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }
    
}
