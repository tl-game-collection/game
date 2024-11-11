package com.xiuxiu.app.server.constant;

public final class SystemTipMessageConstant {
    public static final String TIP_BOX_JOIN = "%s坐到包间%d楼%d座";
    public static final String TIP_ARENA_JOIN = "%s加入竞技场%d(%s,%d-%d人场,底分:%s分)";
    public static final String TIP_ARENA_JOIN_WITH_LEAGUE = "%s加入联盟竞技场%d(%s,%d-%d人场,底分:%s分)";
    public static final String TIP_ARENA_WATCH_JOIN = "%s加入竞技场%d(%s,底分:%s分)";
    public static final String TIP_ARENA_WATCH_JOIN_LEAGUE = "%s加入联盟竞技场%d(%s,底分:%s分)";
    public static final String TIP_HUNDRED_ARENA_JOIN = "%s加入百人场%d(%s)";
    public static final String TIP_HUNDRED_ARENA_JOIN_LEAGUE = "%s加入联盟百人场%d(%s)";
    public static final String TIP_GROUP_KILL ="%s被%s踢出亲友圈"; //"%s被%s踢出群";
    public static final String TIP_GROUP_LEAVE ="%s退出亲友圈"; //"%s退出群";
    public static final String TIP_GROUP_ADD_WITH_CHIEF = "%s加入亲友圈";//"%s加入群";
    public static final String TIP_GROUP_ADD = "%s邀请%s加入亲友圈"; //"%s邀请%s加入群";
    public static final String TIP_RECALL_SELF = "您撤回一条消息";
    public static final String TIP_RECALL_OTHER = "%s撤回一条消息";
    public static final String TIP_DEL = "%s删除一条消息";
    public static final String TIP_MUTE_SELF = "你被%s禁言%s";
    public static final String TIP_MUTE_OTHER = "%s被%s禁言%s";

    public static final int TIP_TYPE_BY_BOX_JOIN = 1;
    public static final int TIP_TYPE_BY_ARENA_JOIN = 2;
    public static final int TIP_TYPE_BY_HUNDRED_ARENA_JOIN = 3;
}
