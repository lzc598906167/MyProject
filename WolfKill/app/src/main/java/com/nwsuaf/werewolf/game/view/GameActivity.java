package com.nwsuaf.werewolf.game.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatUserRole;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoCropRatio;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatParameters;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoRender;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.nwsuaf.werewolf.DemoCache;
import com.nwsuaf.werewolf.R;
import com.nwsuaf.werewolf.WolfKillApplication;
import com.nwsuaf.werewolf.common.util.ConfigUtil;
import com.nwsuaf.werewolf.game.presenter.GamePresenter;
import com.nwsuaf.werewolf.game.presenter.IGamePresenter;
import com.nwsuaf.werewolf.httputil.HttpGameUtil;
import com.nwsuaf.werewolf.receiver.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 游戏界面
 * Created by xiaoheng and liulin on 2017/6/22.
 */

public class GameActivity extends AppCompatActivity implements ConfigUtil, IGameView {
    private static final String TAG = "AV_GAME";
    private int type;
    private ImageView iv_lock1;
    private ImageView iv_lock2;
    private ImageView iv_lock3;
    private TextView tv_time, game_tv_info;
    private HeadImageView civ_gamer10;
    private HeadImageView civ_gamer11;
    private HeadImageView civ_gamer12;
    private Button btn_speak;
    private String roomName;
    private IGamePresenter gamePresenter;
    private AVChatVideoRender avView;
    private Timer timer;
    //获取用户信息
    private NimUserInfo userInfo;
    //头像HeadImageView
    private HeadImageView civ_gamer1, civ_gamer2, civ_gamer3, civ_gamer4, civ_gamer5, civ_gamer6, civ_gamer7, civ_gamer8, civ_gamer9;
    //背景layout
    private RelativeLayout game_rl_1, game_rl_2, game_rl_3, game_rl_4, game_rl_5, game_rl_6, game_rl_7, game_rl_8, game_rl_9;
    //投票的TextView
    private TextView game_tv1, game_tv2, game_tv3, game_tv4, game_tv5, game_tv6, game_tv7, game_tv8, game_tv9;
    //准备
    private Switch play_game_switch;
    //开始
    private Button play_game_start;

    private ImageView game_star1, game_star2, game_star3, game_star4, game_star5, game_star6, game_star7, game_star8, game_star9;

