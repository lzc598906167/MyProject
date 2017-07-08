package com.nwsuaf.werewolf.game.presenter;

import android.content.Context;
import android.util.Log;

import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.AVChatStateObserver;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.constant.AVChatUserRole;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoCropRatio;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatCameraCapturer;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatParameters;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoCapturerFactory;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoRender;
import com.nwsuaf.werewolf.DemoCache;
import com.nwsuaf.werewolf.WolfKillApplication;
import com.nwsuaf.werewolf.game.module.SimpleAVChatStateObserver;
import com.nwsuaf.werewolf.game.view.IGameView;
import com.nwsuaf.werewolf.httputil.HttpGameUtil;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


/**
 * Created by xiaoheng and liulin on 2017/6/27.
 */

public class GamePresenter implements IGamePresenter {
    private static final String TAG = "AV_GAME";
    private IGameView iGameView;
    private AVChatStateObserver stateObserver;
    private AVChatVideoRender avView;
    private AVChatCameraCapturer mVideoCapturer;
    private Context mContext;
    private int roomNum;


    public GamePresenter(Context context, IGameView iGameView, AVChatVideoRender avView) {
        this.iGameView = iGameView;
        this.avView = avView;
        this.mContext = context;
    }


    /**
     * 加入房间
     *
     * @param roomName 房间号
     */
    @Override
    public void joinRoom(final String roomName) {
        roomNum = Integer.parseInt(roomName);
        //开启音视频引擎
        AVChatManager.getInstance().enableRtc();
        //设置场景, 如果需要高清音乐场景，设置 AVChatChannelProfile#CHANNEL_PROFILE_HIGH_QUALITY_MUSIC
        //AVChatManager.getInstance.setChannelProfile(CHANNEL_PROFILE_DEFAULT);
        //视频通话设置
        AVChatManager.getInstance().enableVideo();
        //设置视频采集模块
        mVideoCapturer = AVChatVideoCapturerFactory.createCameraCapturer();
        AVChatManager.getInstance().setupVideoCapturer(mVideoCapturer);
        //设置事件监听
        if (stateObserver != null) {
            AVChatManager.getInstance().observeAVChatState(stateObserver, false);
        }
        stateObserver = new SimpleAVChatStateObserver() {
            @Override
            public void onJoinedChannel(int code, String audioFile, String videoFile) {
                Log.i(TAG, "code=" + code);
                if (code == 200) {
                    startLocalPreview();
                } else {
                    onJoinRoomFailed();
                }
            }

            @Override
            public void onUserJoined(String account) {
                Log.i(TAG, "onUserJoined=" + account);
            }

            @Override
            public void onUserLeave(String account, int event) {
                Log.i(TAG, "onUserLeave=" + account);
                if (account.equals(WolfKillApplication.hostAccount)) {
                    HttpGameUtil httpGameUtil = new HttpGameUtil();
                    httpGameUtil.changeOwner(mContext, roomName, account, GamePresenter.this);
                    return;
                }
            }

            @Override
            public void onReportSpeaker(Map<String, Integer> speakers, int mixedEnergy) {
                //onAudioVolume(speakers);
            }
        };
        AVChatManager.getInstance().observeAVChatState(stateObserver, true);

        /*
         * 加入房间
         */
        //设置用户类型，普通或观众
        AVChatManager.getInstance().setParameter(AVChatParameters.KEY_SESSION_MULTI_MODE_USER_ROLE, AVChatUserRole.AUDIENCE);
        AVChatManager.getInstance().setParameter(AVChatParameters.KEY_AUDIO_REPORT_SPEAKER, true);
        AVChatManager.getInstance().setParameter(AVChatParameters.KEY_VIDEO_FIXED_CROP_RATIO, AVChatVideoCropRatio.CROP_RATIO_1_1);
        AVChatManager.getInstance().joinRoom2(roomName, AVChatType.VIDEO, new AVChatCallback<AVChatData>() {
            @Override
            public void onSuccess(AVChatData avChatData) {
                long chatId = avChatData.getChatId();
                Log.i(TAG, "join!" + "chatId:" + chatId);
                onJoinRoomSuccess();
                iGameView.onShowRoomNum(roomName);
            }

            @Override
            public void onFailed(int code) {
                Log.i(TAG, "" + code);
                onJoinRoomFailed();
            }

            @Override
            public void onException(Throwable exception) {
                onJoinRoomFailed();
                Log.i(TAG, exception.getMessage());
            }
        });
    }

    @Override
    public void getNewOwner(int code, String owner, String account) {
        if (code == 200) {
            WolfKillApplication.hostAccount = owner;
            if (DemoCache.getAccount().equals(WolfKillApplication.hostAccount)) {
                iGameView.updateOwnerAction();
                HttpGameUtil httpGameUtil = new HttpGameUtil();
                httpGameUtil.removeGamer(mContext, roomNum + "", account);
            }
        }
    }

    private void onJoinRoomFailed() {
        iGameView.onFinish();
    }

    private void onJoinRoomSuccess() {
        Set<String> tagSet = new LinkedHashSet<String>();
        tagSet.add(roomNum + "");
        JPushInterface.setAliasAndTags(mContext, DemoCache.getAccount(), tagSet, new TagAliasCallback() {
            @Override
            public void gotResult(int code, String s, Set<String> set) {
                String logs;
                switch (code) {
                    case 0:
                        logs = "Set tag and alias success";
                        Log.i(TAG, logs);
                        HttpGameUtil httpGameUtil = new HttpGameUtil();
                        httpGameUtil.addGamer(mContext, roomNum + "", DemoCache.getAccount(), iGameView);
                        break;
                    case 6002:
                        logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                        Log.i(TAG, logs);
                        break;
                    default:
                        logs = "Failed with errorCode = " + code;
                        Log.e(TAG, logs);
                }
            }
        });
        //当用户进入之后更新后台数据


    }

    private void startLocalPreview() {
        AVChatManager.getInstance().setupLocalVideoRender(avView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
        AVChatManager.getInstance().startVideoPreview();
        AVChatManager.getInstance().muteLocalAudio(true);
        AVChatManager.getInstance().muteLocalVideo(true);
    }

    private void onPlayerUserJoin(String account) {
        AVChatManager.getInstance().setupRemoteVideoRender(account, avView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
    }

    public void leaveRoom(String roomName) {
        AVChatManager.getInstance().leaveRoom2(roomName, new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("TAG", "leaveRoom success!!");
            }

            @Override
            public void onFailed(int code) {
                Log.i("TAG", "leaveRoom failed!!");
            }

            @Override
            public void onException(Throwable exception) {
                Log.i("TAG", "leaveRoom exception!!");
            }
        });
        AVChatManager.getInstance().disableRtc();
    }
}
