package com.nwsuaf.werewolf.login.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.nwsuaf.werewolf.R;
import com.nwsuaf.werewolf.common.config.preference.Preferences;
import com.nwsuaf.werewolf.common.config.preference.UserPreferences;
import com.nwsuaf.werewolf.login.presenter.ILoginPresenter;
import com.nwsuaf.werewolf.login.presenter.LoginPresenter;
import com.nwsuaf.werewolf.main.view.MainActivity;

/**
 * Created by liulin on 2017/6/22.
 */
public class WelcomeActivity extends AppCompatActivity implements ILoginView {
    private ILoginPresenter iLoginPresenter;
    private AbortableFuture<LoginInfo> loginRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        iLoginPresenter = new LoginPresenter(this, this);
        if (!Preferences.getIsFirst()) {
            final String account = DemoCache.getAccount();
            final String token = DemoCache.getToken();
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(account)) {
                Log.i("TAG", account + " " + token);

                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iLoginPresenter.onLogin(account, token);
                    }
                }, 1000);
            }
        } else {
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                    finish();
                }
            }, 1000);
        }
    }


    @Override
    public void goToMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goToLoginActiviy() {
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

}