    private TextView game_tv_identity1, game_tv_identity2;
    public static int state = -1;//状态为2是狼人，状态为3是预言家，状态为4是女巫
    private boolean canSava;
    private boolean canPosion;
    //死亡的座位号
    private int dyingSeat;
    private int winner;
    private static int whoSpeak;
    private String deadString;
    public static boolean startGame = false;
    public int readyNumber = 0;
    private FrameLayout play_game_main;
    private FrameLayout play_game_ready;
    private LinearLayout avView_father;
    private TextView play_game_roomNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.play_game);
        initViews();
        initListeners();
        gamePresenter = new GamePresenter(this, this, avView);
        type = getIntent().getIntExtra(EXTRA_MAIN_TO_GAME, 1);
        roomName = getIntent().getStringExtra(EXTRA_GAME_HOME_NUMBER);
        WolfKillApplication.roomName = roomName;
        Log.i(TAG, roomName);
        //将不用的头像置灰并且锁定
        if (type == 1) {
            iv_lock1.setVisibility(View.VISIBLE);
            iv_lock2.setVisibility(View.VISIBLE);
            iv_lock3.setVisibility(View.VISIBLE);
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            civ_gamer10.setColorFilter(filter);
            civ_gamer11.setColorFilter(filter);
            civ_gamer12.setColorFilter(filter);
        }
        if (!TextUtils.isEmpty(WolfKillApplication.hostAccount)) {
            //创建房间的人的界面会改变
            if (DemoCache.getAccount().equals(WolfKillApplication.hostAccount))
                updateOwnerAction();
        }
        registerMessageReceiver();
        //加入房间
        onJoinRoom();
    }

    private void initListeners() {
        btn_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seconds = 1;
               /* AVChatManager.getInstance().stopVideoPreview();
                //释放view连接，重新绑定用户
                avView.release();
                AVChatManager.getInstance().setupRemoteVideoRender("jack", avView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);*/
            }
        });
        play_game_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    HttpGameUtil httpGameUtil = new HttpGameUtil();
                    httpGameUtil.gameReady(getBaseContext(), WolfKillApplication.roomName, DemoCache.getAccount());
                } else {
                    HttpGameUtil httpGameUtil = new HttpGameUtil();
                    httpGameUtil.gameUnReady(getBaseContext(), WolfKillApplication.roomName, DemoCache.getAccount(), WolfKillApplication.seatNumber + "");
                }
            }
        });
        play_game_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame = true;
                HttpGameUtil httpGameUtil = new HttpGameUtil();
                httpGameUtil.startGame(getBaseContext(), WolfKillApplication.roomName);
            }
        });

    }


    private void onJoinRoom() {
        gamePresenter.joinRoom(roomName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            String action = data.getStringExtra(DIALOG_TO_GAME);
            if (action.equals("救") || action.equals("不救")) {
                if (action.equals("救")) {
                    //请求
                    HttpGameUtil httpGameUtil = new HttpGameUtil();
                    httpGameUtil.saveGamer(getBaseContext(), roomName);
                }
                if (canPosion) {
                    Intent intent = new Intent(GameActivity.this, GameDialog.class);
                    intent.putExtra(REQUEST_CODE, "poisonOrNotGamer");
                    intent.putExtra(DIALOG_TV_TITLE, "提示消息");
                    intent.putExtra(DIALOG_TV_CONTENT, "你可以选择毒人或者不毒人");
                    intent.putExtra(DIALOG_BT_LEFT, "毒");
                    intent.putExtra(DIALOG_BT_RIGHT, "不毒");
                    startActivityForResult(intent, 1);
                    canPosion = false;
                } else {
                    game_tv_info.setText("你没有毒人的权力");
                }
            } else if (action.equals("毒")) {
                state = 4;
                game_tv_info.setText("点击头像进行毒人");
            }
        }
    }

    @Override
    public void onFinish() {
        Toast.makeText(getBaseContext(), "该房间不存在", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void finish() {
        Set<String> tagSet = new LinkedHashSet<String>();
        //当用户离开之后更新后台数据
        JPushInterface.setAliasAndTags(getApplicationContext(), "", tagSet, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });
        if (startGame) {
            Log.i("TAG", "onUserLeave");
            //在游戏中离开
            HttpGameUtil httpGameUtil = new HttpGameUtil();
            httpGameUtil.exitRoom(getApplicationContext(), roomName, DemoCache.getAccount());
        } else if (!startGame) {
            //不在游戏中离开
            Log.i("TAG", "onUserLeave");

            HttpGameUtil httpGameUtil = new HttpGameUtil();
            httpGameUtil.removeGamer(getApplicationContext(), roomName, DemoCache.getAccount());
        }

        super.finish();
    }

    @Override
    public void loadAllImage(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");
            if (code == 200) {
                WolfKillApplication.hostAccount = jsonObject.getString("owner");
                JSONArray jsonArray = jsonObject.getJSONArray("gamers");
                Log.i("loadAllImage", "owner" + json);
                Log.i("loadAllImage", json);
                if (jsonArray != null) {
                    Log.i("loadAllImage", "aaaaaaaaaaaaa");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String userName = object.getString("username");
                        int seatNumber = object.getInt("seatNumber");
                        int state = object.getInt("state");
//                        WolfKillApplication.gamers[seatNumber] = new Gamer();
                        WolfKillApplication.gamers[seatNumber].setUsername(userName);
                        WolfKillApplication.gamers[seatNumber].setSeatNumber(seatNumber);
                        WolfKillApplication.gamers[seatNumber].setState(state);

                    }
                    MyTask myTask = new MyTask();
                    myTask.execute();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOwnerAction() {
        play_game_switch.setVisibility(View.GONE);
        play_game_start.setVisibility(View.VISIBLE);
        play_game_start.setEnabled(false);
    }

    @Override
    public void onShowRoomNum(String roomName) {
        play_game_roomNum.setText(roomName);
    }


    //接收MyReceiver发来的消息
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_TO_GAMEACTIVITY = "com.nwsuaf.werewolf.game.view.GameActity";
    public static final String BUNDLE_TO_GAME = "bundle_to_game";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_TO_GAMEACTIVITY);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_TO_GAMEACTIVITY.equals(intent.getAction())) {
                    Bundle bundle = intent.getBundleExtra(BUNDLE_TO_GAME);
                    String msg_content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                    String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
                    JSONObject extraJson = null;
                    Log.i("GAME", "+" + extras);
                    if (!extras.equals("") && extras != null)
                        extraJson = new JSONObject(extras);
                    Log.i("GAME", "+" + extraJson);
                    if (msg_content.equals("有新玩家加入")) {
                        String account = extraJson.getString("username");
                        int seatNumber = extraJson.getInt("seatNumber");
                        if (DemoCache.getAccount().equals(account)) {
                            WolfKillApplication.seatNumber = seatNumber;
                            ImageView imageView = getStarImage(seatNumber);
                            imageView.setVisibility(View.VISIBLE);
                        }
                        Log.i("GAME", "+" + account);
                        Log.i("GAME", "+" + seatNumber);
//                        WolfKillApplication.gamers[seatNumber] = new Gamer();
                        WolfKillApplication.gamers[seatNumber].setUsername(account);
                        WolfKillApplication.gamers[seatNumber].setSeatNumber(seatNumber);
                        WolfKillApplication.gamers[seatNumber].setState(0);
                        getUserInfo(account, seatNumber);
                    } else if (msg_content.equals("有玩家退出")) {
                        int seatNumber = extraJson.getInt("seatNumber");
                        updateUI("", seatNumber);
                        WolfKillApplication.clearGamer();
                    } else if (msg_content.equals("准备好的玩家")) {
                        int seatNumber = extraJson.getInt("seatNumber");
                        RelativeLayout rl = getRLayout(seatNumber);
                        if (rl != null)
                            rl.setBackgroundColor(getBaseContext().getResources().getColor(R.color.green_4DC0A4));
                        readyNumber = extraJson.getInt("readyNumber");
                        if (readyNumber == 8) {
                            play_game_start.setEnabled(true);
                        }
                    } else if (msg_content.equals("取消准备的玩家")) {
                        int seatNumber = extraJson.getInt("seatNumber");
                        readyNumber--;
                        play_game_start.setEnabled(false);
                        RelativeLayout rl = getRLayout(seatNumber);
                        if (rl != null)
                            rl.setBackgroundColor(getBaseContext().getResources().getColor(R.color.transplant));
                    } else if (msg_content.equals("天黑请闭眼")) {
                        play_game_main.setBackground(getBaseContext().getResources().getDrawable(R.drawable.color_head));
                        game_tv_info.setTextColor(Color.WHITE);
                        play_game_ready.setVisibility(View.GONE);
                        btn_speak.setVisibility(View.VISIBLE);
                        AVChatManager.getInstance().stopVideoPreview();
                        //释放view连接
                        avView.release();
                        avView.setVisibility(View.GONE);

                        for (int i = 1; i < 10; i++) {
                            RelativeLayout relativeLayout = getRLayout(i);
                            if (relativeLayout != null)
                                relativeLayout.setBackgroundColor(getBaseContext().getResources().getColor(R.color.transplant));
                            int identity = extraJson.getInt(i + "");
                            WolfKillApplication.gamers[i].setIdentity(identity);
                            if (WolfKillApplication.gamers[i].getUsername().equals(DemoCache.getAccount())) {
                                WolfKillApplication.identity = identity;
                                switch (identity) {
                                    case 1:
                                        game_tv_identity2.setText("平民");
                                        break;
                                    case 2:
                                        game_tv_identity2.setText("狼人");
                                        break;
                                    case 3:
                                        game_tv_identity2.setText("预言家");
                                        break;
                                    case 4:
                                        game_tv_identity2.setText("女巫");
                                        break;
                                    case 5:
                                        game_tv_identity2.setText("猎人");
                                        break;
                                }
                            }
                        }
                        game_tv_info.setText(msg_content);
                        seconds = 5;
                        MyTimerTask m = new MyTimerTask(msg_content);
                        startTimer(m);
                    } else if (msg_content.equals("狼人阶段开启")) {
                        state = 2;
                        int wolfNumber = extraJson.getInt("wolfNumber");
                        for (int i = 1; i <= wolfNumber; i++) {
                            int w = extraJson.getInt("wolf" + i);
                            RelativeLayout rl = getRLayout(w);
                            if (rl != null)
                                rl.setBackgroundColor(getBaseContext().getResources().getColor(R.color.color_red_ccfa3c55));
                        }
                        //////////////
                        /*AVChatManager.getInstance().enableAudienceRole(false);
                        AVChatManager.getInstance().muteLocalAudio(false);
                        AVChatManager.getInstance().muteRemoteAudio(DemoCache.getAccount(), false);*/
                        //////////////
                        seconds = 15;
                        MyTimerTask m = new MyTimerTask(msg_content);
                        startTimer(m);
                        game_tv_info.setText(msg_content);
                    } else if (msg_content.equals("被狼人选择的人的次数")) {
                        for (int i = 1; i < 10; i++) {
                            int count = extraJson.getInt(i + "");
                            WolfKillApplication.gamers[i].setCount(count);
                            if (WolfKillApplication.identity == 2 && count != 0) {
                                TextView tv = getTView(i);
                                tv.setVisibility(View.VISIBLE);
                                tv.setText(count + "");
                            }
                        }
                    } else if (msg_content.equals("预言家选择要查看的身份")) {
                        state = 3;
                        seconds = 10;
                        MyTimerTask m = new MyTimerTask(msg_content);
                        game_tv_info.setText("请选择要查看的身份");
                        startTimer(m);
                    } else if (msg_content.equals("女巫阶段开始")) {
                        state = 4;
                        String chance = extraJson.getString("chance");
                        dyingSeat = extraJson.getInt("dyingSeat");
                        seconds = 15;
                        MyTimerTask m = new MyTimerTask(msg_content);
                        startTimer(m);
                        if (chance.equals("0,0")) {
                            game_tv_info.setText("你没有技能了,已经不能救人和毒人");
                        } else if (chance.equals("1,0")) {
                            canPosion = false;
                            if (dyingSeat == 0) {
                                game_tv_info.setText("没有人死亡，你没有毒人的权力");
                                return;
                            } else {
                                Intent data = new Intent(GameActivity.this, GameDialog.class);
                                data.putExtra(REQUEST_CODE, "saveOrNotGamer");
                                data.putExtra(DIALOG_TV_TITLE, "提示消息");
                                data.putExtra(DIALOG_TV_CONTENT, dyingSeat + "号玩家死亡,救或者不救");
                                data.putExtra(DIALOG_BT_LEFT, "救");
                                data.putExtra(DIALOG_BT_RIGHT, "不救");
                                startActivityForResult(data, 1);
                            }
                        } else if (chance.equals("0,1")) {
                            canPosion = false;
                            game_tv_info.setText("你没有救人的权力");
                            Intent data = new Intent(GameActivity.this, GameDialog.class);
                            data.putExtra(REQUEST_CODE, "poisonOrNotGamer");
                            data.putExtra(DIALOG_TV_TITLE, "提示消息");
                            data.putExtra(DIALOG_TV_CONTENT, "你可以选择毒人或者不毒人");
                            data.putExtra(DIALOG_BT_LEFT, "毒");
                            data.putExtra(DIALOG_BT_RIGHT, "不毒");
                            startActivityForResult(data, 1);
                        } else if (chance.equals("1,1")) {
                            if (dyingSeat == 0) {
                                canPosion = false;
                                game_tv_info.setText("没有人死亡,选择是否毒人");
                                Intent data = new Intent(GameActivity.this, GameDialog.class);
                                data.putExtra(REQUEST_CODE, "poisonOrNotGamer");
                                data.putExtra(DIALOG_TV_TITLE, "提示消息");
                                data.putExtra(DIALOG_TV_CONTENT, "你可以选择毒人或者不毒人");
                                data.putExtra(DIALOG_BT_LEFT, "毒");
                                data.putExtra(DIALOG_BT_RIGHT, "不毒");
                                startActivityForResult(data, 1);

                            } else {
                                canPosion = true;
                                Intent data = new Intent(GameActivity.this, GameDialog.class);
                                data.putExtra(REQUEST_CODE, "saveOrNotGamer");
                                data.putExtra(DIALOG_TV_TITLE, "提示消息");
                                data.putExtra(DIALOG_TV_CONTENT, dyingSeat + "号玩家死亡,救或者不救");
                                data.putExtra(DIALOG_BT_LEFT, "救");
                                data.putExtra(DIALOG_BT_RIGHT, "不救");
                                startActivityForResult(data, 1);
                            }
                        }
                    } else if (msg_content.equals("黑夜结束")) {
                        WolfKillApplication.isFirst = true;
                        play_game_main.setBackgroundColor(getBaseContext().getResources().getColor(R.color.colorWhite));
                        game_tv_info.setTextColor(Color.BLACK);
                        for (int i = 1; i < 10; i++) {
                            RelativeLayout relativeLayout = getRLayout(i);
                            if (relativeLayout != null)
                                relativeLayout.setBackgroundColor(getBaseContext().getResources().getColor(R.color.transplant));
                        }
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }

                        winner = extraJson.getInt("winner");
                        whoSpeak = extraJson.getInt("whoSpeak");
                        if (WolfKillApplication.gamers[whoSpeak].getUsername().equals(DemoCache.getAccount())) {
                            avView_father.removeAllViews();
                            AVChatVideoRender avView4 = new AVChatVideoRender(getBaseContext());
                            avView_father.addView(avView4,new LinearLayout.LayoutParams
                                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            avView4.setVisibility(View.VISIBLE);
                            Log.i("zzzzzzzzzzzzzzz",WolfKillApplication.gamers[whoSpeak].getUsername());
                            AVChatManager.getInstance().setupLocalVideoRender(avView4, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                            AVChatManager.getInstance().startVideoPreview();
                            AVChatManager.getInstance().muteLocalAudio(false);
                            AVChatManager.getInstance().muteLocalVideo(false);
                        }else if (WolfKillApplication.seatNumber == 1 || WolfKillApplication.seatNumber == 2){
                            Handler handler0 = new Handler();
                            handler0.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    avView_father.removeAllViews();
                                    AVChatVideoRender avView2 = new AVChatVideoRender(getBaseContext());
                                    avView_father.addView(avView2,new LinearLayout.LayoutParams
                                            (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                                    avView2.setVisibility(View.VISIBLE);
                                    Log.i("zzzzzzzzzzzzzzz",WolfKillApplication.gamers[whoSpeak].getUsername());
                                    AVChatManager.getInstance().setupRemoteVideoRender(WolfKillApplication.gamers[whoSpeak].getUsername(),
                                            avView2, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                                }
                            }, 5000);//5秒后执行Runnable中的run方法

                        }

                        seconds = 5;
                        MyTimerTask myTimerTask = new MyTimerTask("黑夜结束");
                        startTimer(myTimerTask);
                        int deadNumber = extraJson.getInt("deadNumber");
                        switch (deadNumber) {
                            case 0:
                                game_tv_info.setText("天亮了,昨天晚上没有人死亡");
                                break;
                            case 1:
                                int dead1 = extraJson.getInt("dead1");
                                changeState(dead1);
                                game_tv_info.setText("天亮了," + dead1 + "号玩家死亡");
                                break;
                            case 2:
                                int dead2 = extraJson.getInt("dead1");
                                changeState(dead2);
                                int dead3 = extraJson.getInt("dead2");
                                changeState(dead3);
                                game_tv_info.setText("天亮了," + dead2 + "号玩家和" + dead3 + "号玩家死亡");
                                break;
                        }

                    } else if (msg_content.equals("下个玩家")) {
                        if (WolfKillApplication.gamers[whoSpeak].getUsername().equals(DemoCache.getAccount())) {
                                AVChatManager.getInstance().muteLocalAudio(true);
                                AVChatManager.getInstance().muteLocalVideo(true);

                            AVChatManager.getInstance().stopVideoPreview();
                            newAvView(getBaseContext());
                        }

                        whoSpeak = extraJson.getInt("nextSeat");
                        if (whoSpeak == 0) {
                            seconds = 10;
                            state = 1;
                            MyTimerTask m = new MyTimerTask("玩家开始投票");
                            startTimer(m);
                        } else {
                            if (WolfKillApplication.gamers[whoSpeak].getUsername().equals(DemoCache.getAccount())) {
                                avView_father.removeAllViews();
                                AVChatVideoRender avView3 = new AVChatVideoRender(getBaseContext());
                                avView_father.addView(avView3,new LinearLayout.LayoutParams
                                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                                avView3.setVisibility(View.VISIBLE);
                                Log.i("zzzzzzzzzzzzzzz",WolfKillApplication.gamers[whoSpeak].getUsername());
                                AVChatManager.getInstance().setupLocalVideoRender(avView3, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                                AVChatManager.getInstance().startVideoPreview();
                                AVChatManager.getInstance().muteLocalAudio(false);
                                AVChatManager.getInstance().muteLocalVideo(false);
                            }else if (WolfKillApplication.seatNumber == 1 || WolfKillApplication.seatNumber == 2){
                                Handler handler0 = new Handler();
                                handler0.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        avView_father.removeAllViews();
                                        AVChatVideoRender avView6 = new AVChatVideoRender(getBaseContext());
                                        avView_father.addView(avView6,new LinearLayout.LayoutParams
                                                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                                        avView6.setVisibility(View.VISIBLE);
                                        Log.i("zzzzzzzzzzzzzzz",WolfKillApplication.gamers[whoSpeak].getUsername());
                                        AVChatManager.getInstance().setupRemoteVideoRender(WolfKillApplication.gamers[whoSpeak].getUsername(),
                                                avView6, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                                    }
                                }, 2000);//5秒后执行Runnable中的run方法
                            }
                            seconds = 2;
                            MyTimerTask m = new MyTimerTask("游戏是否结束判断");
                            startTimer(m);
                        }
                    } else if (msg_content.equals("投票死亡的玩家")) {
                        int max_seat = extraJson.getInt("max_seat");
                        int max = extraJson.getInt("max");
                        winner = extraJson.getInt("winner");
                        seconds = 5;
                        MyTimerTask m = new MyTimerTask("投票死亡的玩家");
                        startTimer(m);
                        if (max_seat == 0)
                            game_tv_info.setText("没有人死亡");
                        else {
                            changeState(max_seat);
                            game_tv_info.setText(max_seat + "号人死亡了,票数是" + max);
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //获得用户信息
    private void getUserInfo(final String account, final int seatNumber) {
        userInfo = NimUserInfoCache.getInstance().getUserInfo(account);
        if (userInfo == null) {
            NimUserInfoCache.getInstance().getUserInfoFromRemote(account, new RequestCallback<NimUserInfo>() {
                @Override
                public void onSuccess(NimUserInfo param) {
                    userInfo = param;
                    updateUI(account, seatNumber);
                }

                @Override
                public void onFailed(int code) {
                    Toast.makeText(GameActivity.this, "getUserInfoFromRemote failed:" + code, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onException(Throwable exception) {
                    Toast.makeText(GameActivity.this, "getUserInfoFromRemote exception:" + exception, Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            updateUI(account, seatNumber);
        }
    }


    //改变头像的颜色
    private void changeState(int seatNumber) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        HeadImageView hiv = getCivView(seatNumber);

        if (hiv != null) {
            hiv.setEnabled(false);
            hiv.setColorFilter(filter);
        }
    }

    //更新个人头像
    private void updateUI(String account, int seatNumber) {
        HeadImageView hiv = getCivView(seatNumber);
        if (hiv != null) {
            hiv.loadBuddyAvatar(account);
        }
    }

    //通过座位号获取对应头像
    private HeadImageView getCivView(int seatNumber) {
        switch (seatNumber) {
            case 1:
                return civ_gamer1;
            case 2:
                return civ_gamer2;
            case 3:
                return civ_gamer3;
            case 4:
                return civ_gamer4;
            case 5:
                return civ_gamer5;
            case 6:
                return civ_gamer6;
            case 7:
                return civ_gamer7;
            case 8:
                return civ_gamer8;
            case 9:
                return civ_gamer9;
        }
        return null;
    }

    //通过座位号改变投票的次数
    private TextView getTView(int seatNumber) {
        switch (seatNumber) {
            case 1:
                return game_tv1;
            case 2:
                return game_tv2;
            case 3:
                return game_tv3;
            case 4:
                return game_tv4;
            case 5:
                return game_tv5;
            case 6:
                return game_tv6;
            case 7:
                return game_tv7;
            case 8:
                return game_tv8;
            case 9:
                return game_tv9;
        }
        return null;
    }

    //获取头像后面的背景对象
    private RelativeLayout getRLayout(int seatNumber) {
        switch (seatNumber) {
            case 1:
                return game_rl_1;
            case 2:
                return game_rl_2;
            case 3:
                return game_rl_3;
            case 4:
                return game_rl_4;
            case 5:
                return game_rl_5;
            case 6:
                return game_rl_6;
            case 7:
                return game_rl_7;
            case 8:
                return game_rl_8;
            case 9:
                return game_rl_9;
        }
        return null;
    }

    private void initViews() {
        avView_father = (LinearLayout) findViewById(R.id.avView_father);
        play_game_roomNum = (TextView) findViewById(R.id.play_game_roomNum);

        play_game_main = (FrameLayout) findViewById(R.id.play_game_main);
        play_game_ready = (FrameLayout) findViewById(R.id.play_game_ready);

        iv_lock1 = (ImageView) findViewById(R.id.iv_lock1);
        iv_lock2 = (ImageView) findViewById(R.id.iv_lock2);
        iv_lock3 = (ImageView) findViewById(R.id.iv_lock3);
        tv_time = (TextView) findViewById(R.id.play_game_time);
        civ_gamer10 = (HeadImageView) findViewById(R.id.play_game_cv10);
        civ_gamer11 = (HeadImageView) findViewById(R.id.play_game_cv11);
        civ_gamer12 = (HeadImageView) findViewById(R.id.play_game_cv12);
        avView = (AVChatVideoRender) findViewById(R.id.play_game_avView);
        btn_speak = (Button) findViewById(R.id.play_game_bt_speak);

        civ_gamer1 = (HeadImageView) findViewById(R.id.play_game_cv1);
        civ_gamer2 = (HeadImageView) findViewById(R.id.play_game_cv2);
        civ_gamer3 = (HeadImageView) findViewById(R.id.play_game_cv3);
        civ_gamer4 = (HeadImageView) findViewById(R.id.play_game_cv4);
        civ_gamer5 = (HeadImageView) findViewById(R.id.play_game_cv5);
        civ_gamer6 = (HeadImageView) findViewById(R.id.play_game_cv6);
        civ_gamer7 = (HeadImageView) findViewById(R.id.play_game_cv7);
        civ_gamer8 = (HeadImageView) findViewById(R.id.play_game_cv8);
        civ_gamer9 = (HeadImageView) findViewById(R.id.play_game_cv9);

        game_rl_1 = (RelativeLayout) findViewById(R.id.game_rl_1);
        game_rl_2 = (RelativeLayout) findViewById(R.id.game_rl_2);
        game_rl_3 = (RelativeLayout) findViewById(R.id.game_rl_3);
        game_rl_4 = (RelativeLayout) findViewById(R.id.game_rl_4);
        game_rl_5 = (RelativeLayout) findViewById(R.id.game_rl_5);
        game_rl_6 = (RelativeLayout) findViewById(R.id.game_rl_6);
        game_rl_7 = (RelativeLayout) findViewById(R.id.game_rl_7);
        game_rl_8 = (RelativeLayout) findViewById(R.id.game_rl_8);
        game_rl_9 = (RelativeLayout) findViewById(R.id.game_rl_9);

        game_tv1 = (TextView) findViewById(R.id.game_tv1);
        game_tv2 = (TextView) findViewById(R.id.game_tv2);
        game_tv3 = (TextView) findViewById(R.id.game_tv3);
        game_tv4 = (TextView) findViewById(R.id.game_tv4);
        game_tv5 = (TextView) findViewById(R.id.game_tv5);
        game_tv6 = (TextView) findViewById(R.id.game_tv6);
        game_tv7 = (TextView) findViewById(R.id.game_tv7);
        game_tv8 = (TextView) findViewById(R.id.game_tv8);
        game_tv9 = (TextView) findViewById(R.id.game_tv9);


        game_star1 = (ImageView) findViewById(R.id.game_star1);
        game_star2 = (ImageView) findViewById(R.id.game_star2);
        game_star3 = (ImageView) findViewById(R.id.game_star3);
        game_star4 = (ImageView) findViewById(R.id.game_star4);
        game_star5 = (ImageView) findViewById(R.id.game_star5);
        game_star6 = (ImageView) findViewById(R.id.game_star6);
        game_star7 = (ImageView) findViewById(R.id.game_star7);
        game_star8 = (ImageView) findViewById(R.id.game_star8);
        game_star9 = (ImageView) findViewById(R.id.game_star9);

        game_tv_identity1 = (TextView) findViewById(R.id.game_tv_identity1);
        game_tv_identity2 = (TextView) findViewById(R.id.game_tv_identity2);
        game_tv_info = (TextView) findViewById(R.id.game_tv_info);

        play_game_switch = (Switch) findViewById(R.id.play_game_switch);

        play_game_start = (Button) findViewById(R.id.play_game_start);


    }

    public void onHivClick(View v) {
        int view_id = v.getId();
        if (state == -1)
            return;
        switch (view_id) {
            case R.id.play_game_cv1:
                dealOnClick(1);
                break;
            case R.id.play_game_cv2:
                dealOnClick(2);
                break;
            case R.id.play_game_cv3:
                dealOnClick(3);
                break;
            case R.id.play_game_cv4:
                dealOnClick(4);
                break;
            case R.id.play_game_cv5:
                dealOnClick(5);
                break;
            case R.id.play_game_cv6:
                dealOnClick(6);
                break;
            case R.id.play_game_cv7:
                dealOnClick(7);
                break;
            case R.id.play_game_cv8:
                dealOnClick(8);
                break;
            case R.id.play_game_cv9:
                dealOnClick(9);
                break;
            default:
                break;
        }

    }

    private void dealOnClick(int number) {
        if (state == 2) {
            HttpGameUtil httpGameUtil = new HttpGameUtil();
            httpGameUtil.pickGamerByWolf(getBaseContext(), roomName, number + "");
            state = -1;
            return;
        }
        if (state == 3) {
            String identity;
            if (WolfKillApplication.gamers[number].getIdentity() == 4) {
                identity = "女巫";
            } else if (WolfKillApplication.gamers[number].getIdentity() == 2) {
                identity = "狼人";
            } else if (WolfKillApplication.gamers[number].getIdentity() == 3) {
                identity = "预言家";
            } else {
                identity = "平民";
            }
            game_tv_info.setText(number + "号玩家的身份是" + identity);
            state = -1;
            return;
        }
        if (state == 4) {
            //处理被毒的操作
            RelativeLayout rl = getRLayout(number);
            rl.setBackgroundColor(getBaseContext().getResources().getColor(R.color.yellow));
            HttpGameUtil httpGameUtil = new HttpGameUtil();
            httpGameUtil.poisonGamer(getBaseContext(), roomName, number + "");
            state = -1;
            return;
        }
        if (state == 1) {
            //进行投票
            HttpGameUtil httpGameUtil = new HttpGameUtil();
            httpGameUtil.voteGamer(getBaseContext(), roomName, number + "");
            state = -1;
            return;
        }
    }

    private static int seconds = 10;

    private void startTimer(TimerTask timerTask) {
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tv_time.setText(seconds + "");
                    break;
                case 2:
                    String info = msg.getData().getString("msg");
                    game_tv_info.setText(info + "");
                    if (info.equals("预言家正在查看的玩家身份")) {
                        myNotesetVisibility();
                    }
                    break;
                case 3:
                    int st = msg.getData().getInt("seat");
                    RelativeLayout rl=getRLayout(st);
                    if(rl!=null) rl.setBackgroundColor(getBaseContext().getResources().getColor(R.color.green_4DC0A4));
                    break;
                case 4:
                    newAvView(getBaseContext());
                    AVChatManager.getInstance().setupRemoteVideoRender(WolfKillApplication.gamers[whoSpeak].getUsername(),
                            avView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
            }
            super.handleMessage(msg);
        }
    };

    public class MyTimerTask extends TimerTask {
        String action;
        Boolean isFirstRequest;

        public MyTimerTask(String action) {
            this.action = action;
            isFirstRequest = true;
        }

        @Override
        public void run() {
            seconds--;
            handler.sendEmptyMessage(1);
            if (seconds == 0) {
                if (action.equals("天黑请闭眼")) {

                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }

                    if (WolfKillApplication.hostAccount.equals(DemoCache.getAccount()) && isFirstRequest) {
                        HttpGameUtil httpGameUtil = new HttpGameUtil();
                        httpGameUtil.getWolf(getBaseContext(), roomName);
                        isFirstRequest = false;
                    }
                    if (WolfKillApplication.identity != 2) {
                        seconds = 15;
                        MyTimerTask m = new MyTimerTask("狼人正在杀人中");
                        startTimer(m);
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("msg", "狼人正在杀人中");
                        message.setData(bundle);
                        message.what = 2;
                        handler.sendMessage(message);
                    }

                } else if (action.equals("狼人正在杀人中")) {

                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }

                    if (WolfKillApplication.hostAccount.equals(DemoCache.getAccount()) && isFirstRequest) {
                        HttpGameUtil httpGameUtil = new HttpGameUtil();

                        httpGameUtil.gotoSeerPeriod(getBaseContext(), roomName);
                        isFirstRequest = false;
                    }
                    if (WolfKillApplication.identity != 3) {
                        seconds = 10;
                        MyTimerTask m = new MyTimerTask("预言家正在查看的玩家身份");
                        startTimer(m);
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("msg", "预言家正在查看的玩家身份");
                        message.setData(bundle);
                        message.what = 2;
                        handler.sendMessage(message);
                    }

                } else if (action.equals("预言家正在查看的玩家身份")) {

                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    if (WolfKillApplication.identity != 4) {
                        seconds = 15;
                        MyTimerTask m = new MyTimerTask("女巫正在进行操作");
                        startTimer(m);
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("msg", "女巫正在进行操作");
                        message.setData(bundle);
                        message.what = 2;
                        handler.sendMessage(message);
                    }
                } else if (action.equals("黑夜结束")) {
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }

                    seconds = 1;
                    MyTimerTask m = new MyTimerTask("谁死亡了");
                    startTimer(m);
                }
                /*else if (action.equals("女巫正在进行操作")) {
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", "天亮了");
                    message.setData(bundle);
                    message.what = 2;
                    handler.sendMessage(message);

                }*/
                else if (action.equals("狼人阶段开启")) {
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    state = -1;
                    if (WolfKillApplication.hostAccount.equals(DemoCache.getAccount())) {
                        HttpGameUtil httpGameUtil = new HttpGameUtil();
                        httpGameUtil.gotoSeerPeriod(getBaseContext(), roomName);
                    }
                    //////////////////////////////////
                    /*AVChatManager.getInstance().enableAudienceRole(true);
                    AVChatManager.getInstance().muteLocalAudio(true);
                    AVChatManager.getInstance().muteRemoteAudio(DemoCache.getAccount(), true);*/
                    ///////////////////////////////////

                    seconds = 10;
                    MyTimerTask m = new MyTimerTask("预言家正在查看的玩家身份");
                    startTimer(m);
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", "预言家正在查看的玩家身份");
                    message.setData(bundle);
                    message.what = 2;
                    handler.sendMessage(message);
                } else if (action.equals("预言家选择要查看的身份")) {
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    state = -1;
                    HttpGameUtil httpGameUtil = new HttpGameUtil();
                    httpGameUtil.gotoWitchPeriod(getBaseContext(), roomName);
                    seconds = 15;
                    MyTimerTask m = new MyTimerTask("女巫正在进行操作");
                    startTimer(m);
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", "女巫正在进行操作");
                    message.setData(bundle);
                    message.what = 2;
                    handler.sendMessage(message);
                } else if (action.equals("女巫阶段开始")) {
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    state = -1;
                    HttpGameUtil httpGameUtil = new HttpGameUtil();
                    httpGameUtil.leaveNight(getBaseContext(), roomName);
                } else if (action.equals("谁死亡了")) {
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    switch (winner) {
                        case 0:
                            bundle.putString("msg", "游戏未结束");
                            message.setData(bundle);
                            message.what = 2;
                            handler.sendMessage(message);
                            seconds = 5;
                            MyTimerTask m = new MyTimerTask("游戏是否结束判断");
                            startTimer(m);
                            break;
                        case 1:
                            bundle.putString("msg", "好人阵营获胜");
                            message.setData(bundle);
                            message.what = 2;
                            hivSetEnable();
                            handler.sendMessage(message);
                            break;
                        case 2:
                            bundle.putString("msg", "狼人阵营获胜");
                            message.setData(bundle);
                            message.what = 2;
                            hivSetEnable();
                            handler.sendMessage(message);
                            break;
                    }

                } else if (action.equals("游戏是否结束判断")) {
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", whoSpeak + "号玩家发言");
                    message.setData(bundle);
                    message.what = 2;
                    handler.sendMessage(message);
                    seconds = 15;
                    MyTimerTask m = new MyTimerTask("玩家发言");
                    startTimer(m);
                } else if (action.equals("玩家发言")) {

                    if (isFirstRequest && DemoCache.getAccount().equals(WolfKillApplication.hostAccount)) {
                        //获取下一个谁说话 /TODD
                        HttpGameUtil httpGameUtil = new HttpGameUtil();
                        httpGameUtil.nextGamer(getBaseContext(), roomName, whoSpeak + "");
                        isFirstRequest = false;
                    }
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }


                } else if (action.equals("玩家开始投票")) {
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    HttpGameUtil httpGameUtil = new HttpGameUtil();
                    httpGameUtil.countResult(getBaseContext(), roomName);
                } else if (action.equals("投票死亡的玩家")) {
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    switch (winner) {
                        case 0:
                            bundle.putString("msg", "游戏未结束");
                            message.setData(bundle);
                            message.what = 2;
                            handler.sendMessage(message);
                            seconds = 5;
                            MyTimerTask m = new MyTimerTask("天黑请闭眼");
                            startTimer(m);
                            break;
                        case 1:
                            bundle.putString("msg", "好人阵营获胜");
                            message.setData(bundle);
                            hivSetEnable();
                            message.what = 2;
                            handler.sendMessage(message);
                            break;
                        case 2:
                            bundle.putString("msg", "狼人阵营获胜");
                            message.setData(bundle);
                            hivSetEnable();
                            message.what = 2;
                            handler.sendMessage(message);
                            break;
                    }
                }
            }

        }
    }


    @Override
    public void onBackPressed() {
        gamePresenter.leaveRoom(roomName);
        finish();
        super.onBackPressed();
    }

    private void myNotesetVisibility() {
        game_tv1.setVisibility(View.GONE);
        game_tv2.setVisibility(View.GONE);
        game_tv3.setVisibility(View.GONE);
        game_tv4.setVisibility(View.GONE);
        game_tv5.setVisibility(View.GONE);
        game_tv6.setVisibility(View.GONE);
        game_tv7.setVisibility(View.GONE);
        game_tv8.setVisibility(View.GONE);
        game_tv9.setVisibility(View.GONE);
    }

    class MyTask extends AsyncTask<Void, Void, String> {
        private NimUserInfo userI;
        private String account;
        private int seatNumber;
        private int st = 0;

        /**
         * 异步任务启动时最先被执行的方法，可以在此方法中实现
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * 执行耗时的方法，等同于线程的run
         */
        @Override
        protected String doInBackground(Void... params) {
            for (int i = 1; i < 10; i++) {
                SystemClock.sleep(500);
                if (WolfKillApplication.gamers[i].getUsername() == null)
                    break;
                account = WolfKillApplication.gamers[i].getUsername();
                seatNumber = WolfKillApplication.gamers[i].getSeatNumber();
                this.userI = NimUserInfoCache.getInstance().getUserInfo(this.account);
                st = WolfKillApplication.gamers[i].getState();
                if(st==1)
                { Message message=new Message();
                Bundle bundle=new Bundle();
                bundle.putInt("seat",i);
                message.setData(bundle);
                message.what=3;
                handler.sendMessage(message);}
                NimUserInfoCache.getInstance().getUserInfoFromRemote(this.account, new RequestCallback<NimUserInfo>() {
                    @Override
                    public void onSuccess(NimUserInfo param) {
                        userI = param;
                        updateUI(account, seatNumber);

                    }

                    @Override
                    public void onFailed(int code) {
                        Toast.makeText(GameActivity.this, "getUserInfoFromRemote failed:" + code, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onException(Throwable exception) {
                        Toast.makeText(GameActivity.this, "getUserInfoFromRemote exception:" + exception, Toast.LENGTH_SHORT).show();

                    }
                });
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if (st == 1) {
                RelativeLayout rl = getRLayout(seatNumber);
                rl.setBackgroundColor(getBaseContext().getResources().getColor(R.color.green_4DC0A4));
            }
        }
    }

    //游戏结束，将头像取消置灰
    private void hivSetEnable() {
        for (int i = 1; i < 10; i++) {
            HeadImageView headImageView = getCivView(i);
            headImageView.setEnabled(true);
            headImageView.clearColorFilter();
        }
        play_game_start.setEnabled(false);
    }

    private ImageView getStarImage(int seatNumber) {
        switch (seatNumber) {
            case 1:
                return game_star1;
            case 2:
                return game_star2;
            case 3:
                return game_star3;
            case 4:
                return game_star4;
            case 5:
                return game_star5;
            case 6:
                return game_star6;
            case 7:
                return game_star7;
            case 8:
                return game_star8;
            case 9:
                return game_star9;
        }
        return null;

    }

    private void newAvView(Context context){
        avView.setVisibility(View.GONE);
        avView = new AVChatVideoRender(context);
        avView_father.addView(avView,new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        avView.setVisibility(View.VISIBLE);
    }
}
