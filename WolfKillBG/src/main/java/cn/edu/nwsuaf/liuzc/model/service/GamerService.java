package cn.edu.nwsuaf.liuzc.model.service;

import cn.edu.nwsuaf.liuzc.model.dao.GamerDao;
import cn.edu.nwsuaf.liuzc.model.entity.Gamer;
import cn.edu.nwsuaf.liuzc.utils.SendBroadCast;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by 林中漫步 on 2017/10/22.
 */
@Service
public class GamerService {
    private SendBroadCast sendBroadCast;
    @Resource
    GamerDao gamerDao;
    public List<Gamer> getAll(Integer roomName){
        return gamerDao.getAll(roomName);
    }

    public String getOwner(Integer roomName){
        return gamerDao.getOwner(roomName);
    }

    public Integer addGamer(Integer roomName,String username){
        Gamer gamer = new Gamer();
        gamer.setRoomName(roomName);
        gamer.setUserName(username);
        gamer.setState(0);
        //获取从1号开始第一个空闲的座位号
        List<Integer> seatNumbers = gamerDao.getSeatNumber(roomName);
        int seatNumber = 1;
        for (Integer one : seatNumbers){
            if (seatNumber == one){
                seatNumber++;
            }else {
                break;
            }
        }
        gamer.setSeatNumber(seatNumber);
        int flag = gamerDao.addGamer(gamer);
        if (flag > 0){
            sendBroadCast = new SendBroadCast();
            return sendBroadCast.pushNewUser(roomName,username,seatNumber);
        }else {
            return 222;
        }
    }

    public String changeOwner(Integer roomName, String username){
        List<String> usernameList = gamerDao.getUserByRoomName(roomName);
        String owner = null;
        for (String one : usernameList){
            if (!one.equals(username)){
                owner = one;
                break;
            }
        }
        gamerDao.changeOwner(roomName,owner);
        return owner;
    }

    public Integer exitRoom(Integer roomName,String username){
        Integer seatNumber = gamerDao.getSeatByUser(username);
        int flag = gamerDao.exitRoom(roomName,username);
        if (flag > 0){
            sendBroadCast = new SendBroadCast();
            return sendBroadCast.pushExitUser(roomName,seatNumber);
        }else {
            return 222;
        }
    }

    public Integer removeGamer(Integer roomName,String username){
        Integer seatNumber = gamerDao.getSeatByUser(username);
        int flag = gamerDao.removeGamer(username);
        if (flag > 0){
            sendBroadCast = new SendBroadCast();
            return sendBroadCast.pushExitUser(roomName,seatNumber);
        }else {
            return 222;
        }
    }

    //随机分配身份并开始游戏
    public Integer setIdentity(int roomName){
        List<String> userNameList = gamerDao.getUserByRoomName(roomName);
        int identity = 0;
        int[] a = new int[5];
        List<Integer> identityList = new ArrayList<>();
//        gamerDao.setIdentity(userNameList.get(0),3,null);
//        identityList.add(3);
//        gamerDao.setIdentity(userNameList.get(1),4,"1,1");
//        identityList.add(4);
//        gamerDao.setIdentity(userNameList.get(2),1,null);
//        identityList.add(1);
//        gamerDao.setIdentity(userNameList.get(3),1,null);
//        identityList.add(1);
//        gamerDao.setIdentity(userNameList.get(4),1,null);
//        identityList.add(1);
//        gamerDao.setIdentity(userNameList.get(5),2,null);
//        identityList.add(2);
//        gamerDao.setIdentity(userNameList.get(6),2,null);
//        identityList.add(2);
//        gamerDao.setIdentity(userNameList.get(7),2,null);
//        identityList.add(2);
//        gamerDao.setIdentity(userNameList.get(8),5,null);
//        identityList.add(5);

        for (String one : userNameList){
            while (true){
                identity = new Random().nextInt(5) + 1;
                if (identity == 1 && a[0] < 3){    //分配平民
                    gamerDao.setIdentity(one,identity,null);
                    a[0]++;
                    break;
                }
                else if (identity == 2 && a[1] < 3){    //分配狼人
                    gamerDao.setIdentity(one,identity,null);
                    a[1]++;
                    break;
                }
                else if (identity == 3 && a[2] < 1){    //分配预言家
                    gamerDao.setIdentity(one,identity,null);
                    a[2]++;
                    break;
                }
                else if (identity == 4 && a[3] < 1){    //分配女巫
                    gamerDao.setIdentity(one,identity,"1,1");
                    a[3]++;
                    break;
                }
                else if (identity == 5 && a[4] < 1){    //分配猎人
                    gamerDao.setIdentity(one,identity,null);
                    a[4]++;
                    break;
                }
            }
            identityList.add(identity);
        }
        sendBroadCast = new SendBroadCast();
        return sendBroadCast.pushStart(roomName,identityList);
    }

