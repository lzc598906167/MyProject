package com.nwsuaf.plantmap;

import com.nwsuaf.plantcontrol.StaticVal;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * �����ڡ�ҳ�� Activity
 * 
 * @author ���֡�������
 */
public class AboutActivity extends Activity {
	private Button back_button; // ���ذ�ť

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		// ���ذ�ť��ʼ��
		back_button = (Button) findViewById(R.id.about_back_button);
		back_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		TextView appInfo = (TextView) findViewById(R.id.app_info);
		appInfo.setText(StaticVal.app_name + " " + StaticVal.app_version);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}
