package com.nwsuaf.werewolf.main.view;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.nwsuaf.werewolf.R;

/**
 * Created by liulin on 2017/6/22.
 */
public class RegularActivity extends Activity {
    private ImageView iv_close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_regular);
        initViews();
        initListeners();
    }

    private void initListeners() {
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initViews() {
        iv_close= (ImageView) findViewById(R.id.regular_iv_close);
    }
}
