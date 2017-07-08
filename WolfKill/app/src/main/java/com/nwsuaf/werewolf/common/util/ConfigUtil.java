package com.nwsuaf.werewolf.common.util;

/**
 * Created by xiaoheng and liulin on 2017/6/22.
 */

public interface ConfigUtil {
    /**
     * 从主界面调到游戏界面传递的key值
     */
    String EXTRA_MAIN_TO_GAME = "com.nwsuaf.werewolf.extras.EXTRAMAINTOGAME";

    /**
     * 从主界面传到游戏界面的房间号的key值
     */
    String EXTRA_GAME_HOME_NUMBER = "com.nwsuaf.werewolf.extras.EXTRAGAMEHOMENUMBER";
    /*获取用户名片的地址*/
    String GET_USER_INFO = "https://api.netease.im/nimserver/user/getUinfos.action";
    /*更新用户的名片*/
    String UPDATE_USER_INFO = "https://api.netease.im/nimserver/user/updateUinfo.action";
    //注册地址
    String API_SERVER = "https://api.netease.im/nimserver/user/create.action";
    //APP密钥
    String APP_SECRET = "d99e568ea26c";

    //对话框的空间的文本设置
    /*判断是哪个动作或者哪个请求了对话框*/
    String REQUEST_CODE = "who_action_request";
    String DIALOG_TV_TITLE = "dialog_tv_tile";
    String DIALOG_TV_CONTENT = "dialog_tv_content";
    String DIALOG_ET_CONTENT = "dialog_et_content";
    String DIALOG_BT_LEFT = "dialog_bt_left";
    String DIALOG_BT_CENTER = "dialog_bt_center";
    String DIALOG_BT_RIGHT = "dialog_bt_right";

    String DIALOG_TO_MAIN = "dialog_to_main";

    String DIALOG_TO_GAME = "dialog_to_game";
    //后端接口地址
    String SERVER_ROOM_URL = "http://172.29.22.40:8080/campus-item/room/room_";

    String SERVER_GAMER_URL = "http://172.29.22.40:8080/campus-item/gamer/gamer_";
    //查找一个简单模式房间
    String HTTP_FINDSIMPLE = "findSimple";
    // 查找一个高级模式房间
    String HTTP_FINDHARD = "findHard";
    //查询指定房间号
    String HTTP_GETROOM = "get?roomName=";
    // 创建一个类型为简单或高级的房间
    String HTTP_ROOM_INSERT = "insert?type=";
    //删除指定房间
    String HTTP_ROOM_DELETE = "delete?roomName=";
    //增加房间人数（加入时更新）
    String HTTP_GAMER_ADD = "addGamer";
    //改变房主
    String HTTP_GAMER_CHANGEROWNER = "changeOwner";
    //减少房间人数（退出时更新）
    String HTTP_GAMER_REMOVE = "removeGamer";
    //2-3游戏中途有人退出
    String HTTP_GAMER_EXITROOM = "exitRoom";
    //准备请求
    String HTTP_GAMER_READY = "ready";
    //取消准备
    String HTTP_GAMER_UNREADY = "unready";
    //随机分配身份并开始游戏
    String HTTP_GAMER_START = "startGame";
    //获得狼人列表，客户端准备开启狼人音频
    String HTTP_GAMER_GETWOLF = "getWolf";
    //7.狼人选择要杀的人
    String HTTP_GAMER_PICKGAMERBYWOLF = "pickGamerBywolf";
    //8.狼人阶段结束，通知预言家进入预言家阶段
    String HTTP_GAMER_SEER = "gotoSeerPeriod";
    //9.预言家阶段结束，通知女巫进入女巫阶段
    String HTTP_GAMER_WITCH = "gotoWitchPeriod";
    //10.女巫救人
    String HTTP_GAMER_SAVA = "saveGamer";
    //11.女巫不救
    String HTTP_GAMER_NOT_SAVA = "notSaveGamer";
    //12.女巫毒人
    String HTTP_GAMER_POISON = "poisonGamer";
    //13.夜晚结束，进入白天，通知谁死了，游戏是否结束
    String HTTP_GAMER_LEAVENIGHT = "leaveNight";
    //14.每个玩家发言结束，推送下一个玩家发言，并判断发言是否结束
    String HTTP_GAMER_NEXTGAME = "nextGamer";
    //
    String HTTP_GAMER_VOTEGAMER = "voteGamer";
    //
    String HTTP_GAMER_COUNTRESULT = "countResult";
}
