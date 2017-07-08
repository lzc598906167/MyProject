package com.nwsuaf.werewolf.login.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.nwsuaf.werewolf.R;
import com.nwsuaf.werewolf.common.config.preference.Preferences;
import com.nwsuaf.werewolf.login.presenter.ILoginPresenter;
import com.nwsuaf.werewolf.login.presenter.LoginPresenter;
import com.nwsuaf.werewolf.main.view.MainActivity;

/**
 * 账号登陆类
 * Created by liulin on 2017/6/26.
 */

public class LoginActivity extends AppCompatActivity implements ILoginView {
    private Button bt_login;
    private Button bt_register;
    private ClearableEditTextWithIcon et_account;
    private ClearableEditTextWithIcon et_password;
    private ILoginPresenter iLoginPresenter;
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initViews();
        initListeners();
        initClear();
        iLoginPresenter = new LoginPresenter(this, this);

    }

    private void initClear() {
        et_password.setText("");
        et_account.setText("");
    }

    private void initListeners() {
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkRegisterContentValid())
                {
                    return;
                }
                iLoginPresenter.onLogin(et_account.getText().toString().toLowerCase(), et_password.getText().toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200) {
            String account = data.getExtras().getString("account");
            String password = data.getExtras().getString("password");
            et_account.setText(account);
            et_password.setText(password);
            Preferences.saveIs_FIRST(false);
            iLoginPresenter.onLogin(et_account.getText().toString().toLowerCase(), et_password.getText().toString());
        }
    }

    private void initViews() {
        bt_login = (Button) findViewById(R.id.login_bt_submit);
        bt_register = (Button) findViewById(R.id.login_bt_register);
        et_account = (ClearableEditTextWithIcon) findViewById(R.id.login_et_account);
        et_password = (ClearableEditTextWithIcon) findViewById(R.id.login_et_password);
    }

    /**
     * ***************************************** 登录 **************************************
     */

    @Override
    public void goToMainActivity() {
        // 进入主界面
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goToLoginActiviy() {
        //不用写
    }
    private boolean checkRegisterContentValid() {

        // 帐号检查
        String account = et_account.getText().toString().trim();
        if (account.length() <= 0 || account.length() > 20) {
            Toast.makeText(this, "帐号限20位字母或者数字", Toast.LENGTH_SHORT).show();

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
