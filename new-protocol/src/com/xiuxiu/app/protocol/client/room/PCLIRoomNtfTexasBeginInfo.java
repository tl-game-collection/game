package com.xiuxiu.app.protocol.client.room;

import java.util.List;

public class PCLIRoomNtfTexasBeginInfo extends PCLIRoomNtfBeginInfo {
    public int myIndex;    //我的位置；
    public int bankIndex;    //庄的位置
    public int smallBlindIndex;  //小盲注；
    public int bigBlindIndex;    //大盲注；
    public int firstOperaPlayerIndex;//大小盲注后第一个操作的玩家；
    public List<Byte> myCards; //我手里的牌；
    public int ante;            //前注；
    @Override
    public String toString() {
        return "PCLIRoomNtfTexasBeginInfo{" +
                "myIndex=" + myIndex +
                ", bankIndex=" + bankIndex +
                ", smallBlindIndex=" + smallBlindIndex +
                ", bigBlindIndex=" + bigBlindIndex +
                ", firstOperaPlayerIndex=" + firstOperaPlayerIndex +
                ", myCards=" + myCards +
                ", ante=" + ante +
                '}';
    }




}
