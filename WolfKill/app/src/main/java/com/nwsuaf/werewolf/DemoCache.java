package com.nwsuaf.werewolf;

import android.content.Context;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.nwsuaf.werewolf.common.config.preference.Preferences;


public class DemoCache {

    private static Context context;

    private static String account;

    private static String sid;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        DemoCache.token = token;
    }


    //云信服务 token
    private static String token;

    //视频云点播服务 token
    private static String vodtoken;

    private static StatusBarNotificationConfig notificationConfig;

    public static void clear() {
        account = null;
    }

    public static String getAccount() {
        return account;
    }

    private static boolean mainTaskLaunching;

    public static void setAccount(String account) {
        DemoCache.account = account;
        NimUIKit.setAccount(account);
    }

    public static void setNotificationConfig(StatusBarNotificationConfig notificationConfig) {
        DemoCache.notificationConfig = notificationConfig;
    }

    public static StatusBarNotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    public static Context getContext() {
        return context;
    }

    public static String getSid() {
        if (sid == null) {
            sid = Preferences.getUserSid();
        }
        return sid;
    }

    public static void setSid(String sid) {
        DemoCache.sid = sid;
        Preferences.saveUserSid(sid);
    }

    public static String getVodtoken() {
        return vodtoken;
    }

    public static void setVodtoken(String vodtoken) {
        DemoCache.vodtoken = vodtoken;
    }

    public static void setContext(Context context) {
        DemoCache.context = context.getApplicationContext();
    }

    public static void setMainTaskLaunching(boolean mainTaskLaunching) {
        DemoCache.mainTaskLaunching = mainTaskLaunching;
    }

    public static boolean isMainTaskLaunching() {
        return mainTaskLaunching;
    }
}
