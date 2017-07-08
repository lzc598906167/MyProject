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
 * ƫ������ҳ�� Activity
 * 
 * @author ���֡�������
 */
public class SettingActivity extends Activity {
	private Button back_button; // ���ذ�ť
	private DetachableAreaMap mDetachableAreaMap; // ���ߵ�ͼ����
	private long countingTime;
	private int count = 0;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		// ��ƫ�ò����������ǰ����
		SettingsFragment settingsFragment = new SettingsFragment();
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.add(R.id.settings_content, settingsFragment);
		transaction.commit();

		// ���ذ�ť��ʼ��
		back_button = (Button) findViewById(R.id.back_button);
		back_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SettingActivity.this.finish();
			}
		});

		// �����������淵�صķ���ֵ
		Intent intent = new Intent();
		setResult(5, intent);

		mDetachableAreaMap = new DetachableAreaMap();
	}

	/**
	 * ƫ�ÿ�����
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

			// ��ʼ�������Ϣ
			Preference about = (Preference) getPreferenceScreen().findPreference("about");
			String about_title = getString(R.string.about) + " " + StaticVal.app_name;
			about.setTitle(about_title);
			Preference versionInfo = (Preference) getPreferenceScreen()
					.findPreference("current_version");
			versionInfo.setSummary(StaticVal.app_version);

			// ��ȡƫ�������ļ�
			sharedPreferences = getSharedPreferences("preferences", 0);
			preferencesEditor = sharedPreferences.edit();

			// ��ʼ��ƫ������
			keepScreenOnCheckBox = (CheckBoxPreference) findPreference("keep_screen_on");
			if (sharedPreferences.getInt("keep_screen_on", 0) == 1)
				keepScreenOnCheckBox.setChecked(true);
			else
				keepScreenOnCheckBox.setChecked(false);
		}

		// ƫ��ѡ�������
		@Override
		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
			if (preference.getKey().equals("keep_screen_on")) {
				// ��Ļ����
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
				// �������ߵ�ͼ
				mDetachableAreaMap.remove();

				// ����������ʷ��¼
				SharedPreferences nameHistoryPreferences = getSharedPreferences("name_history", Context.MODE_PRIVATE);
				SharedPreferences.Editor nameHistoryPreferencesEditor = nameHistoryPreferences.edit();
				nameHistoryPreferencesEditor.clear();
				nameHistoryPreferencesEditor.commit();
				SharedPreferences placeHistoryPreferences = getSharedPreferences("place_history", Context.MODE_PRIVATE);
				SharedPreferences.Editor placeHistoryPreferencesEditor = placeHistoryPreferences.edit();
				placeHistoryPreferencesEditor.clear();
				placeHistoryPreferencesEditor.commit();

				// ��������
				Toast.makeText(getApplicationContext(), R.string.done_clean_cache, Toast.LENGTH_SHORT).show();
			}

			if (preference.getKey().equals("current_version")) {
				// �ʵ�
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
				// ����
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
