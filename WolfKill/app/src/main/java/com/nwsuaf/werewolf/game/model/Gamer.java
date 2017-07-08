package com.nwsuaf.werewolf.game.model;

import java.io.Serializable;

/**
 * Created by liulin on 2017/7/3.
 */

public class Gamer implements Serializable {
    private Integer gamerId;    //玩家id
    private Integer roomName;    //房间号
    private String username;    //玩家账号
    private String nickname;    //玩家昵称
    private Integer seatNumber; //座位号
    private Integer identity;   //玩家身份：1平民，2狼人，3预言家，4女巫
    private Integer count;      //被狼人选择的次数或被投票的次数
    private Integer who;        //投票选择的玩家或女巫选择毒的玩家座次号
    private Integer state;      //玩家状态：未准备0、存活/准备1、暂时死亡2、死亡3
    private String chance;     //女巫的机会：无00，只可救10，只可毒01，可毒可救11
    private Boolean hasPeople;  //当在房间中的时候，这个位置有没有人

    public Gamer() {
        hasPeople = false;
    }

    public Boolean getHasPeople() {
        return hasPeople;
    }

    public void setHasPeople(Boolean hasPeople) {
        this.hasPeople = hasPeople;
    }

    public Gamer(Integer gamerId, Integer roomName, String username, String nickname, Integer seatNumber, Integer identity, Integer count, Integer who, Integer state, String chance, Boolean hasPeople) {
        this.gamerId = gamerId;
        this.roomName = roomName;
        this.username = username;
        this.nickname = nickname;
        this.seatNumber = seatNumber;
        this.identity = identity;
        this.count = count;
        this.who = who;
        this.state = state;
        this.chance = chance;
        this.hasPeople = hasPeople;
    }

    public Integer getGamerId() {
        return gamerId;
    }

    public void setGamerId(Integer gamerId) {
        this.gamerId = gamerId;
    }

    public String getChance() {
        return chance;
    }

    public void setChance(String chance) {
        this.chance = chance;
    }

    public Integer getRoomName() {
        return roomName;
    }

    public void setRoomName(Integer roomName) {
        this.roomName = roomName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Integer getIdentity() {
        return identity;
    }

    public void setIdentity(Integer identity) {
        this.identity = identity;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getWho() {
        return who;
    }

    public void setWho(Integer who) {
        this.who = who;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
