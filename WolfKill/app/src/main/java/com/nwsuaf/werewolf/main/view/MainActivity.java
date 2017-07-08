package com.nwsuaf.werewolf.main.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.permission.MPermission;
import com.netease.nim.uikit.permission.annotation.OnMPermissionDenied;
import com.netease.nim.uikit.permission.annotation.OnMPermissionGranted;
import com.netease.nim.uikit.permission.annotation.OnMPermissionNeverAskAgain;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.model.AVChatChannelInfo;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.nwsuaf.werewolf.DemoCache;
import com.nwsuaf.werewolf.R;
import com.nwsuaf.werewolf.WolfKillApplication;
import com.nwsuaf.werewolf.common.config.preference.Preferences;
import com.nwsuaf.werewolf.common.util.ConfigUtil;
import com.nwsuaf.werewolf.game.model.Room;
import com.nwsuaf.werewolf.game.view.GameActivity;
import com.nwsuaf.werewolf.game.view.GameDialog;
import com.nwsuaf.werewolf.httputil.HttpRoomUtil;
import com.nwsuaf.werewolf.login.view.LoginActivity;
import com.nwsuaf.werewolf.login.view.LogoutHelper;
import com.nwsuaf.werewolf.main.presenter.IMainPresenter;
import com.nwsuaf.werewolf.main.presenter.MainPresenter;
import com.nwsuaf.werewolf.settings.view.SettingsActivity;

import java.util.List;

import flyn.Eyes;

public class MainActivity extends AppCompatActivity implements IMainView, ConfigUtil {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private LinearLayout llKill;
    private LinearLayout llFriend;
    private LinearLayout llloginout;
    private LinearLayout llRank;
    private LinearLayout llSimple;
    private LinearLayout llHard;
    private LinearLayout llRule;
    private LinearLayout llCreate;
    private LinearLayout llFind;
    private IMainPresenter iMainPresenter;
    private ImageView ivCloud;
    private ImageView ivBack;
    private TextView tvNickname;
    private ImageView ivSex;
    private HeadImageView nav_header_ci_image;
    private String account;
    private NimUserInfo userInfo;
    private HeadImageView civ;
    private LinearLayout llheader;
    private TextView tv_account;
    private Boolean isFirst = true;
    /**
     * 权限检查参数
     */
    private static final int AUTO_REJECT_CALL_TIMEOUT = 45 * 1000;
    private static final int CHECK_RECEIVED_CALL_TIMEOUT = 45 * 1000;
    private static final int MAX_SUPPORT_ROOM_USERS_COUNT = 9;
    private static final int BASIC_PERMISSION_REQUEST_CODE = 0x100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iMainPresenter = new MainPresenter(this);
        Eyes.translucentStatusBar(this, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        initViews();
        initListeners();

        account = DemoCache.getAccount();
        getUserInfo();
        if (userInfo != null) {
            updateUI();
        }
        checkPermission();
    }

