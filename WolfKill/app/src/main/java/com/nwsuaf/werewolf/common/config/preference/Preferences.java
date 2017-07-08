package com.nwsuaf.werewolf.common.config.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.nwsuaf.werewolf.DemoCache;


/**
 * Created by liulin on 2017/6/15.
 */
public class Preferences {
    private static final String KEY_USER_ACCOUNT = "account";
    private static final String KEY_USER_TOKEN = "token";
    private static final  String KEY_USER_NICK="name";
    private static final String KEY_USER_SID = "sid";
    private static final  String  IS_FIRST="is";
    public static void saveUserAccount(String account) {
        saveString(KEY_USER_ACCOUNT, account);
    }

    public static String getUserAccount() {
        return getString(KEY_USER_ACCOUNT);
    }

    public static void saveUserToken(String token) {
        saveString(KEY_USER_TOKEN, token);
    }

    public static String getUserToken() {
        return getString(KEY_USER_TOKEN);
    }

    public static void saveIs_FIRST(Boolean isfirst) {
        saveBoolean(IS_FIRST, isfirst);
    }

    public static void saveUserName(String name) {
        saveString(KEY_USER_NICK, name);
    }

    public static String getUserName() {
        return getString(KEY_USER_NICK);
    }

    public static Boolean getIsFirst() {
        return getSharedPreferences().getBoolean(IS_FIRST,true);
    }

    private static void saveString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }
    private static void saveBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private static String getString(String key) {
        return getSharedPreferences().getString(key, null);
    }

    static SharedPreferences getSharedPreferences() {
        return DemoCache.getContext().getSharedPreferences("Demo", Context.MODE_PRIVATE);
    }

    public static void saveUserSid(String sid){
        saveString(KEY_USER_SID, sid);
    }

    public static String getUserSid(){
        return getString(KEY_USER_SID);
    }
}
