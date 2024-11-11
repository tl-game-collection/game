package com.xiuxiu.app.server.player;

import com.xiuxiu.app.server.BaseManager;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.table.DiamondCostManager;
import com.xiuxiu.core.net.Task;

import java.util.List;

public class RecommendManager extends BaseManager {
    private static class RecommendManagerHolder {
        private static RecommendManager instance = new RecommendManager();
    }

    public static RecommendManager I = RecommendManagerHolder.instance;

    private RecommendManager() {
    }

    public void recommend(Player recommendPlayer, Player recommendedPlayer, long groupUid) {
        // 推荐的玩家
        recommendPlayer.addRecommendDiamond(DiamondCostManager.I.getCostByGameType(0, DiamondCostManager.COST_TYPE_RECOMMEND, 0));
        recommendPlayer.addRecommend();
        // 被推荐的玩家
        recommendedPlayer.addRecommendDiamond(DiamondCostManager.I.getCostByGameType(0, DiamondCostManager.COST_TYPE_RECOMMENDED, 0));
        recommendedPlayer.changeRecommendUid(recommendPlayer.getUid());

        DBManager.I.save(new Task() {
            @Override
            public void run() {
                int diamond = DiamondCostManager.I.getCostByGameType(0, DiamondCostManager.COST_TYPE_RECOMMENDED, 0);
                Recommend recommend = new Recommend();
                recommend.setUid(UIDManager.I.getAndInc(UIDType.RECOMMEND));
                recommend.setRecommendPlayerUid(recommendPlayer.getUid());
                recommend.setRecommendedPlayerUid(recommendedPlayer.getUid());
                recommend.setGroupUid(groupUid);
                recommend.setState(1);
                recommend.setDiamond(diamond);
                recommend.setBindingTime(System.currentTimeMillis());
                DBManager.I.getRecommendDao().save(recommend);
            }
        });
    }

    public List<Recommend> load(Player player, int page) {
        return DBManager.I.getRecommendDao().load(player.getUid(), page * Constant.PAGE_CNT_10, Constant.PAGE_CNT_100);
    }

    public List<Recommend> load(long recommendPlayerUid, long groupUid) {
        return DBManager.I.getRecommendDao().load(recommendPlayerUid, groupUid);
    }

    public List<Recommend> load(long groupUid) {
        return DBManager.I.getRecommendDao().load(groupUid);
    }

    public boolean clear(long groupUid, long recommendPlayerUid) {
        return DBManager.I.getRecommendDao().changeState(recommendPlayerUid, groupUid, 1, 2);
    }

    @Override
    public int save() {
        return 0;
    }

    @Override
    public int shutdown() {
        return 0;
    }
}