    public Integer ready(Integer roomName,String username){
        int flag = gamerDao.ready(username);
        int readyNumber = gamerDao.getReadyNum(roomName);
        int seatNumber = gamerDao.getSeatByUser(username);
        if (flag > 0){
            sendBroadCast = new SendBroadCast();
            return sendBroadCast.pushReady(roomName,seatNumber,readyNumber);
        }else {
            return 222;
        }
    }



    public Integer unready(Integer roomName,String username,Integer seatNumber){
        int flag = gamerDao.unready(username);
        if (flag > 0){
            sendBroadCast = new SendBroadCast();
            return sendBroadCast.pushUnready(roomName,seatNumber);
        }else {
            return 222;
        }
    }


    public Integer getWolf(Integer roomName){
        List<Gamer> wolfList = gamerDao.getWolf(roomName);
        List<String> wolfNameList = new ArrayList<>();
        List<Integer> wolfSeatList = new ArrayList<>();
        for (Gamer one : wolfList){
            wolfNameList.add(one.getUserName());
            wolfSeatList.add(one.getSeatNumber());
        }
        sendBroadCast = new SendBroadCast();
        return sendBroadCast.pushWolfToWolf(roomName,wolfNameList,wolfSeatList);
    }

    //被狼人选择的玩家count加1
    public Integer pickGamerByWolf(Integer roomName, Integer seatNumber){
        int flag = gamerDao.setCountByWolf(roomName, seatNumber);
        if (flag>0){
            List<Integer> countList = gamerDao.getCount(roomName);
            List<Gamer> wolfList = gamerDao.getWolf(roomName);
            List<String> wolfNameList = new ArrayList<>();
            for (Gamer one : wolfList){
                wolfNameList.add(one.getUserName());
            }
            sendBroadCast = new SendBroadCast();
            return sendBroadCast.pushWhoToWolf(roomName,wolfNameList,countList);
        }else {
            return 222;
        }
    }

    //判断是否有人死亡，进入预言家阶段
    public Integer gotoSeerPeriod(Integer roomName){
        List<Integer> countList = gamerDao.getCount(roomName);

        int max = 0;
        int max_seat = judgeDead(max,countList);


        if (max_seat > 0){
            gamerDao.setDying(roomName,max_seat);
        }

        String seerName = gamerDao.getSeer(roomName);
        if (seerName != null){
            sendBroadCast = new SendBroadCast();
            return sendBroadCast.pushSeerPeriod(roomName,seerName);
        }else {
            return 444;
        }
    }

    //进入女巫阶段
    public Integer gotoWitchPeriod(Integer roomName) {
        Gamer witchGamer = gamerDao.getWitch(roomName);
        if (witchGamer != null){
            String  chance = witchGamer.getChance();
            Integer dyingSeat = gamerDao.getDying(roomName);
            if (dyingSeat == null){
                dyingSeat = 0;
            }
            String witchName = witchGamer.getUserName();
            sendBroadCast = new SendBroadCast();
            return sendBroadCast.pushWitchPeriod(roomName,witchName,chance,dyingSeat);
        }else {
            return 444;
        }
    }

    public Integer saveGamer(Integer roomName) {
        Gamer witchGamer = gamerDao.getWitch(roomName);
        String chance = witchGamer.getChance();
        String[] strings = chance.split(",");
        String newChance = "0," + strings[1];
        gamerDao.setState(roomName,2,1);
        return gamerDao.setChance(roomName,newChance);
    }


