package com.nwsuaf.werewolf.httputil;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.netease.nim.uikit.common.http.NimHttpClient;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.MD5;
import com.nwsuaf.werewolf.DemoCache;
import com.nwsuaf.werewolf.common.util.CheckSumBuilder;
import com.nwsuaf.werewolf.common.util.ConfigUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by liulin on 2017/6/30.
 */
public class RegisterHttpClient implements ConfigUtil {
    private static final String TAG = "RegisterHttpClient";

    // code
    private static final int RESULT_CODE_SUCCESS = 200;

    // header
    private static final String HEADER_KEY_APP_KEY = "appkey";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";


    public interface ContactHttpCallback<T> {
        void onSuccess(T t);

        void onFailed(int code, String errorMsg);
    }

    private static RegisterHttpClient instance;

    public static synchronized RegisterHttpClient getInstance() {
        if (instance == null) {
            instance = new RegisterHttpClient();
        }

        return instance;
    }

    private RegisterHttpClient() {
        NimHttpClient.getInstance().init(DemoCache.getContext());
    }


    /**
     * 向应用服务器创建账号（注册账号）
     * 由应用服务器调用WEB SDK接口将新注册的用户数据同步到云信服务器
     */
    public void register(String account, String nickName, String password, final ContactHttpCallback<Void> callback) {
        String url = API_SERVER;
        password = MD5.getStringMD5(password);
        try {
            nickName = URLEncoder.encode(nickName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Map<String, String> headers = new HashMap<>(1);
        String appKey = readAppKey();
        String nonce =  "12345";
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(APP_SECRET, nonce ,curTime);

        headers.put(HEADER_KEY_APP_KEY, appKey);
        headers.put(HEADER_CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");
        headers.put("Nonce", nonce);
        headers.put("CurTime", curTime);
        headers.put("CheckSum", checkSum);


        StringBuilder body = new StringBuilder();
        body.append("accid").append("=").append(account.toLowerCase()).append("&")
                .append("name").append("=").append(nickName).append("&")
                .append("token").append("=").append(password);
        String bodyString = body.toString();
        NimHttpClient.getInstance().execute(url, headers, bodyString, new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable exception) {
                if (code != 200 || exception != null) {
                    LogUtil.e(TAG, "register failed : code = " + code + ", errorMsg = "
                            + (exception != null ? exception.getMessage() : "null"));
                    if (callback != null) {
                        callback.onFailed(code, exception != null ? exception.getMessage() : "null");
                    }
                    return;
                }
                try {
                    JSONObject resObj = JSONObject.parseObject(response);
                    int resCode = resObj.getIntValue("code");

                    if (resCode == RESULT_CODE_SUCCESS) {
                        callback.onSuccess(null);
                    } else {
                        String error="";
                        if(resCode==414)
                        {
                            error="帐号已经注册";
                        }
                        else if(resCode==431)
                        {
                            error="HTTP重复请求";
                        }
                        else
                        {
                            error="服务器内部错误";
                        }
                        callback.onFailed(resCode, error);
                    }
                } catch (JSONException e) {
                    callback.onFailed(-1, e.getMessage());
                }
            }
        });
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
}
