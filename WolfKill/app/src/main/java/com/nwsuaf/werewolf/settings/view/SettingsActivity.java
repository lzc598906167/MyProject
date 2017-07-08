package com.nwsuaf.werewolf.settings.view;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.media.picker.PickImageHelper;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.session.actions.PickImageAction;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.nwsuaf.werewolf.DemoCache;
import com.nwsuaf.werewolf.common.util.ConfigUtil;
import com.nwsuaf.werewolf.httputil.UserHttpClient;
import com.nwsuaf.werewolf.R;
import java.io.File;


public class SettingsActivity extends AppCompatActivity  implements ConfigUtil{
    private static final int PICK_AVATAR_REQUEST = 0x0E;
    private static final int AVATAR_TIME_OUT = 30000;

    AbortableFuture<String> uploadAvatarFuture;
    private RadioGroup rg;
    private Button bt_sure;
    private ClearableEditTextWithIcon et_name;
    private HeadImageView settings_cv_icon;
    private NimUserInfo userInfo;
    private String account;
    RadioButton male, female;
    private Boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_settings);
        initViews();
        initListers();
        getUserInfo();
    }

    private void initListers() {
        settings_cv_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImageHelper.PickImageOption option = new PickImageHelper.PickImageOption();
                option.titleResId = R.string.set_head_image;
                option.crop = true;
                option.multiSelect = false;
                option.cropOutputImageWidth = 720;
                option.cropOutputImageHeight = 720;
                PickImageHelper.pickImage(SettingsActivity.this, PICK_AVATAR_REQUEST, option);
            }
        });

        bt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et_name.getText().toString().trim();
                if (!name.matches("^[\\u4E00-\\u9FA5A-Za-z0-9]{1,10}$")) {
                    Toast.makeText(SettingsActivity.this, "昵称限10位汉字、字母或者数字", Toast.LENGTH_SHORT).show();
                    return;
                }
                int id = rg.getCheckedRadioButtonId();

                int gender = 0;
                if (id == R.id.settings_rb_nan) {
                    gender = 1;
                } else {
                    gender = 2;
                }
                DialogMaker.showProgressDialog(SettingsActivity.this, null, null, true, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        cancelUpload(R.string.user_info_update_cancel);
                    }
                }).setCanceledOnTouchOutside(true);
                UserHttpClient.getInstance().setUserInfo(DemoCache.getAccount(), gender, name, new UserHttpClient.UserHttpCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SettingsActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        DialogMaker.dismissProgressDialog();
                        setResult(1);
                        finish();
                    }
                    @Override
                    public void onFailed(int code, String errorMsg) {
                        Log.i("TGA", getString(R.string.set_userinfo_failed, String.valueOf(code), errorMsg));
                    }
                });
            }
        });
    }

    private void initViews() {
        rg = (RadioGroup) findViewById(R.id.settings_gp);
        bt_sure = (Button) findViewById(R.id.settings_bt_sure);
        et_name = (ClearableEditTextWithIcon) findViewById(R.id.settings_et_name);
        settings_cv_icon = (HeadImageView) findViewById(R.id.settings_cv_icon);
        male = (RadioButton) findViewById(R.id.settings_rb_nan);
        female = (RadioButton) findViewById(R.id.settings_rb_nv);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_AVATAR_REQUEST) {
            String path = data.getStringExtra(com.netease.nim.uikit.session.constant.Extras.EXTRA_FILE_PATH);
            updateAvatar(path);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(1);
        super.onBackPressed();
    }

    private void updateAvatar(final String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }

        File file = new File(path);
        if (file == null) {
            return;
        }

        DialogMaker.showProgressDialog(this, null, null, true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelUpload(R.string.user_info_update_cancel);
            }
        }).setCanceledOnTouchOutside(true);

        LogUtil.i("TAG", "start upload avatar, local file path=" + file.getAbsolutePath());
        new Handler().postDelayed(outimeTask, AVATAR_TIME_OUT);
        uploadAvatarFuture = NIMClient.getService(NosService.class).upload(file, PickImageAction.MIME_JPEG);
        uploadAvatarFuture.setCallback(new RequestCallbackWrapper<String>() {
            @Override
            public void onResult(int code, String url, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS && !TextUtils.isEmpty(url)) {
                    LogUtil.i("TAG", "upload avatar success, url =" + url);

                    UserUpdateHelper.update(UserInfoFieldEnum.AVATAR, url, new RequestCallbackWrapper<Void>() {
                        @Override
                        public void onResult(int code, Void result, Throwable exception) {
                            if (code == ResponseCode.RES_SUCCESS) {
                                Toast.makeText(SettingsActivity.this, R.string.head_update_success, Toast.LENGTH_SHORT).show();

                                onUpdateDone();
                            } else {
                                Toast.makeText(SettingsActivity.this, R.string.head_update_failed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }); // 更新资料
                } else {
                    Toast.makeText(SettingsActivity.this, R.string.user_info_update_failed, Toast
                            .LENGTH_SHORT).show();
                    onUpdateDone();
                }
            }
        });
    }

    private void cancelUpload(int resId) {
        if (uploadAvatarFuture != null) {
            uploadAvatarFuture.abort();
            Toast.makeText(SettingsActivity.this, resId, Toast.LENGTH_SHORT).show();
            onUpdateDone();
        }
    }

    private Runnable outimeTask = new Runnable() {
        @Override
        public void run() {
            cancelUpload(R.string.user_info_update_failed);
        }
    };

    private void onUpdateDone() {
        uploadAvatarFuture = null;
        DialogMaker.dismissProgressDialog();
        getUserInfo();
    }

    private void getUserInfo() {
        account = DemoCache.getAccount();
        userInfo = NimUserInfoCache.getInstance().getUserInfo(DemoCache.getAccount());
        if (userInfo == null) {
            NimUserInfoCache.getInstance().getUserInfoFromRemote(account, new RequestCallback<NimUserInfo>() {
                @Override
                public void onSuccess(NimUserInfo param) {
                    userInfo = param;
                    updateUI();
                }

                @Override
                public void onFailed(int code) {
                    Toast.makeText(SettingsActivity.this, "getUserInfoFromRemote failed:" + code, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onException(Throwable exception) {
                    Toast.makeText(SettingsActivity.this, "getUserInfoFromRemote exception:" + exception, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            updateUI();
        }
    }

    private void updateUI() {
        settings_cv_icon.loadBuddyAvatar(account);
        if (isFirst) {
            isFirst=false;
            et_name.setText(userInfo.getName());
            if (userInfo.getGenderEnum() != null) {
                if (userInfo.getGenderEnum() == GenderEnum.MALE) {
                    male.setChecked(true);
                } else if (userInfo.getGenderEnum() == GenderEnum.FEMALE) {
                    female.setChecked(true);
                }
            }
        }
    }
}
