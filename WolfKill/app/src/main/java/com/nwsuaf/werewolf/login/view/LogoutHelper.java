package com.nwsuaf.werewolf.login.view;

import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.ui.drop.DropManager;
import com.nwsuaf.werewolf.DemoCache;

/**
 * 注销帮助类
 * Created by liulin on 2015/10/8.
 */
public class LogoutHelper {
    public static void logout() {
        // 清理缓存&注销监听&清除状态
        NimUIKit.clearCache();
        DemoCache.clear();
        LoginSyncDataStatusObserver.getInstance().reset();
        DropManager.getInstance().destroy();
    }
}
