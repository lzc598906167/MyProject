package cn.edu.nwsuaf.liuzc.model.entity;

/**
 * Created by 林中漫步 on 2017/10/22.
 */
public class Gamer {
    private Integer gamerId;    //玩家id
    private Integer roomName;     //房间号
    private String  userName;   //玩家账号
    private String nickName;    //玩家昵称
    private Integer seatNumber; //座位号
    private Integer identity;    //玩家身份：1平民，2狼人，3预言家，4女巫，5猎人
    private Integer count;      //被狼人选择的次数或被投票的次数
    private Integer speakChance;         //发言机会，1为已经发言，0为未发言
    private Integer state;       //玩家状态：未准备0、存活1、暂时死亡2、刚死亡3、早已死亡4，离开5
    private String chance;     //女巫的机会：无0,0，只可救1,0，只可毒0,1，可毒可救1,1

    public Integer getGamerId() {
        return gamerId;
    }

    public void setGamerId(Integer gamerId) {
        this.gamerId = gamerId;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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

    public Integer getSpeakChance() {
        return speakChance;
    }

    public void setSpeakChance(Integer speakChance) {
        this.speakChance = speakChance;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getChance() {
        return chance;
    }

    public void setChance(String chance) {
        this.chance = chance;
    }
}
