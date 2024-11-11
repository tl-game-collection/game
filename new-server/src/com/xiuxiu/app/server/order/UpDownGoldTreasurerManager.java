package com.xiuxiu.app.server.order;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubGoldChangeType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.ds.ConcurrentHashSet;
import com.xiuxiu.core.utils.TimeUtil;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UpDownGoldTreasurerManager {
    public static UpDownGoldTreasurerManager I = UpDownGoldTreasurerManagerHolder.INSTANCE;

    private static class UpDownGoldTreasurerManagerHolder {
        private static final UpDownGoldTreasurerManager INSTANCE = new UpDownGoldTreasurerManager();
    }

    private UpDownGoldTreasurerManager() {
    }

    /**
     * 等待处理的所有订单
     * @parm1 订单id
     * @parm2 订单对象
     */
    private ConcurrentHashMap<Long,UpDownGoldOrder> waitDealOrderMap = new ConcurrentHashMap<>();
    /**
     * 对应财务的等待处理的所有订单
     * @parm1 财务uid
     * @parm2 此财务相关的等待处理的订单id
     */
    private ConcurrentHashMap<Long,ConcurrentHashSet<Long>> treasurerOrderMap = new ConcurrentHashMap<>();
    /**
     * 对应玩家的等待处理的所有订单
     * @parm1 玩家uid
     * @parm2 此玩家相关的等待处理的订单id
     */
    private ConcurrentHashMap<Long,ConcurrentHashSet<Long>> playerOrderMap = new ConcurrentHashMap<>();

    /**
     * 根据订单id获取某个等待处理的订单
     * @param orderUid
     * @return
     */
    public UpDownGoldOrder getWaitDealOrderFromCache(long orderUid){
        return this.waitDealOrderMap.get(orderUid);
    }

    /**
     * 获取某个财务在某个圈中相关所有订单id
     * @param treasurerPlayerUid 财务id
     * @param clubUid 圈id(主圈)
     * @return
     */
    public List<Long> getWaitDealOrderUidByTreasurerUid(long treasurerPlayerUid, long clubUid){
        List<Long> tempSet = new ArrayList<>();
        ConcurrentHashSet<Long> orderIds = this.treasurerOrderMap.get(treasurerPlayerUid);
        if (null == orderIds){
            return tempSet;
        }
        for (Long orderId : orderIds) {
            UpDownGoldOrder tempOrder = this.getWaitDealOrderFromCache(orderId);
            if (tempOrder == null) {
                continue;
            }
            if (tempOrder.getMainClubUid() != clubUid) {
                continue;
            }
            tempSet.add(orderId);
        }
        return tempSet;
    }

    /**
     * 获取某个下分玩家某个圈中的相关所有订单id
     * @param playerUid 玩家id
     * @return
     */
    public List<Long> getWaitDealOrderUidByPlayerUid(long playerUid, long clubUid){
        List<Long> tempSet = new ArrayList<>();
        ConcurrentHashSet<Long> orderIds = this.playerOrderMap.get(playerUid);
        if (null == orderIds){
            return tempSet;
        }
        for (Long orderId : orderIds) {
            UpDownGoldOrder tempOrder = this.getWaitDealOrderFromCache(orderId);
            if (tempOrder == null) {
                continue;
            }
            if (tempOrder.getClubUid() != clubUid) {
                continue;
            }
            tempSet.add(orderId);
        }
        return tempSet;
    }

    /**
     * 获取某个圈相关的所有订单
     * @param clubUid 圈id
     * @return
     */
    public HashSet<UpDownGoldOrder> getWaitDealOrderByClubUid(long clubUid) {
        HashSet<UpDownGoldOrder> tempList = new HashSet<>();
        for (Map.Entry<Long,UpDownGoldOrder> entry : this.waitDealOrderMap.entrySet()) {
            if (entry.getValue().getClubUid() == clubUid) {
                tempList.add(entry.getValue());
            }
        }
        return tempList;
    }

    /**
     * 获取某个主圈相关的所有订单
     * @param mainClubUid 主圈id
     * @return
     */
    public HashSet<UpDownGoldOrder> getWaitDealOrderByMainClubUid(long mainClubUid) {
        HashSet<UpDownGoldOrder> tempList = new HashSet<>();
        for (Map.Entry<Long,UpDownGoldOrder> entry : this.waitDealOrderMap.entrySet()) {
            if (entry.getValue().getMainClubUid() == mainClubUid) {
                tempList.add(entry.getValue());
            }
        }
        return tempList;
    }

    //创建的新下分订单的手续费
    public int getNewUpOrderCharge(IClub _club ,IClub mainClub ,long _playerUid, int _gold) {
        long lastOrderTime = _club.getMemberExt(_playerUid, true).getOrderCreateTime();
        long curOrderTime = System.currentTimeMillis();
        if (TimeUtil.isSameDay(lastOrderTime, curOrderTime) || !mainClub.getClubInfo().checkTreasurerFirstFree()) {
            return (int)(_gold * mainClub.getClubInfo().getTreasurerServiceChargePercentage() / 100);
        }
        return 0;
    }

    //保存上下分订单
    public boolean saveUpDownGoldOrder(UpDownGoldOrder order, boolean saveNow) {
        order.setDirty(true);
        if (saveNow) {
            if (DBManager.I.getUpDownGoldOrderDao().save(order)){
                addUpDownGoldOrderToCache(order);
                return true;
            }
        } else {
            return order.save();
        }
        return false;
    }

    /**
     * 修改上下分订单状态
     * @param order 订单对象
     * @param _state 要设置的订单状态
     * @return
     */
    public ErrorCode changeUpDownGoldOrderState(UpDownGoldOrder order, int _state, boolean saveNow) {
        if (null == order){
            return ErrorCode.OK;
        }
        synchronized (order) {
            if (order.getState() == _state) {
                return ErrorCode.PLAY_SET_ORDER_STATE_EQUAL_CUR_STATE;//设置订单状态与订单当前状态一致
            }
            //订单当前状态如果不是未处理，就不能改变状态
            if (order.getState() != EUpDownGoldOrderType.WAIT.getValue()) {
                return ErrorCode.PLAY_SET_ORDER_STATE_EQUAL_CUR_STATE;
            }
            order.setState(_state);
        }

        //把订单从未处理订单内存中删除
        if (this.waitDealOrderMap.containsKey(order.getUid())) {
            this.waitDealOrderMap.remove(order.getUid());
        }

        ConcurrentHashSet<Long> orderUidSet = null;
        if (this.treasurerOrderMap.containsKey(order.getOptPlayerUid())) {
            orderUidSet = this.treasurerOrderMap.get(order.getOptPlayerUid());
            if (orderUidSet.contains(order.getUid())) {
                orderUidSet.remove(order.getUid());
            }
        }

        if (this.playerOrderMap.containsKey(order.getPlayerUid())) {
            orderUidSet = this.playerOrderMap.get(order.getPlayerUid());
            if (orderUidSet.contains(order.getUid())) {
                orderUidSet.remove(order.getUid());
            }
        }
        //拒绝
        if (_state == EUpDownGoldOrderType.REFUSE.getValue()) {
            //如果拒绝就退钱给玩家
            Player tempPlayer = PlayerManager.I.getPlayer(order.getPlayerUid());
            if (tempPlayer == null) {
                return ErrorCode.PLAYER_NOT_EXISTS;//玩家不存在
            }
            IClub club = ClubManager.I.getClubByUid(order.getClubUid());
            if (club == null) {
                return ErrorCode.GROUP_IS_NOT_EXISTS;//亲友圈不存在
            }
//            if (!club.hasMember(order.getPlayerUid())) {
//                return ErrorCode.CLUB_NOT_HAVE_PLAYER;//不在亲友圈中
//            }
            if (!club.addMemberClubGold(order.getPlayerUid(), order.getValue(), order.getOptPlayerUid(), EClubGoldChangeType.DEC_DOWN_TREASURER_INC)){
                Logs.CLUB.warn("%s playerUid:%d下分订单被拒绝时，玩家增加金币失败", order, order.getPlayerUid());
                return ErrorCode.PLAY_REFUSE_DOWN_GOLD_FAIL;//财务拒绝下分订单失败
            }
        }else if (_state == EUpDownGoldOrderType.DEAL.getValue()){
            Player tempPlayer = PlayerManager.I.getPlayer(order.getOptPlayerUid());
            if (tempPlayer == null) {
                return ErrorCode.PLAYER_NOT_EXISTS;//玩家不存在
            }
            IClub club = ClubManager.I.getClubByUid(order.getMainClubUid());
            if (club == null) {
                return ErrorCode.GROUP_IS_NOT_EXISTS;//亲友圈不存在
            }
            if (!club.addMemberClubGold(order.getOptPlayerUid(), order.getValue(), order.getPlayerUid(), EClubGoldChangeType.INC_DOWN_TREASURER_DEC)){
                Logs.CLUB.warn("%s playerUid:%d下分订单同意时，财务增加金币失败", order, order.getPlayerUid());
                return ErrorCode.PLAY_TRANSFER_CREATE_ORDER_FAIL;//财务同意下分订单失败
            }
        }
        long nowTime = System.currentTimeMillis();
        order.setOptAt(TimeUtil.getZeroTimestamp(nowTime));
        order.setOptAtDetail(nowTime);
        if (!this.saveUpDownGoldOrder(order, saveNow)) {
            return ErrorCode.PLAY_TRANSFER_CREATE_ORDER_FAIL;//保存下分订单失败
        }
        return ErrorCode.OK;
    }

    public void init () {
        List<UpDownGoldOrder> list = DBManager.I.getUpDownGoldOrderDao().loadByState(EUpDownGoldOrderType.WAIT.getValue());
        for (UpDownGoldOrder tempOrder : list) {
            this.addUpDownGoldOrderToCache(tempOrder);
        }
    }

    private void addUpDownGoldOrderToCache(UpDownGoldOrder order){
        if (order.getState() == EUpDownGoldOrderType.WAIT.getValue()) {
            this.waitDealOrderMap.put(order.getUid(),order);

            ConcurrentHashSet<Long> orderUidSet = this.treasurerOrderMap.get(order.getOptPlayerUid());
            if (orderUidSet == null){
                treasurerOrderMap.putIfAbsent(order.getOptPlayerUid(),new ConcurrentHashSet<>());
                orderUidSet = treasurerOrderMap.get(order.getOptPlayerUid());
            }
            orderUidSet.add(order.getUid());

            orderUidSet = this.playerOrderMap.get(order.getOptPlayerUid());
            if (orderUidSet == null){
                playerOrderMap.putIfAbsent(order.getPlayerUid(),new ConcurrentHashSet<>());
                orderUidSet = playerOrderMap.get(order.getPlayerUid());
            }
            orderUidSet.add(order.getUid());
        }
    }

    /**
     * 清理某一个财务某一个圈的所有未处理下分订单(订单设为拒绝状态)
     * @param treasurerPlayerUid 财务id
     * @param clubUid 圈id(主圈)
     */
    public void clearTreasurerAllOrder(long treasurerPlayerUid, long clubUid) {
        List<Long> orderIds = this.getWaitDealOrderUidByTreasurerUid(treasurerPlayerUid, clubUid);
        if (orderIds == null) {
            return;
        }
        for (Long orderId : orderIds) {
            UpDownGoldOrder tempOrder = this.getWaitDealOrderFromCache(orderId);
            if (tempOrder == null) {
                continue;
            }
            //订单状态设置为拒绝
            ErrorCode errorCode = changeUpDownGoldOrderState(tempOrder, EUpDownGoldOrderType.REFUSE.getValue(), false);
            if (errorCode != ErrorCode.OK) {
                Logs.CLUB.warn("%s treasurerPlayerUid:%d清理财务订单失败, %s", tempOrder, treasurerPlayerUid, errorCode.getMsg());
            }
        }
    }

    /**
     * 清理一个亲友圈所有订单(订单设为拒绝状态)
     * @param clubUid 圈id
     */
    public void clearClubAllOrder(long clubUid) {
        HashSet<UpDownGoldOrder> tempSet = this.getWaitDealOrderByClubUid(clubUid);
        if (tempSet == null) {
            return;
        }
        for (UpDownGoldOrder tempOrder : tempSet) {
            if (tempOrder == null) {
                continue;
            }
            //订单状态设置为拒绝
            ErrorCode errorCode = changeUpDownGoldOrderState(tempOrder, EUpDownGoldOrderType.REFUSE.getValue(), false);
            if (errorCode != ErrorCode.OK) {
                Logs.CLUB.warn("%s clubUid:%d清理财务订单失败, %s", tempOrder, clubUid, errorCode.getMsg());
            }
        }
    }

    /**
     * 清理一个亲友圈包含下级圈所有订单(订单设为拒绝状态)
     * @param clubUid 圈id
     */
    public void clearClubAndChildAllOrder(long clubUid) {
        IClub tempClub = ClubManager.I.getClubByUid(clubUid);
        if (tempClub == null) {
            return;
        }
        List<Long> allChildUidList = new ArrayList<>();
        tempClub.fillDepthChildClubUidList(allChildUidList);
        allChildUidList.add(0, clubUid);
        for (Long tempClubUid : allChildUidList) {
            UpDownGoldTreasurerManager.I.clearClubAllOrder(tempClubUid);
        }
    }

    /**
     * 清理一个主亲友圈所有订单(订单设为拒绝状态)
     * @param mainClubUid 主圈id
     */
    public void clearMainClubAllOrder(long mainClubUid) {
        HashSet<UpDownGoldOrder> tempSet = this.getWaitDealOrderByMainClubUid(mainClubUid);
        if (tempSet == null) {
            return;
        }
        for (UpDownGoldOrder tempOrder : tempSet) {
            if (tempOrder == null) {
                continue;
            }
            //订单状态设置为拒绝
            ErrorCode errorCode = changeUpDownGoldOrderState(tempOrder, EUpDownGoldOrderType.REFUSE.getValue(), false);
            if (errorCode != ErrorCode.OK) {
                Logs.CLUB.warn("%s mainClubUid:%d清理财务订单失败, %s", tempOrder, mainClubUid, errorCode.getMsg());
            }
        }
    }
}
