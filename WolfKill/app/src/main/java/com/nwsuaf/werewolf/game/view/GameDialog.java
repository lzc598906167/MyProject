package com.nwsuaf.werewolf.game.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nwsuaf.werewolf.R;
import com.nwsuaf.werewolf.common.util.ConfigUtil;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by liulin on 2017/6/29.
 */
public class GameDialog extends Activity implements ConfigUtil {
    private TextView game_tv_title, game_tv_content;
    private ImageView game_iv_close;
    private EditText game_et_content;
    private Button game_bt_left, game_bt_center, game_bt_right;
    private Timer timer;
    private Boolean isSavaDialog = false;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_dialog);
        initViews();
        initListeners();
        Intent intent = getIntent();
        String request_String = intent.getStringExtra(REQUEST_CODE);
        changeViews(request_String, intent);
        super.onCreate(savedInstanceState);
    }

    private void changeViews(String request_string, Intent intent) {
        if (request_string.equals("createRoom")) {
            String title = intent.getStringExtra(DIALOG_TV_TITLE);
            String content = intent.getStringExtra(DIALOG_TV_CONTENT);
            String bt_left = intent.getStringExtra(DIALOG_BT_LEFT);
            String bt_right = intent.getStringExtra(DIALOG_BT_RIGHT);
            game_tv_title.setText(title);
            game_tv_content.setText(content);
            game_bt_left.setText(bt_left);
            game_bt_right.setText(bt_right);

        } else if (request_string.equals("findRoom")) {
            String title = intent.getStringExtra(DIALOG_TV_TITLE);
            String content = intent.getStringExtra(DIALOG_ET_CONTENT);
            String bt_left = intent.getStringExtra(DIALOG_BT_LEFT);
            String bt_right = intent.getStringExtra(DIALOG_BT_RIGHT);
            game_tv_title.setText(title);
            game_et_content.setHint(content);
            game_bt_left.setText(bt_left);
            game_bt_right.setText(bt_right);
            game_et_content.setVisibility(View.VISIBLE);
            game_tv_content.setVisibility(View.GONE);
        } else if (request_string.equals("saveOrNotGamer")) {
            String title = intent.getStringExtra(DIALOG_TV_TITLE);
            String content = intent.getStringExtra(DIALOG_TV_CONTENT);
            String bt_left = intent.getStringExtra(DIALOG_BT_LEFT);
            String bt_right = intent.getStringExtra(DIALOG_BT_RIGHT);
            game_tv_title.setText(title);
            game_tv_content.setText(content);
            game_bt_left.setText(bt_left);
            game_bt_right.setText(bt_right);
            this.setTheme(R.style.AppTheme);
            isSavaDialog = true;
            seconds = 10;
            startTimer();
        } else if (request_string.equals("poisonOrNotGamer")) {
            String title = intent.getStringExtra(DIALOG_TV_TITLE);
            String content = intent.getStringExtra(DIALOG_TV_CONTENT);
            String bt_left = intent.getStringExtra(DIALOG_BT_LEFT);
            String bt_right = intent.getStringExtra(DIALOG_BT_RIGHT);
            game_tv_title.setText(title);
            game_tv_content.setText(content);
            game_bt_left.setText(bt_left);
            game_bt_right.setText(bt_right);
            this.setTheme(R.style.AppTheme);
            seconds = 20;
            startTimer();
        }

    }

    private void initViews() {
        game_tv_title = (TextView) findViewById(R.id.game_tv_title);
        game_tv_content = (TextView) findViewById(R.id.game_tv_content);
        game_iv_close = (ImageView) findViewById(R.id.game_iv_close);
        game_et_content = (EditText) findViewById(R.id.game_et_content);
        game_bt_left = (Button) findViewById(R.id.game_bt_left);
        game_bt_center = (Button) findViewById(R.id.game_bt_center);
        game_bt_right = (Button) findViewById(R.id.game_bt_right);
    }

    private void initListeners() {
        game_bt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String left_text = game_bt_left.getText().toString();
                if (left_text.equals("确定")) {
                    String content = game_et_content.getText().toString().trim();
                    if (!content.equals("")) {
                        Intent intent = new Intent();
                        intent.putExtra(DIALOG_TO_MAIN, "确定");
                        Log.i("Dialog",content);
                        intent.putExtra("content", content);
                        setResult(2, intent);
                        finish();
                    }
                } else if (left_text.equals("简单")) {
                    Intent intent = new Intent();
                    intent.putExtra(DIALOG_TO_MAIN, "简单");
                    setResult(2, intent);
                    finish();
                } else if (left_text.equals("救")) {
                    timer.cancel();
                    Intent intent = new Intent();
                    intent.putExtra(DIALOG_TO_GAME, "救");
                    setResult(1, intent);
                    finish();
                } else if (left_text.equals("毒")) {
                    timer.cancel();
                    Intent intent = new Intent();
                    intent.putExtra(DIALOG_TO_GAME, "毒");
                    setResult(1, intent);
                    finish();
                }
            }
        });
        game_bt_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        game_bt_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String right_text = game_bt_right.getText().toString();
                if (right_text.equals("取消")) {
                    finish();
                } else if (right_text.equals("困难")) {
                    Intent intent = new Intent();
                    intent.putExtra(DIALOG_TO_MAIN, "困难");
                    setResult(2, intent);
                    finish();
                } else if (right_text.equals("不救")) {
                    timer.cancel();
                    Intent intent = new Intent();
                    intent.putExtra(DIALOG_TO_GAME, "不救");
                    setResult(1, intent);
                    finish();
                } else if (right_text.equals("不毒")) {
                    timer.cancel();
                    finish();
                }
            }
        });

    }

    private int seconds = 0;

    private void startTimer() {
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            seconds--;
            if (seconds == 0) {
                if (isSavaDialog) {
                    timer.cancel();
                    Intent intent = new Intent();
                    intent.putExtra(DIALOG_TO_GAME, "不救");
                    setResult(1, intent);
                    finish();
                }
            }
        }
    };
}
