package com.xiuxiu.app.protocol.client.box;

public class PCLIBoxReqStateInfo {
    /** 亲友圈id */
    public long clubUid;
    /** 楼层id */
    public long floorUid;
    /** 分页 */
    public int page;
    /** 每页数量 */
    public int size;
    /** 是否显示玩法桌(0不显示1显示) */
    public boolean flag;
    /** 显示类型(0默认1竞技场2百人场) */
    public int type;
    /** 游戏类型(-1默认，其他值为游戏类型; gameyType属性在type为0时传) */
    public int gameType;
    /** 游戏地方类型(0默认1竞技场2百人场) */
    public int gameSubType;
    /** 底分 */
    public int endPoint;
    /** 玩法 */
    public int playType;
    /** 客户端显示类型 0 是type 参与筛选 1 gameSubType,entPoint 参与筛选*/
    public int drawType;

    @Override
    public String toString() {
        return "PCLIBoxReqStateInfo{" +
                "clubUid=" + clubUid +
                ", floorUid=" + floorUid +
                '}';
    }
}
