package com.nwsuaf.plantmap;

import com.nwsuaf.plantcontrol.DetachableAreaMap;
import com.nwsuaf.plantcontrol.StaticVal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * 偏好设置页面 Activity
 * 
 * @author 刘林、王俊杰
 */
public class SettingActivity extends Activity {
	private Button back_button; // 返回按钮
	private DetachableAreaMap mDetachableAreaMap; // 离线地图管理
	private long countingTime;
	private int count = 0;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		// 将偏好布局适配进当前布局
		SettingsFragment settingsFragment = new SettingsFragment();
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.add(R.id.settings_content, settingsFragment);
		transaction.commit();

		// 返回按钮初始化
		back_button = (Button) findViewById(R.id.back_button);
		back_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SettingActivity.this.finish();
			}
		});

		// 设置向主界面返回的返回值
		Intent intent = new Intent();
		setResult(5, intent);

		mDetachableAreaMap = new DetachableAreaMap();
	}

	/**
	 * 偏好控制类
	 */
	@SuppressLint("NewApi")
	public class SettingsFragment extends PreferenceFragment {
		private SharedPreferences sharedPreferences;
		private SharedPreferences.Editor preferencesEditor;
		private CheckBoxPreference keepScreenOnCheckBox;

		@SuppressLint("CommitPrefEdits")
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);

			// 初始化软件信息
			Preference about = (Preference) getPreferenceScreen().findPreference("about");
			String about_title = getString(R.string.about) + " " + StaticVal.app_name;
			about.setTitle(about_title);
			Preference versionInfo = (Preference) getPreferenceScreen()
					.findPreference("current_version");
			versionInfo.setSummary(StaticVal.app_version);

			// 读取偏好配置文件
			sharedPreferences = getSharedPreferences("preferences", 0);
			preferencesEditor = sharedPreferences.edit();

			// 初始化偏好配置
			keepScreenOnCheckBox = (CheckBoxPreference) findPreference("keep_screen_on");
			if (sharedPreferences.getInt("keep_screen_on", 0) == 1)
				keepScreenOnCheckBox.setChecked(true);
			else
				keepScreenOnCheckBox.setChecked(false);
		}

		// 偏好选项监听器
		@Override
		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
			if (preference.getKey().equals("keep_screen_on")) {
				// 屏幕常亮
				if (sharedPreferences.getInt("keep_screen_on", 0) == 0) {
					keepScreenOnCheckBox.setChecked(true);
					preferencesEditor.putInt("keep_screen_on", 1);
					preferencesEditor.commit();
				} else {
					keepScreenOnCheckBox.setChecked(false);
					preferencesEditor.putInt("keep_screen_on", 0);
					preferencesEditor.commit();
				}
			}
			if (preference.getKey().equals("clear_data")) {
				// 清理离线地图
				mDetachableAreaMap.remove();

				// 清理搜索历史记录
				SharedPreferences nameHistoryPreferences = getSharedPreferences("name_history", Context.MODE_PRIVATE);
				SharedPreferences.Editor nameHistoryPreferencesEditor = nameHistoryPreferences.edit();
				nameHistoryPreferencesEditor.clear();
				nameHistoryPreferencesEditor.commit();
				SharedPreferences placeHistoryPreferences = getSharedPreferences("place_history", Context.MODE_PRIVATE);
				SharedPreferences.Editor placeHistoryPreferencesEditor = placeHistoryPreferences.edit();
				placeHistoryPreferencesEditor.clear();
				placeHistoryPreferencesEditor.commit();

				// 缓存清理
				Toast.makeText(getApplicationContext(), R.string.done_clean_cache, Toast.LENGTH_SHORT).show();
			}

			if (preference.getKey().equals("current_version")) {
				// 彩蛋
				if (System.currentTimeMillis() - countingTime > 1000)
					count = 1;
				else
					count++;
				countingTime = System.currentTimeMillis();
				if (count >= 10) {
					count = 0;
					Intent intent = new Intent();
					intent.setClass(SettingActivity.this, SubActivity.class);
					startActivity(intent);
				}
			}

			if (preference.getKey().equals("about")) {
				// 关于
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this, AboutActivity.class);
				startActivity(intent);
			}

			return super.onPreferenceTreeClick(preferenceScreen, preference);
		}
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
