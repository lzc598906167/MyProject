package com.nwsuaf.werewolf.login.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.MD5;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.nwsuaf.werewolf.DemoCache;
import com.nwsuaf.werewolf.common.config.preference.Preferences;
import com.nwsuaf.werewolf.common.config.preference.UserPreferences;
import com.nwsuaf.werewolf.login.view.ILoginView;
import com.nwsuaf.werewolf.login.view.LoginActivity;
import com.nwsuaf.werewolf.login.view.WelcomeActivity;

/**
 * 登录操作类
 * Created by liulin on 2017/6/30.
 */

public class LoginPresenter implements ILoginPresenter {
    private AbortableFuture<LoginInfo> loginRequest;
    private final Context mContext;
    private static final String TAG= "TAG"; ;
    private ILoginView iLoginView;
    public   LoginPresenter(Context context,ILoginView iLogin) {
        mContext = context;
        iLoginView=iLogin;
    }

    /**
     * 登录应用服务器
     */
    public void onLogin(final String account, final String passwd) {
        DialogMaker.showProgressDialog(mContext, null, "登录中·····", true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (loginRequest != null) {
                    loginRequest.abort();
                    onLoginDone();
                }
            }
        }).setCanceledOnTouchOutside(false);
        Log.i("TAG",account);
        Log.i("TAG",passwd);
        final String token = tokenFromPassword(passwd);
        loginRequest = NimUIKit.doLogin(new LoginInfo(account, token), new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                LogUtil.i(TAG, "login success");

                onLoginDone();
                DemoCache.setAccount(account);
                saveLoginInfo(account, passwd);
                // 初始化消息提醒配置
                Preferences.saveIs_FIRST(false);
                iLoginView.goToMainActivity();
            }

            @Override
            public void onFailed(int code) {
                onLoginDone();
                if (code == 302 || code == 404) {
                    Toast.makeText(mContext, "帐号或密码错误", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "登录失败: " + code, Toast.LENGTH_SHORT).show();
                }
                if(mContext.getClass().equals(WelcomeActivity.class))
                {
                    iLoginView.goToLoginActiviy();
                }
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(mContext, "无效输入", Toast.LENGTH_LONG).show();
                onLoginDone();
            }
        });
    }

    private String tokenFromPassword(String password) {
        Log.i("TAG",password);
        Log.i("TAG",MD5.getStringMD5(password));
        return MD5.getStringMD5(password);
    }

    private void onLoginDone() {
        loginRequest = null;
        DialogMaker.dismissProgressDialog();
    }

    private static String readAppKey(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null) {
                return appInfo.metaData.getString("com.netease.nim.appKey");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initNotificationConfig() {
        // 初始化消息提醒
        NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

        // 加载状态栏配置
        StatusBarNotificationConfig statusBarNotificationConfig = UserPreferences.getStatusConfig();
        if (statusBarNotificationConfig == null) {
            statusBarNotificationConfig = DemoCache.getNotificationConfig();
            UserPreferences.setStatusConfig(statusBarNotificationConfig);
        }
        // 更新配置
        NIMClient.updateStatusBarNotificationConfig(statusBarNotificationConfig);
    }

    private void saveLoginInfo(final String account, final String token) {
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
    }

}
