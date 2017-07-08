package com.nwsuaf.werewolf.game.model;

/**
 * Created by liulin on 2017/6/30.
 */


public class Room {
    private Integer roomId;
    private Integer roomName;
    private Integer type;
    private Integer peopleNumber;

    public Room() {
    }

    public Room(Integer roomId, Integer roomName, Integer type, Integer peopleNumber) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.type = type;
        this.peopleNumber = peopleNumber;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getRoomName() {
        return roomName;
    }

    public void setRoomName(Integer roomName) {
        this.roomName = roomName;
    }

    public Integer getPeopleNumber() {
        return peopleNumber;
    }

    public void setPeopleNumber(Integer peopleNumber) {
        this.peopleNumber = peopleNumber;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}


