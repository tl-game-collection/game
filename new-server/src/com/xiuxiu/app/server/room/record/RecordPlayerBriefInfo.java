package com.xiuxiu.app.server.room.record;

import com.xiuxiu.app.server.player.IPlayer;

import java.util.ArrayList;
import java.util.List;

public class RecordPlayerBriefInfo {
    protected long playerUid;
    protected String playerName;
    protected String playerIcon;
    protected byte playerSex;
    protected int index;
    protected int bureau;
    protected List<Byte> handCard = new ArrayList<>();

    public RecordPlayerBriefInfo() {

    }

    public RecordPlayerBriefInfo(IPlayer player, int index, int bureau) {
        this.playerUid = player.getUid();
        this.playerName = player.getName();
        this.playerIcon = player.getIcon();
        this.playerSex = player.getSex();
        this.index = index;
        this.bureau = bureau;
    }

    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerIcon() {
        return playerIcon;
    }

    public void setPlayerIcon(String playerIcon) {
        this.playerIcon = playerIcon;
    }

    public byte getPlayerSex() {
        return playerSex;
    }

    public void setPlayerSex(byte playerSex) {
        this.playerSex = playerSex;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getBureau() {
        return bureau;
    }

    public void setBureau(int bureau) {
        this.bureau = bureau;
    }

    public List<Byte> getHandCard() {
        return handCard;
    }

    public void setHandCard(List<Byte> handCard) {
        this.handCard = handCard;
    }
}
