package com.xiuxiu.app.server.rank;

import com.xiuxiu.core.utils.TimeUtil;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RankItemInfo {
    protected long lastRecordTime;
    protected int score;
    protected int bureau;
    protected int bigWinner;
    protected int totalBureau;
    protected int totalScore;
    protected int totalWinner;
    protected int[] sevenDayBureau = new int[7];
    protected int[] sevenDayScore = new int[7];
    protected int[] sevenDayBigWinner = new int[7];
    protected long[] sevenLastRecordTime = new long[7];

    protected transient ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public void zero(long now) {
        if (TimeUtil.isSameDay(now, this.lastRecordTime)) {
            return;
        }
        try {
            this.rwLock.writeLock().lock();
            int dis = (int) ((TimeUtil.getZeroTimestamp(now) - TimeUtil.getZeroTimestamp(this.lastRecordTime)) / TimeUtil.ONE_DAY_MS);
            if (dis >= 7) {
                for (int i = 6; i > 0; --i) {
                    this.sevenDayBureau[i] = 0;
                    this.sevenDayScore[i] = 0;
                    this.sevenDayBigWinner[i] = 0;
                    this.sevenLastRecordTime[i] = 0;
                }
            } else if (dis > 0) {
                this.sevenDayBureau[6] = 0;
                this.sevenDayScore[6] = 0;
                this.sevenDayBigWinner[6] = 0;
                this.sevenLastRecordTime[6] = this.lastRecordTime;
                for (int i = 0; i < 6; ++i) {
                    this.sevenDayBureau[6] += this.sevenDayBureau[i];
                    this.sevenDayScore[6] += this.sevenDayScore[i];
                    this.sevenDayBigWinner[6] += this.sevenDayBigWinner[i];
                }
                for (int i = 5; i >= dis; --i) {
                    this.sevenDayBureau[i] = this.sevenDayBureau[i - 1];
                    this.sevenDayScore[i] = this.sevenDayScore[i - 1];
                    this.sevenDayBigWinner[i] = this.sevenDayBigWinner[i - 1];
                    this.sevenLastRecordTime[i] = this.sevenLastRecordTime[i - 1];
                }
                for (int i = 0; i < dis; ++i) {
                    this.sevenDayBureau[i] = 0;
                    this.sevenDayScore[i] = 0;
                    this.sevenDayBigWinner[i] = 0;
                    this.sevenLastRecordTime[i] = 0;
                }
            }
            this.sevenDayScore[0] = 0;
            this.sevenDayBureau[0] = 0;
            this.sevenDayBigWinner[0] = 0;
            this.sevenLastRecordTime[0] = 0;
            this.score = 0;
            this.bureau = 0;
            this.bigWinner = 0;

        } finally {
            this.rwLock.writeLock().unlock();
        }
    }

    public void addScoreAndBureau(int score, int bureau, long now) {
        try {
            this.rwLock.writeLock().lock();
            if (!TimeUtil.isSameDay(now, this.lastRecordTime)) {
                this.sevenDayBureau[6] = 0;
                this.sevenDayScore[6] = 0;
                for (int i = 0; i < 6; ++i) {
                    this.sevenDayBureau[6] += this.sevenDayBureau[i];
                    this.sevenDayScore[6] += this.sevenDayScore[i];
                }
                for (int i = 5; i > 0; --i) {
                    this.sevenDayBureau[i] = this.sevenDayBureau[i - 1];
                    this.sevenDayScore[i] = this.sevenDayScore[i - 1];
                }
                this.sevenDayScore[0] = 0;
                this.sevenDayBureau[0] = 0;
                this.score = 0;
                this.bureau = 0;
            }
            this.score += score;
            this.bureau += bureau;
            this.totalScore += score;
            this.totalBureau += bureau;
            this.lastRecordTime = now;
            this.sevenDayScore[0] = this.score;
            this.sevenDayBureau[0] = this.bureau;
            this.sevenDayScore[6] += score;
            this.sevenDayBureau[6] += bureau;
        } finally {
            this.rwLock.writeLock().unlock();
        }
    }

    public void addBigWinner(long now, int win){
        try {
            this.rwLock.writeLock().lock();
            if (!TimeUtil.isSameDay(now, this.lastRecordTime)) {
                this.sevenDayBigWinner[6] = 0;
                this.sevenLastRecordTime[6] = this.lastRecordTime;
                for (int i = 0; i < 6; ++i) {
                    this.sevenDayBigWinner[6] += this.sevenDayBigWinner[i];
                }
                for (int i = 5; i > 0; --i) {
                    this.sevenDayBigWinner[i] = this.sevenDayBigWinner[i - 1];
                    this.sevenLastRecordTime[i] = this.sevenLastRecordTime[i - 1];
                }
                this.sevenDayBigWinner[0] = 0;
                this.sevenLastRecordTime[0] = 0;
            }
            this.bigWinner += win;
            this.totalWinner += win;
            this.sevenDayBigWinner[0] = this.bigWinner;
            this.sevenDayBigWinner[6] += win;
            this.sevenLastRecordTime[0] = now;
            this.sevenLastRecordTime[6] = now;
        } finally {
            this.rwLock.writeLock().unlock();
        }
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getBureau() {
        return bureau;
    }

    public void setBureau(int bureau) {
        this.bureau = bureau;
    }

    public int getBigWinner() {
        return bigWinner;
    }

    public void setBigWinner(int bigWinner) {
        this.bigWinner = bigWinner;
    }

    public long getLastRecordTime() {
        return lastRecordTime;
    }

    public void setLastRecordTime(long lastRecordTime) {
        this.lastRecordTime = lastRecordTime;
    }

    public int getTotalBureau() {
        return totalBureau;
    }

    public void setTotalBureau(int totalBureau) {
        this.totalBureau = totalBureau;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }


    public int[] getSevenDayBureau() {
        return sevenDayBureau;
    }

    public int getTotalWinner() {
        return totalWinner;
    }

    public void setTotalWinner(int totalWinner) {
        this.totalWinner = totalWinner;
    }

    public void setSevenDayBureau(int[] sevenDayBureau) {
        this.sevenDayBureau = sevenDayBureau;
    }

    public int[] getSevenDayBigWinner() {
        return sevenDayBigWinner;
    }

    public void setSevenDayBigWinner(int[] sevenDayBigWinner) {
        this.sevenDayBigWinner = sevenDayBigWinner;
    }

    public int[] getSevenDayScore() {
        return sevenDayScore;
    }

    public void setSevenDayScore(int[] sevenDayScore) {
        this.sevenDayScore = sevenDayScore;
    }

    public ReadWriteLock getRwLock() {
        return rwLock;
    }

    public void setRwLock(ReadWriteLock rwLock) {
        this.rwLock = rwLock;
    }

    public long[] getSevenLastRecordTime() {
        return sevenLastRecordTime;
    }

    public void setSevenLastRecordTime(long[] sevenLastRecordTime) {
        this.sevenLastRecordTime = sevenLastRecordTime;
    }
}
