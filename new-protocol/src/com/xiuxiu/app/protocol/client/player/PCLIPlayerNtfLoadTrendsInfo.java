package com.xiuxiu.app.protocol.client.player;

import java.util.ArrayList;
import java.util.List;

public class PCLIPlayerNtfLoadTrendsInfo {
    public static class FeedbackInfo {
        public long fromPlayerUid;      // 回复玩家uid
        public String fromPlayerName;   // 回复玩家name
        public long toPlayerUid;        // 被回复玩家uid, -1: 没有被回复玩家
        public String toPlayerName;     // 被回复玩家uid
        public String content;          // 回复内容

        @Override
        public String toString() {
            return "FeedbackInfo{" +
                    "fromPlayerUid=" + fromPlayerUid +
                    ", fromPlayerName='" + fromPlayerName + '\'' +
                    ", toPlayerUid=" + toPlayerUid +
                    ", toPlayerName='" + toPlayerName + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    public static class CommentInfo {
        public long commentPlayerUid;           // 评论玩家uid
        public String commentPlayerName;        // 评论玩家name
        public String commentPlayerIcon;        // 评论玩家icon
        public String content;                  // 评论玩家内容
        public long commentTimestamp;           // 评论玩家时间
        public int distance;                    // 距离(m)
        public List<FeedbackInfo> feedbacks = new ArrayList<>();    // 回复列表

        @Override
        public String toString() {
            return "CommentInfo{" +
                    "commentPlayerUid=" + commentPlayerUid +
                    ", commentPlayerName='" + commentPlayerName + '\'' +
                    ", commentPlayerIcon='" + commentPlayerIcon + '\'' +
                    ", content='" + content + '\'' +
                    ", commentTimestamp=" + commentTimestamp +
                    ", distance=" + distance +
                    ", feedbacks=" + feedbacks +
                    '}';
        }
    }

    public static class TrendsInfo {
        public long uid;                    // 动态uid
        public long postPlayerUid;          // 发布玩家uid
        public String postPlayerName;       // 发布玩家name
        public String postPlayerIcon;       // 发布玩家icon
        public long postTimestamp;          // 发布时间戳(ms)
        public String content;              // 发布内容
        public List<String> imgs = new ArrayList<>();       // 发布图片/视频等url
        public boolean showLocation = false;// 是否显示位置
        public double lat;                  // gps纬度
        public double lng;                  // gps经度
        public int distance;                // 距离(m)
        public int readCnt;                 // 阅读数量
        public int likeCnt;                 // 点赞数量
        public List<CommentInfo> comments = new ArrayList<>();  // 评论列表

        @Override
        public String toString() {
            return "TrendsInfo{" +
                    "uid=" + uid +
                    ", postPlayerUid=" + postPlayerUid +
                    ", postPlayerName='" + postPlayerName + '\'' +
                    ", postPlayerIcon='" + postPlayerIcon + '\'' +
                    ", postTimestamp=" + postTimestamp +
                    ", content='" + content + '\'' +
                    ", imgs=" + imgs +
                    ", showLocation=" + showLocation +
                    ", lat=" + lat +
                    ", lng=" + lng +
                    ", distance=" + distance +
                    ", readCnt=" + readCnt +
                    ", likeCnt=" + likeCnt +
                    ", comments=" + comments +
                    '}';
        }
    }

    public int page;            // 从0开始
    public boolean hasNext;
    public List<TrendsInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIPlayerNtfLoadTrendsInfo{" +
                "page=" + page +
                ", hasNext=" + hasNext +
                ", list=" + list +
                '}';
    }
}