    public Integer poisonGamer(Integer roomName, Integer seatNumber) {
        Gamer witchGamer = gamerDao.getWitch(roomName);
        String chance = witchGamer.getChance();
        String[] strings = chance.split(",");
        String newChance = strings[0] + ",0";
        gamerDao.poisonGamer(roomName, seatNumber);
        return gamerDao.setChance(roomName,newChance);
    }

    //夜晚结束，进入白天，需判断谁死了，游戏是否结束
    public Integer leaveNight(Integer roomName) {
        gamerDao.setState(roomName,2,3);
        List<Integer> seatList = gamerDao.deadSeat(roomName);

        int size = seatList.size();
        int whoSpeak;
        if (size == 0){
            whoSpeak = 1;
        }else {
            whoSpeak = (seatList.get(0)) % 9 + 1;
        }

        gamerDao.setState(roomName,3,4);
        int winner = getWinner(roomName);

        Map<String,String> seatMap = new HashMap<>();
        seatMap.put("winner",winner+"");
        seatMap.put("whoSpeak",whoSpeak+"");    //第一个该谁说话，规定为死亡的下家或第一个
        seatMap.put("deadNumber",size+"");      //当天晚上死亡人数
        int i = 1;
        for (Integer one : seatList){
            seatMap.put("dead"+i,one+"");
            i++;
        }

        gamerDao.clearCount(roomName);
        sendBroadCast = new SendBroadCast();
        return sendBroadCast.pushDayPeriod(roomName,seatMap);
    }

    public Integer nextGamer(Integer roomName, Integer seatNumber) {
        gamerDao.setSpeakChance(roomName,seatNumber);
        List<Integer> seatList = gamerDao.getSpeakSeat(roomName);
        int nextSeat;
        if (seatList == null){
            gamerDao.clearChance(roomName);
            nextSeat = 0;
        }else {
            nextSeat = seatNumber;
            int flag = 0;
            int count = 0;
            while (flag == 0 || count < 9){
                nextSeat = (seatNumber % 9) + 1;
                for (Integer one : seatList){
                    if (one == nextSeat){
                        flag = 1;
                        break;
                    }
                }
                count++;
            }
        }
        sendBroadCast = new SendBroadCast();
        return sendBroadCast.pushNextGamer(roomName,nextSeat);
    }

    public int voteGamer(Integer roomName, Integer seatNumber) {
        int flag = gamerDao.setCountByWolf(roomName,seatNumber);
        if(flag > 0){
            return 200;
        }else {
            return 222;
        }
    }

    public Integer countResult(Integer roomName) {
        List<Integer> countList = gamerDao.getCount(roomName);
        int max = 0;
        int max_seat = judgeDead(max,countList);
        if (max_seat > 0){
            gamerDao.setOuter(roomName,max_seat);
        }
        int winner = getWinner(roomName);
        sendBroadCast = new SendBroadCast();
        return sendBroadCast.pushOuter(roomName,max_seat,max,winner);
    }


    private Integer judgeDead(int max,List<Integer> countList){
        int second_max = 0;
        int max_seat = 0;
        int i = 1;
        for (Integer one : countList) {
            if (max < one) {
                second_max = max;
                max = one;
                max_seat = i;
            } else if (max == one) {
                second_max = one;
            } else {
                if (second_max < one) {
                    second_max = one;
                }
            }
            i++;
        }
        if (max != second_max){
            return max_seat;
        }else {
            return 0;
        }
    }

    private Integer getWinner(Integer roomName){
        List<Integer> identityList = gamerDao.getAllDead(roomName);
        int c_people = 0,c_wolf = 0 ,c_god = 0,winner = 0;
        for (Integer one : identityList){
            if (one == 1){
                c_people++;
                if (c_people == 3){
                    winner = 2;
                    break;
                }
            }else if(one == 2){
                c_wolf++;
                if (c_wolf == 3){
                    winner = 1;
                    break;
                }
            }else {
                c_god++;
                if (c_god == 3){
                    winner = 2;
                    break;
                }
            }
        }
        return winner;
    }

}
