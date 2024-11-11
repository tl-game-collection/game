package com.xiuxiu.app.server.club.impl;

import com.xiuxiu.app.server.rank.ERankType;

import java.util.Set;

public class ClubCard extends AbstractClubBox {
    @Override
    public void addBoxScoreAndBureau(long fromClubUid, long playerUid, int score, int bureau, long now) {
        updateClubRankByBox(ERankType.CLUB_GAME_SCORE, fromClubUid, playerUid, score, System.currentTimeMillis());
    }
}
