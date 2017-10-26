package cn.edu.nwsuaf.liuzc.model.dao;

import cn.edu.nwsuaf.liuzc.model.entity.Gamer;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/22.
 */
@Repository
public interface GamerDao {
        List<Gamer> getAll(Integer roomName) ;//得到此房间所有玩家

        String getOwner(Integer roomName);//得到房主

        Integer addGamer(Gamer gamer);//指定房间增加一个玩家

        Integer changeOwner(Integer roomName,String userName);//改变房主

        Integer exitRoom(Integer roomName,String userName);//游戏中途有人退出

        Integer removeGamer(String userName);//指定房间减少一个玩家

        Integer getSeatByUser(String userName); //通过用户名获取座次号

        List<Integer> getSeatNumber(int roomName);//获得指定房间已分配的座次号，按升序排序

        List<String> getUserByRoomName(int  roomName);//获得指定房间的所有玩家名

        /**分配玩家身份，设置状态为存活1，若为女巫，初始化女巫技能*/
        Integer setIdentity(String userName,Integer identity,String chance);

        Integer ready(String userName);//设置某玩家为准备状态
        Integer getReadyNum(Integer roomName);//获取准备好的人数
        Integer unready(String userName); //设置某玩家为未准备状态
        List<Gamer> getWolf(Integer roomName);//获得狼人座次号
        Integer setCountByWolf(Integer roomName,Integer seatNumber); //被狼人选择的玩家count+1
        List<Integer> getCount(Integer roomName);//获取被选择次数，按座次号排序

        Integer setDying(Integer roomName,Integer seatNumber);//设置暂时死亡的玩家

        String getSeer(Integer roomName); //获得预言家的座次号，死亡则返回null

        Gamer getWitch(Integer roomName);//获得女巫的座次号和机会，死亡则返回null

        Integer getDying( Integer seatNumber);//获得暂时死亡的玩家

        Integer setState(Integer roomName,Integer oldState,Integer newState); //改变玩家状态

        Integer setChance(Integer roomName,String chance);//改变女巫的机会

        Integer poisonGamer(Integer roomName,Integer seatNumber); //女巫毒人

        List<Integer> deadSeat(Integer roomName);//获取刚死亡玩家的座位号

        List<Integer> getAllDead(Integer roomName); //获取一个房间已死亡的玩家身份

        Integer clearCount(Integer roomName); //置count全为0

        Integer setSpeakChance(Integer roomName,Integer seatNumber);//更改说话机会为1

        Integer clearChance(Integer roomName); //置发言机会全为0

        List<Integer> getSpeakSeat(Integer roomName); //获得可发言玩家的座次号

        List<Integer> setOuter(Integer roomName,Integer seatNumber); //设置投票死亡玩家的状态为4
}
