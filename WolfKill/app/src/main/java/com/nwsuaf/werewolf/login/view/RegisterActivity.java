package com.nwsuaf.werewolf.login.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.nwsuaf.werewolf.common.config.preference.Preferences;
import com.nwsuaf.werewolf.R;
import com.nwsuaf.werewolf.httputil.RegisterHttpClient;

/**
 * 账号注册类
 * Created by liulin on 2017/6/26.
 */

public class RegisterActivity extends AppCompatActivity {
    private Button bt_register;
    private ClearableEditTextWithIcon et_name, et_account, et_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        initListeners();
    }

    private void initListeners() {
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegister();
            }
        });
    }

    private void onRegister() {
        register();
    }

    private void initViews() {
        bt_register = (Button) findViewById(R.id.register_bt);
        et_name = (ClearableEditTextWithIcon) findViewById(R.id.register_et_name);
        et_account = (ClearableEditTextWithIcon) findViewById(R.id.register_et_account);
        et_password = (ClearableEditTextWithIcon) findViewById(R.id.register_et_passwd);
    }

    /**
     * ***************************************** 注册 **************************************
     */


    private void register() {

        if (!checkRegisterContentValid()) {
            return;
        }

        if (!NetworkUtil.isNetAvailable(RegisterActivity.this)) {
            Toast.makeText(RegisterActivity.this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        DialogMaker.showProgressDialog(this, "注册中····", false);

        // 注册流程
        final String account = et_account.getText().toString();
        final String nickName = et_name.getText().toString();
        final String password = et_password.getText().toString();

        RegisterHttpClient.getInstance().register(account, nickName, password, new RegisterHttpClient.ContactHttpCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                DialogMaker.dismissProgressDialog();
                Intent intent = new Intent();
                intent.putExtra("account", account);
                intent.putExtra("password", password);
                Preferences.saveUserName(nickName);
                setResult(200, intent);
                finish();
            }
            @Override
            public void onFailed(int code, String errorMsg) {
                Toast.makeText(RegisterActivity.this, getString(R.string.register_failed, String.valueOf(code), errorMsg), Toast.LENGTH_SHORT)
                        .show();
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    private boolean checkRegisterContentValid() {

        // 帐号检查
        String account = et_account.getText().toString().trim();
        if (account.length() <= 0 || account.length() > 20) {
            Toast.makeText(this, "帐号限20位字母或者数字", Toast.LENGTH_SHORT).show();

            return false;
        }

        // 昵称检查
        String nick = et_name.getText().toString().trim();
        if (!nick.matches("^[\\u4E00-\\u9FA5A-Za-z0-9]{1,10}$")) {
            Toast.makeText(this, "昵称限10位汉字、字母或者数字", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 密码检查
        String password = et_password.getText().toString().trim();
        if (password.length() < 6 || password.length() > 20) {
            Toast.makeText(this, "密码必须为6~20位字母或者数字", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }
}
