package cn.edu.nwsuaf.liuzc.action;

import cn.edu.nwsuaf.liuzc.model.entity.Room;
import cn.edu.nwsuaf.liuzc.model.service.RoomService;

import javax.annotation.Resource;

/**
 * Created by 林中漫步 on 2017/10/22.
 */
public class RoomAction {
    private Integer roomName;
    private Integer peopleNumber;
    private Room room;
    private int code;
    private int type;
    private String owner;
    @Resource
    RoomService roomService;
    public String findSimple(){
        roomName = roomService.findSimple();
        if(roomName!=null) {
            code = 200;
        }
        else {
            code = 222;
        }
        return "simple";
    }
    public String findHard(){
        roomName = roomService.findHard();
        if(roomName!=null) {
            code = 200;
        }
        else {
            code = 222;
        }
        return "hard";
    }
    public String get(){
        room = roomService.get(roomName);
        if(room!=null){
            code = 200;
        }else {
            code = 222;
        }
        return "get_room";
    }
    public String insert(){
        roomName = roomService.insert(type,owner);
        code = 200;
        return "create";
    }
    public String delete(){
        int flag = roomService.delete(roomName);
        if(flag>0) {
            code = 200;
        }
        else {
            code = 222;
        }
        return "delete";
    }
    public String peopleNumber(){
        peopleNumber = roomService.getPeopleNumber(roomName);
        if(peopleNumber!=null){
            code = 200;
        }else {
            code = 222;
        }
        return "people";
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

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
