package com.nwsuaf.werewolf.httputil;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.netease.nim.uikit.common.http.NimHttpClient;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.nwsuaf.werewolf.DemoCache;
import com.nwsuaf.werewolf.common.util.CheckSumBuilder;
import com.nwsuaf.werewolf.common.util.ConfigUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liulin on 2017/6/27.
 */

public class UserHttpClient implements ConfigUtil {

    private static UserHttpClient instance;

    public static synchronized UserHttpClient getInstance() {
        if (instance == null) {
            instance = new UserHttpClient();
        }

        return instance;
    }

    public interface UserHttpCallback<T> {
        void onSuccess(T t);

        void onFailed(int code, String errorMsg);
    }

    private UserHttpClient() {
        NimHttpClient.getInstance().init(DemoCache.getContext());
    }

    private String readAppKey() {
        try {
            ApplicationInfo appInfo = DemoCache.getContext().getPackageManager()
                    .getApplicationInfo(DemoCache.getContext().getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null) {
                return appInfo.metaData.getString("com.netease.nim.appKey");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, String> getHeader() {
        Map<String, String> headers = new HashMap<>(1);
        String appKey = readAppKey();
        String nonce = "12345";
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(APP_SECRET, nonce, curTime);

        headers.put("AppKey", appKey);
        headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.put("Nonce", nonce);
        headers.put("CurTime", curTime);
        headers.put("CheckSum", checkSum);
        return headers;
    }


    public void setUserInfo(String accid, Integer gender, String name, final UserHttpCallback<Void> callback) {
        String url = UPDATE_USER_INFO;
        Map<String, String> headers = getHeader();

        StringBuilder body = new StringBuilder();
        body.append("accid").append("=").append(accid).append("&").append("name").append("=")
                .append(name).append("&").append("gender").append("=").append(gender);
        String bodyString = body.toString();
        NimHttpClient.getInstance().execute(url, headers, bodyString, new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable exception) {
                Log.i("TAG", "code" + code);
                if (code != 200 || exception != null) {
                    LogUtil.e("TAG", "register failed : code = " + code + ", errorMsg = "
                            + (exception != null ? exception.getMessage() : "null"));
                    if (callback != null) {
                        callback.onFailed(code, exception != null ? exception.getMessage() : "null");
                    }
                }
                Log.i("TAG", "response" + response);
                JSONObject Obj = JSONObject.parseObject(response);

                int mycode = Obj.getIntValue("code");
                Log.i("TAG", "mycode" + mycode);
                if (mycode == 200) {
                    callback.onSuccess(null);
                } else {
                    String error = "";
                    if (mycode == 414) {
                        error = "参数错误";
                    } else if (mycode == 431) {
                        error = "HTTP重复请求";
                    } else {
                        error = "服务器内部错误";
                    }
                    callback.onFailed(mycode, error);
                }


            }
        });
    }
}
