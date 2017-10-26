package cn.edu.nwsuaf.liuzc.utils;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 林中漫步 on 2017/10/22.
 */
public class SendBroadCast {
    private JPushClient jPushClient;

    public SendBroadCast(){
        String APP_KEY = "6f39386489e150a5e0ccbc16";
        String MASTER_SECRET = "0e27b4569c6c30971794fb57";
        jPushClient = new JPushClient(MASTER_SECRET, APP_KEY, null, ClientConfig.getInstance());

    }

    //有新用户加入时推送消息给同一个房间所有用户
    public int pushNewUser(Integer roomName,String username,Integer seatNumber){
        PushPayload pushPayload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.tag(roomName+""))
                .setMessage(Message.newBuilder()
                        .setTitle("new_user")
                        .setMsgContent("有新玩家加入")
                        .addExtra("username",username)
                        .addExtra("seatNumber",seatNumber)
                        .build())
                .build();
        return sendPushContent(pushPayload);
    }

    //有用户退出时推送消息给同一个房间所有用户
    public int pushExitUser(Integer roomName,Integer seatNumber){
        PushPayload pushPayload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.tag(roomName+""))
                .setMessage(Message.newBuilder()
                        .setTitle("exit_user")
                        .setMsgContent("有玩家退出")
                        .addExtra("seatNumber",seatNumber)
                        .build())
                .build();
        return sendPushContent(pushPayload);
    }

    //推送已准备好的玩家
    public int pushReady(Integer roomName,Integer seatNumber,int readyNumber) {
        PushPayload pushPayload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.tag(roomName+""))  //向roomName标签内玩家发送准备消息
                .setMessage(Message.newBuilder()
                        .setTitle("ready_status")
                        .setMsgContent("准备好的玩家")
                        .addExtra("seatNumber",seatNumber)
                        .addExtra("readyNumber",readyNumber)
                        .build())
                .build();

        return sendPushContent(pushPayload);
    }

    //推送取消准备的玩家
    public int pushUnready(Integer roomName,Integer seatNumber){
        PushPayload pushPayload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.tag(roomName+""))
                .setMessage(Message.newBuilder()
                        .setTitle("unready_status")
                        .setMsgContent("取消准备的玩家")
                        .addExtra("seatNumber",seatNumber)
                        .build())
                .build();

        return sendPushContent(pushPayload);
    }

    //通知指定房间玩家开始游戏
    public Integer pushStart(Integer roomName, List<Integer> identityList) {
        Map<String,String> identityMap = new HashMap<>();
        int i = 1;
        for (Integer one : identityList){
            identityMap.put(i+"",one+"");
            i++;
        }
        PushPayload pushPayload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.tag(roomName+""))
                .setMessage(Message.newBuilder()
                        .setTitle("start_game")
                        .setMsgContent("天黑请闭眼")
                        .addExtras(identityMap)
                        .build())
                .build();
        return sendPushContent(pushPayload);
    }

    //向所有存活的狼人推送狼人开始杀人消息
    public Integer pushWolfToWolf(Integer roomName, List<String> wolfNameList, List<Integer> wolfSeatList) {
        Map<String,String> seatMap = new HashMap<>();
        int i = 1;
        seatMap.put("wolfNumber",wolfSeatList.size()+"");
        for (Integer one : wolfSeatList){
            seatMap.put("wolf"+i,one+"");
            i++;
        }

        PushPayload pushPayload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.tag(roomName+""))
                        .addAudienceTarget(AudienceTarget.alias(wolfNameList))
                        .build())
                .setMessage(Message.newBuilder()
                        .setTitle("wolf_list")
                        .setMsgContent("狼人阶段开启")
                        .addExtras(seatMap)
                        .build())
                .build();
        return sendPushContent(pushPayload);
    }

    //推送狼人投票的座次号
    public Integer pushWhoToWolf(Integer roomName, List<String> wolfNameList, List<Integer> countList){
        Map<String,String> countMap = new HashMap<>();
        int i = 1;
        for (Integer one : countList){
            countMap.put(i+"",one+"");
            i++;
        }
        PushPayload pushPayload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.tag(roomName+""))
                        .addAudienceTarget(AudienceTarget.alias(wolfNameList))
                        .build())
                .setMessage(Message.newBuilder()
                        .setTitle("wolf_picked")
                        .setMsgContent("被狼人选择的人的次数")
                        .addExtras(countMap)
                        .build())
                .build();
        return sendPushContent(pushPayload);
    }



    //通知预言家进入预言家阶段
    public Integer pushSeerPeriod(Integer roomName, String seerName) {
        PushPayload pushPayload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.tag(roomName+""))
                        .addAudienceTarget(AudienceTarget.alias(seerName))
                        .build())
                .setMessage(Message.newBuilder()
                        .setTitle("seer_period")
                        .setMsgContent("预言家选择要查看的身份")
                        .build())
                .build();
        return sendPushContent(pushPayload);
    }

    //通知女巫进入女巫阶段，返回暂时死亡的玩家(可能没有）和女巫的机会
    public Integer pushWitchPeriod(Integer roomName,String witchName, String chance, Integer dyingSeat) {
        PushPayload pushPayload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.tag(roomName+""))
                        .addAudienceTarget(AudienceTarget.alias(witchName))
                        .build())
                .setMessage(Message.newBuilder()
                        .setTitle("witch_period")
                        .setMsgContent("女巫阶段开始")
                        .addExtra("chance",chance)
                        .addExtra("dyingSeat",dyingSeat)
                        .build())
                .build();
        return sendPushContent(pushPayload);
    }

    //推送给所有玩家黑夜结束，有哪些玩家死亡和发言的玩家的座次号
    public Integer pushDayPeriod(Integer roomName, Map<String,String> seatMap) {
        PushPayload pushPayload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.tag(roomName+""))
                .setMessage(Message.newBuilder()
                        .setTitle("day_period")
                        .setMsgContent("黑夜结束")
                        .addExtras(seatMap)
                        .build())
                .build();
        return sendPushContent(pushPayload);
    }

    //推送下一个发言的玩家
    public Integer pushNextGamer(Integer roomName, int nextSeat) {
        PushPayload pushPayload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.tag(roomName+""))
                .setMessage(Message.newBuilder()
                        .setTitle("next_gamer")
                        .setMsgContent("下个玩家")
                        .addExtra("nextSeat",nextSeat)
                        .build())
                .build();
        return sendPushContent(pushPayload);
    }

    //推送投票出局的玩家，以及游戏是否结束
    public Integer pushOuter(Integer roomName, int max_seat, int max, int winner) {
        PushPayload pushPayload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.tag(roomName+""))
                .setMessage(Message.newBuilder()
                        .setTitle("outer")
                        .setMsgContent("投票死亡的玩家")
                        .addExtra("max_seat",max_seat)
                        .addExtra("max",max)
                        .addExtra("winner",winner)
                        .build())
                .build();
        return sendPushContent(pushPayload);
    }

    private Integer sendPushContent(PushPayload pushPayload){
        try {
            PushResult pushResult = jPushClient.sendPush(pushPayload);
            System.out.println("Got result - " + pushResult);
            return 200;
        } catch (APIConnectionException e) {
            System.out.println("连接失败");
            return 223;
        } catch (APIRequestException e) {
            System.out.println("Should review the error, and fix the request" + e);
            System.out.println("HTTP Status: " + e.getStatus());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            return 224;
        }
    }
}
