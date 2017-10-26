package cn.edu.nwsuaf.liuzc.action;

import cn.edu.nwsuaf.liuzc.model.entity.Gamer;
import cn.edu.nwsuaf.liuzc.model.service.GamerService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/22.
 */
public class GamerAction {
    private Integer roomName;
    private String userName;
    private Integer seatNumber;
    private int code;
    private Gamer gamer;
    private String owner;
    private List<Gamer> gamers;
    @Resource
    GamerService gamerService;
    public String addGamer(){
        gamers = gamerService.getAll(roomName);
        owner = gamerService.getOwner(roomName);
        code = gamerService.addGamer(roomName,userName);
        return "add";
    }

    public String changeOwner(){
        owner = gamerService.changeOwner(roomName,userName);
        code = 200;
        return "change";
    }

    public String exitRoom(){
        int flag = gamerService.exitRoom(roomName,userName);
        if (flag > 0){
            code = 200;
        }else {
            code = 222;
        }
        return "exit";
    }

    public String removeGamer(){
        int flag = gamerService.removeGamer(roomName,userName);
        if (flag > 0){
            code = 200;
        }else {
            code = 222;
        }
        return "remove";
    }

    public String startGame(){
        code = gamerService.setIdentity(roomName);
        code = 200;
        return "start";
    }

    public String ready(){
        code = gamerService.ready(roomName,userName);
        return "ready";
    }

    public String unready(){
        code = gamerService.unready(roomName,userName,seatNumber);
        return "unready";
    }


    public String getWolf(){
        code = gamerService.getWolf(roomName);
        return "getWolf";
    }

    public String pickGamerBywolf(){
        code = gamerService.pickGamerByWolf(roomName,seatNumber);
        return "wolfPicked";
    }

    public String gotoSeerPeriod(){
        code = gamerService.gotoSeerPeriod(roomName);
        return "seerPeriod";
    }

    public String gotoWitchPeriod(){
        code = gamerService.gotoWitchPeriod(roomName);
        return "witchPeriod";
    }

    public String saveGamer(){
        int flag = gamerService.saveGamer(roomName);
        if (flag > 0){
            code = 200;
        }else {
            code = 222;
        }
        return "save";
    }

    public String poisonGamer(){
        int flag = gamerService.poisonGamer(roomName,seatNumber);
        if (flag > 0){
            code = 200;
        }else {
            code = 222;
        }
        return "poison";
    }

    public String leaveNight(){
        code = gamerService.leaveNight(roomName);
        return "leaveNight";
    }

    public String nextGamer(){
        code = gamerService.nextGamer(roomName,seatNumber);
        return "next";
    }

    public String voteGamer() {
        code = gamerService.voteGamer(roomName, seatNumber);
        return "vote";
    }

    public String countResult(){
        code = gamerService.countResult(roomName);
        return "countResult";
    }

    public Integer getRoomName() {
        return roomName;
    }

    public void setRoomName(Integer roomName) {
        this.roomName = roomName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Gamer getGamer() {
        return gamer;
    }

    public void setGamer(Gamer gamer) {
        this.gamer = gamer;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<Gamer> getGamers() {
        return gamers;
    }

    public void setGamers(List<Gamer> gamers) {
        this.gamers = gamers;
    }

    public GamerService getGamerService() {
        return gamerService;
    }

    public void setGamerService(GamerService gamerService) {
        this.gamerService = gamerService;
    }
}
