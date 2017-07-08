package com.nwsuaf.werewolf.main.presenter;

import android.content.Context;

import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoCapturerFactory;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoRender;
import com.netease.nrtc.video.render.IVideoRender;
import com.nwsuaf.werewolf.main.view.IMainView;

/**
 * Created by xiaoheng on 2017/6/22.
 */

public class MainPresenter implements IMainPresenter {
    private IMainView iMainView;

    public MainPresenter(IMainView iMainView) {
        this.iMainView = iMainView;
    }

    @Override
    public void gotoGameActivity(int type, String room) {
        iMainView.OnGotoGameActivity(type,room);
    }


}