    private void initListeners() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        llSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpRoomUtil httpRoomUtil = new HttpRoomUtil();
                httpRoomUtil.findSimple(MainActivity.this, MainActivity.this);
            }
        });

        llHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpRoomUtil httpRoomUtil = new HttpRoomUtil();
                httpRoomUtil.findHard(MainActivity.this, MainActivity.this);
            }
        });
        //创建随机房间
        llCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameDialog.class);
                intent.putExtra(REQUEST_CODE, "createRoom");
                intent.putExtra(DIALOG_TV_TITLE, "创建房间");
                intent.putExtra(DIALOG_TV_CONTENT, "请选择游戏难度");
                intent.putExtra(DIALOG_BT_LEFT, "简单");
                intent.putExtra(DIALOG_BT_RIGHT, "困难");
                startActivityForResult(intent, 2);
            }
        });
        //查找房间
        llFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameDialog.class);
                intent.putExtra(REQUEST_CODE, "findRoom");
                intent.putExtra(DIALOG_TV_TITLE, "查询房间");
                intent.putExtra(DIALOG_ET_CONTENT, "请选择输入房间号(数字)");
                intent.putExtra(DIALOG_BT_LEFT, "确定");
                intent.putExtra(DIALOG_BT_RIGHT, "取消");
                startActivityForResult(intent, 1);
                //
            }
        });
        //规则界面
        llRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegularActivity.class);
                startActivity(intent);
            }
        });
        //注销
        llloginout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLogout();
            }
        });
        //个人设置界面
        llheader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, 1);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            //从设置界面返回主界面
            getUserInfo();
        } else if (resultCode == 2) {
            //从dialog界面返回主界面（点击确定）获取房间号进行查找
            String result = data.getStringExtra(DIALOG_TO_MAIN);
            if (result.equals("确定")) {
                String temp = data.getStringExtra("content");
                Log.i("Mainactivity", temp + "");
                int roomName = Integer.parseInt(temp);
                HttpRoomUtil httpRoomUtil = new HttpRoomUtil();
                httpRoomUtil.findRoom(this, roomName, this);
            } else if (result.equals("简单")) {
                HttpRoomUtil httpRoomUtil = new HttpRoomUtil();
                httpRoomUtil.creatRoom(this, 1, account, this);

            } else if (result.equals("困难")) {
                HttpRoomUtil httpRoomUtil = new HttpRoomUtil();
                httpRoomUtil.creatRoom(this, 2, account, this);

            }
        } else if (requestCode == 3) {

        }
    }

    private void initViews() {
        tvNickname = (TextView) findViewById(R.id.nav_header_tv_nickname);
        ivSex = (ImageView) findViewById(R.id.nav_header_iv_sex);
        nav_header_ci_image = (HeadImageView) findViewById(R.id.nav_header_ci_image);
        tv_account = (TextView) findViewById(R.id.nav_header_tv_account);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        civ = (HeadImageView) findViewById(R.id.main_civ);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        llKill = (LinearLayout) findViewById(R.id.main_drawer_wolfkill);
        llFriend = (LinearLayout) findViewById(R.id.main_drawer_myfriend);
        llRank = (LinearLayout) findViewById(R.id.main_drawer_ranking);
        llloginout = (LinearLayout) findViewById(R.id.main_drawer_loingout);
        llheader = (LinearLayout) findViewById(R.id.nav_header_ll_head);
        llSimple = (LinearLayout) findViewById(R.id.main_ll_simple);
        llHard = (LinearLayout) findViewById(R.id.main_ll_hard);
        llRule = (LinearLayout) findViewById(R.id.main_ll_rule);
        llCreate = (LinearLayout) findViewById(R.id.main_ll_create);
        llFind = (LinearLayout) findViewById(R.id.main_ll_find);
        ivCloud = (ImageView) findViewById(R.id.bar_iv_cloud);
        ivBack = (ImageView) findViewById(R.id.bar_iv_back);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    //调到游戏界面
    @Override
    public void OnGotoGameActivity(int type, String room) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra(EXTRA_MAIN_TO_GAME, type);
        intent.putExtra(EXTRA_GAME_HOME_NUMBER, room);
        startActivity(intent);
    }

    //随机简单或者困难房间
    @Override
    public void findRandomRoom(int code, int roomName, int type) {
        Log.i("TAG", "" + code);
        if (code == 200) {
            iMainPresenter.gotoGameActivity(type, roomName + "");
        } else {
            Toast.makeText(MainActivity.this, "房间全满或者在游戏中，你可以创建房间", Toast.LENGTH_SHORT).show();
        }
    }

    //查找指定房间
    @Override
    public void findRoom(int code, Room room) {
        Log.i("TAG", "" + code);
        if (code == 200) {
            if (room.getType() == 1 && room.getPeopleNumber() >= 9) {
                Toast.makeText(MainActivity.this, "对不起，房间人数已满", Toast.LENGTH_SHORT).show();
            } else if (room.getType() == 2 && room.getPeopleNumber() >= 12) {
                Toast.makeText(MainActivity.this, "对不起，房间人数已满", Toast.LENGTH_SHORT).show();
            } else {
                iMainPresenter.gotoGameActivity(room.getType(), room.getRoomName() + "");
            }
        } else {
            Toast.makeText(MainActivity.this, "对不起，没有这个房间", Toast.LENGTH_SHORT).show();
        }
    }

    //创建简单或者困难模式房间
    @Override
    public void createRoom(int code, final int roomName, final int type) {
        Log.i("TAG", "" + code);
        if (code == 200) {
            Log.i("TAG", "roomName=" + roomName);
            AVChatManager.getInstance().createRoom
                    (roomName + "", "hello", new AVChatCallback<AVChatChannelInfo>() {
                        @Override
                        public void onSuccess(AVChatChannelInfo avChatChannelInfo) {
                            Log.i("TAG", "good!");
                            WolfKillApplication.hostAccount = account;
                            iMainPresenter.gotoGameActivity(type, roomName + "");
                        }

                        @Override
                        public void onFailed(int code) {
                            Log.i("TAG", code + "");
                            HttpRoomUtil httpRoomUtil = new HttpRoomUtil();
                            httpRoomUtil.deleteRoom(getBaseContext(), roomName);
                        }

                        @Override
                        public void onException(Throwable exception) {
                            Log.i("TAG", exception.getMessage());
                            HttpRoomUtil httpRoomUtil = new HttpRoomUtil();
                            httpRoomUtil.deleteRoom(getBaseContext(), roomName);
                        }
                    });
        } else {
            Toast.makeText(MainActivity.this, "创建房间失败", Toast.LENGTH_SHORT).show();
        }
    }

    //获取个人信息
    private void getUserInfo() {
        userInfo = NimUserInfoCache.getInstance().getUserInfo(account);

        if (userInfo == null) {
            NimUserInfoCache.getInstance().getUserInfoFromRemote(account, new RequestCallback<NimUserInfo>() {
                @Override
                public void onSuccess(NimUserInfo param) {
                    userInfo = param;
                    updateUI();
                }

                @Override
                public void onFailed(int code) {
                    Toast.makeText(MainActivity.this, "getUserInfoFromRemote failed:" + code, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onException(Throwable exception) {
                    Toast.makeText(MainActivity.this, "getUserInfoFromRemote exception:" + exception, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            updateUI();
        }
    }

    //更新个人信息
    private void updateUI() {
        nav_header_ci_image.loadBuddyAvatar(account);
        civ.loadBuddyAvatar(account);
        if (userInfo.getName() != null)
            tvNickname.setText(userInfo.getName());

        if (userInfo.getGenderEnum() != null) {
            if (userInfo.getGenderEnum() == GenderEnum.MALE) {
                ivSex.setImageResource(R.mipmap.nav_header_nan);
            } else if (userInfo.getGenderEnum() == GenderEnum.FEMALE) {
                ivSex.setImageResource(R.mipmap.nav_header_nv);
            } else {

            }
        }
        if (isFirst) {
            tv_account.setText(DemoCache.getAccount());
        }
    }

    // 注销
    private void onLogout() {
        // 清理缓存&注销监听
        LogoutHelper.logout();
        Preferences.saveIs_FIRST(true);
        // 启动登录
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    /**
     * ************************************ 权限检查 ***************************************
     */

    private void checkPermission() {
        List<String> lackPermissions = AVChatManager.getInstance().checkPermission(MainActivity.this);
        if (lackPermissions.isEmpty()) {
            onBasicPermissionSuccess();
        } else {
            String[] permissions = new String[lackPermissions.size()];
            for (int i = 0; i < lackPermissions.size(); i++) {
                permissions[i] = lackPermissions.get(i);
            }
            MPermission.with(MainActivity.this)
                    .setRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                    .permissions(permissions)
                    .request();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    @OnMPermissionNeverAskAgain(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        Toast.makeText(this, "音视频通话所需权限未全部授权，部分功能可能无法正常运行！", Toast.LENGTH_SHORT).show();
    }
}
