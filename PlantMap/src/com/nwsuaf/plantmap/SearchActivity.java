package com.nwsuaf.plantmap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.nwsuaf.plantcontrol.HttpClientUtils;
import com.nwsuaf.plantcontrol.Plant;
import com.nwsuaf.plantcontrol.TabInfo;

/**
 * ����ҳ�� Activity
 * 
 * @author ���֡�������
 */
public class SearchActivity extends AppCompatActivity {
	private EditText search_eddit; // ������
	private Button back_button; // ���ذ�ť
	private Button clear_button; // ����������ݰ�ť�������������ݲ�Ϊ��ʱ����
	private Button clearNameHistoryButton;
	private Button clearPlaceHistoryButton;
	private Button qr_button; // ��ά��ɨ�谴��
	private String keyword; // �����ؼ���
	/* ��ά���Լ���������߳���� */
	private HttpClientUtils myHttpClientUtils;
	private Handler myHandler = null;
	private Thread currentThread = null;
	private Thread qrThread;
	private Runnable urlConnRunnable;
	private ProgressDialog progressDialog; // �ȴ��Ի���

	private int tabSelectionIndex = 1; // ��ѡ����Tab������,1Ϊ��ֲ��,2Ϊ���ص�

	private ListView nameResultListView;
	private ArrayList<HashMap<String, String>> nameResultInfoList; // ������������б�
	private ListView placeResultListView;
	private ArrayList<HashMap<String, String>> placeResultInfoList; // �ص���������б�
	private SimpleAdapter nameResultListAdapter; // ������������б�������
	private SimpleAdapter placeResultListAdapter; // �ص���������б�������

	private TextView nameHistoryTitle;
	private ListView nameHistoryListView;
	private ArrayList<String> nameHistoryList; // ������ʷ��¼�б�
	private TextView placeHistoryTitle;
	private ListView placeHistoryListView;
	private ArrayList<String> placeHistoryList; // �ص���ʷ��¼�б�
	private ArrayAdapter<String> nameHistoryListAdapter; // ������ʷ��¼�б�������
	private ArrayAdapter<String> placeHistoryListAdapter; // �ص���ʷ��¼�б�������

	private SharedPreferences nameHistoryPreferences; // ������ʷ��¼ƫ��
	private SharedPreferences.Editor nameHistoryPreferencesEditor; // ������ʷ��¼ƫ�ñ༭��
	private SharedPreferences placeHistoryPreferences; // �ص���ʷ��¼ƫ��
	private SharedPreferences.Editor placeHistoryPreferencesEditor; // �ص���ʷ��¼ƫ�ñ༭��

	/* Tab�� */
	private View resultNameView, resultPlaceView;
	private TabLayout tabLayout;
	private List<String> titleList;
	private ViewPager viewPager;
	private List<View> viewList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		init();
		initHandler();
		// ����ǰ�ؼ��ʲ�Ϊ�գ���ʾ��ؿؼ�
		if (keyword.length() != 0) {
			clear_button.setVisibility(View.VISIBLE);
			mySlurPlant(keyword, 1);
			mySlurPlant(keyword, 2);
		}
	}

	/**
	 * �����ʼ��
	 */
	protected void init() {
		// �������ʼ��
		search_eddit = (EditText) findViewById(R.id.search_edit);
		search_eddit.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if (arg1 == EditorInfo.IME_ACTION_SEND
						|| (arg2 != null && arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
					if (keyword.length() == 0)
						finish();
					else if (tabSelectionIndex == 1)
						plantThread(keyword, "", "", "plant");
				}
				return false;
			}
		});
		// �ӳٵ�������̣���ȷ������̳ɹ�����
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) search_eddit.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(search_eddit, 0);
			}
		}, 100);

		// ��ȡ��ǰ�������ؼ���
		Intent intent = getIntent();
		keyword = intent.getStringExtra("origin_keyword");

		// ����ǰ�������ؼ������������򲢽�����ƶ������
		search_eddit.setText(keyword);
		search_eddit.setSelection(keyword.length());

		// ���ذ�ť��ʼ��
		back_button = (Button) findViewById(R.id.seach_back_button);
		back_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SearchActivity.this.finish();
			}
		});

		// ��հ�ť��ʼ��
		clear_button = (Button) findViewById(R.id.clear_button);
		clear_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				search_eddit.setText("");
				keyword = "";
			}
		});

		// ��ά��ɨ�谴����ʼ��
		qr_button = (Button) findViewById(R.id.search_qr_button);
		qr_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(SearchActivity.this, CaptureActivity.class);
				startActivityForResult(intent, 0);
			}
		});

		// ���������ݼ�������������ؿؼ��ĳ�������ʧ
		search_eddit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				nameResultInfoList.clear();
				nameResultListAdapter.notifyDataSetChanged();
				placeResultInfoList.clear();
				placeResultListAdapter.notifyDataSetChanged();

				keyword = arg0.toString().replace(" ", "");
				if (keyword.length() == 0) {
					clear_button.setVisibility(View.GONE);
					keyword = "";
				} else {
					clear_button.setVisibility(View.VISIBLE);
					mySlurPlant(keyword, 1);
					mySlurPlant(keyword, 2);
				}
			}
		});

		// Tab����������ʼ��
		viewPager = (ViewPager) findViewById(R.id.result_content);

		getLayoutInflater();
		LayoutInflater inflater = LayoutInflater.from(this);
		resultNameView = inflater.inflate(R.layout.result_list_name, (ViewGroup) null);
		resultPlaceView = inflater.inflate(R.layout.result_list_place, (ViewGroup) null);
		viewList = new ArrayList<View>();
		viewList.add(resultNameView);
		viewList.add(resultPlaceView);

		titleList = new ArrayList<String>();
		titleList.add(getString(R.string.search_name));
		titleList.add(getString(R.string.search_place));

		PagerAdapter pagerAdapter = new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return viewList.size();
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				container.removeView(viewList.get(position));
			}

			@Override
			public int getItemPosition(Object object) {
				return super.getItemPosition(object);
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return titleList.get(position);
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(viewList.get(position));

				if (position == 0) {
					// ������������б��ʼ��
					nameResultInfoList = new ArrayList<HashMap<String, String>>();

					// ������������б���������ʼ��
					nameResultListAdapter = new SimpleAdapter(SearchActivity.this, nameResultInfoList,
							R.layout.result_list_name_item, new String[] { "title", "text" },
							new int[] { R.id.name_item_title, R.id.name_item_text });
					nameResultListView = (ListView) findViewById(R.id.result_name_list);
					nameResultListView.setAdapter(nameResultListAdapter);
					nameResultListView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
							String keywordOne = nameResultInfoList.get(arg2).get("title");
							String keywordTwo = nameResultInfoList.get(arg2).get("text").replace(" ", "");
							String two[] = keywordTwo.split(",");
							if (two.length == 2)
								plantThread(keywordOne, two[0], two[1], "plant");
							keyword = keywordOne;
						}
					});

					// ������ʷ��¼�б��ʼ��
					nameHistoryTitle = (TextView) findViewById(R.id.name_history_title);
					nameHistoryList = new ArrayList<String>();
					nameHistoryPreferences = getSharedPreferences("name_history", Context.MODE_PRIVATE);
					nameHistoryPreferencesEditor = nameHistoryPreferences.edit();
					String nameHistroyString = nameHistoryPreferences.getString("name_history", "");
					if (nameHistroyString.length() != 0) {
						String[] histroyStringArray = nameHistroyString.split("#");
						for (int i = 0; i < histroyStringArray.length; i++)
							nameHistoryList.add(histroyStringArray[i]);
					}
					nameHistoryListAdapter = new ArrayAdapter<String>(SearchActivity.this, R.layout.history_list_item,
							nameHistoryList);
					nameHistoryListView = (ListView) findViewById(R.id.history_name_list);
					nameHistoryListView.setAdapter(nameHistoryListAdapter);
					nameHistoryListView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
							keyword = nameHistoryList.get(arg2);
							search_eddit.setText(keyword);
							search_eddit.setSelection(keyword.length());
						}
					});

					// ���������ʷ��¼��ť��ʼ��
					clearNameHistoryButton = (Button) findViewById(R.id.clean_name_history);
					clearNameHistoryButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							AlertDialog.Builder dialogBuilder = new Builder(SearchActivity.this);
							dialogBuilder.setTitle(R.string.clean_history);
							dialogBuilder.setMessage(R.string.clear_history_hint);
							dialogBuilder.setPositiveButton(getString(R.string.positive),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											nameHistoryList.clear();
											nameHistoryListAdapter.notifyDataSetChanged();
											nameHistoryPreferencesEditor.clear();
											nameHistoryPreferencesEditor.commit();
											nameHistoryTitle.setVisibility(View.GONE);
											clearNameHistoryButton.setVisibility(View.GONE);
											arg0.dismiss();
										}
									});
							dialogBuilder.setNegativeButton(getString(R.string.negative),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											arg0.dismiss();
										}
									});
							dialogBuilder.create().show();
						}
					});
					nameHistoryTitle.setVisibility(View.GONE);
					clearNameHistoryButton.setVisibility(View.GONE);
					if (nameHistoryList.size() != 0) {
						nameHistoryTitle.setVisibility(View.VISIBLE);
						clearNameHistoryButton.setVisibility(View.VISIBLE);
					}
				}

				if (position == 1) {
					// �ص���������б��ʼ��
					placeResultInfoList = new ArrayList<HashMap<String, String>>();

					// �ص���������б���������ʼ��
					placeResultListAdapter = new SimpleAdapter(SearchActivity.this, placeResultInfoList,
							R.layout.result_list_place_item, new String[] { "title", "text" },
							new int[] { R.id.place_item_title, R.id.place_item_text });
					placeResultListView = (ListView) findViewById(R.id.result_place_list);
					placeResultListView.setAdapter(placeResultListAdapter);
					placeResultListView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
							String keywordOne = placeResultInfoList.get(arg2).get("title");
							String keywordTwo = placeResultInfoList.get(arg2).get("text").replace(" ", "");
							plantThread(keywordOne, keywordTwo, "", "place");
							keyword = keywordOne;
						}
					});

					// �ص���ʷ��¼�б��ʼ��
					placeHistoryTitle = (TextView) findViewById(R.id.place_history_title);
					placeHistoryList = new ArrayList<String>();
					placeHistoryPreferences = getSharedPreferences("place_history", Context.MODE_PRIVATE);
					placeHistoryPreferencesEditor = placeHistoryPreferences.edit();
					String placeHistroyString = placeHistoryPreferences.getString("place_history", "");
					if (placeHistroyString.length() != 0) {
						String[] histroyStringArray = placeHistroyString.split("#");
						for (int i = 0; i < histroyStringArray.length; i++)
							placeHistoryList.add(histroyStringArray[i]);
					}
					placeHistoryListAdapter = new ArrayAdapter<String>(SearchActivity.this, R.layout.history_list_item,
							placeHistoryList);
					placeHistoryListView = (ListView) findViewById(R.id.history_place_list);
					placeHistoryListView.setAdapter(placeHistoryListAdapter);
					placeHistoryListView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
							keyword = placeHistoryList.get(arg2);
							search_eddit.setText(keyword);
							search_eddit.setSelection(keyword.length());
						}
					});

					// ����ص���ʷ��¼��ť��ʼ��
					clearPlaceHistoryButton = (Button) findViewById(R.id.clean_place_history);
					clearPlaceHistoryButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							AlertDialog.Builder dialogBuilder = new Builder(SearchActivity.this);
							dialogBuilder.setTitle(R.string.clean_history);
							dialogBuilder.setMessage(R.string.clear_history_hint);
							dialogBuilder.setPositiveButton(getString(R.string.positive),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											placeHistoryList.clear();
											placeHistoryListAdapter.notifyDataSetChanged();
											placeHistoryPreferencesEditor.clear();
											placeHistoryPreferencesEditor.commit();
											placeHistoryTitle.setVisibility(View.GONE);
											clearPlaceHistoryButton.setVisibility(View.GONE);
											arg0.dismiss();
										}
									});
							dialogBuilder.setNegativeButton(getString(R.string.negative),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											arg0.dismiss();
										}
									});
							dialogBuilder.create().show();
						}
					});
					placeHistoryTitle.setVisibility(View.GONE);
					clearPlaceHistoryButton.setVisibility(View.GONE);
					if (placeHistoryList.size() != 0) {
						placeHistoryTitle.setVisibility(View.VISIBLE);
						clearPlaceHistoryButton.setVisibility(View.VISIBLE);
					}
				}

				return viewList.get(position);
			}
		};
		viewPager.setAdapter(pagerAdapter);
		viewPager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				tabSelectionIndex = arg0 + 1;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		tabLayout.setupWithViewPager(viewPager);
		tabLayout.setTabMode(TabLayout.MODE_FIXED);
	}

	/**
	 * �����������������
	 * 
	 * @param title
	 *            ����
	 * @param text
	 *            ������
	 */
	private void addNameResultInfo(String title, String text) {
		HashMap<String, String> resultInfo = new HashMap<String, String>();
		resultInfo.put("title", title);
		resultInfo.put("text", text);

		nameResultInfoList.add(resultInfo);
		nameResultListAdapter.notifyDataSetChanged();
	}

	/**
	 * ���ӵص����������
	 * 
	 * @param title
	 *            ����
	 * @param text
	 *            ������
	 */
	private void addPlaceResultInfo(String title, String text) {
		HashMap<String, String> resultInfo = new HashMap<String, String>();
		resultInfo.put("title", title);
		resultInfo.put("text", text);

		placeResultInfoList.add(resultInfo);
		placeResultListAdapter.notifyDataSetChanged();
	}

	/**
	 * �߳�Handler��ʼ��
	 */
	@SuppressLint("HandlerLeak")
	private void initHandler() {
		myHandler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					if (progressDialog != null)
						progressDialog.dismiss();
					keyword = (String) msg.obj;
					plantThread(keyword, "", "", "plant");
				} else if (msg.what == 1) {
					if (tabSelectionIndex == 1) {
						int moveIndex = -1;
						for (int i = 0; i < nameHistoryList.size(); i++) {
							if (keyword.equals(nameHistoryList.get(i))) {
								moveIndex = i;
								break;
							}
						}
						nameHistoryList.add(0, keyword);
						if (moveIndex == -1) {
							if (nameHistoryList.size() > 5)
								nameHistoryList.remove(5);
						} else
							nameHistoryList.remove(moveIndex + 1);

						StringBuilder stringBuilder = new StringBuilder();
						String[] historyStringArray = nameHistoryList.toArray(new String[nameHistoryList.size()]);
						for (int i = 0; i < historyStringArray.length; i++) {
							stringBuilder.append(historyStringArray[i]);
							if (i != historyStringArray.length - 1)
								stringBuilder.append("#");
						}
						nameHistoryPreferencesEditor.clear();
						nameHistoryPreferencesEditor.putString("name_history", stringBuilder.toString());
						nameHistoryPreferencesEditor.commit();
					} else {
						int moveIndex = -1;
						for (int i = 0; i < placeHistoryList.size(); i++) {
							if (keyword.equals(placeHistoryList.get(i))) {
								moveIndex = i;
								break;
							}
						}
						placeHistoryList.add(0, keyword);
						if (moveIndex == -1) {
							if (placeHistoryList.size() > 5)
								placeHistoryList.remove(5);
						} else
							placeHistoryList.remove(moveIndex + 1);

						StringBuilder stringBuilder = new StringBuilder();
						String[] historyStringArray = placeHistoryList.toArray(new String[placeHistoryList.size()]);
						for (int i = 0; i < historyStringArray.length; i++) {
							stringBuilder.append(historyStringArray[i]);
							if (i != historyStringArray.length - 1)
								stringBuilder.append("#");
						}
						placeHistoryPreferencesEditor.clear();
						placeHistoryPreferencesEditor.putString("place_history", stringBuilder.toString());
						placeHistoryPreferencesEditor.commit();
					}
					if (progressDialog != null)
						progressDialog.dismiss();

					ArrayList<Plant> plants = (ArrayList<Plant>) msg.obj;
					if (plants == null)
						return;
					Intent intent = new Intent();
					Bundle bundle = msg.getData();
					bundle.putParcelableArrayList("plants", plants);
					intent.putExtras(bundle);
					intent.putExtra("keyword", keyword);
					setResult(1, intent);
					finish();
				} else if (msg.what == 2) {
					if (progressDialog != null)
						progressDialog.dismiss();
					Intent intent = new Intent();
					intent.putExtra("keyword", keyword);
					setResult(2, intent);
					finish();
				} else if (msg.what == 3) {
					if (progressDialog != null)
						progressDialog.dismiss();
					Intent intent = new Intent();
					intent.putExtra("keyword", keyword);
					setResult(3, intent);
					finish();
				} else if (msg.what == 4) {
					if (progressDialog != null)
						progressDialog.dismiss();
					Toast.makeText(getApplicationContext(), R.string.time_out, Toast.LENGTH_SHORT).show();
				} else if (msg.what == 5) {
					if (progressDialog != null)
						progressDialog.dismiss();
					Toast.makeText(getApplicationContext(), R.string.invalide_bar_code, Toast.LENGTH_SHORT).show();
				} else if (msg.what == 6) {
					if (progressDialog != null)
						progressDialog.dismiss();
					Toast.makeText(getApplicationContext(), R.string.service_error, Toast.LENGTH_SHORT).show();
				} else if (msg.what == 7) {
					ArrayList<TabInfo> tabInfos = msg.getData().getParcelableArrayList("tabInfos");
					if (tabInfos == null)
						return;
					for (TabInfo one : tabInfos) {
						addNameResultInfo(one.getPlantName(), one.getSectionName() + " , " + one.getCampusName());
					}
				} else if (msg.what == 8) {
					ArrayList<TabInfo> distrcits = msg.getData().getParcelableArrayList("distrcits");
					if (distrcits == null)
						return;
					for (TabInfo one : distrcits) {
						addPlaceResultInfo(one.getCampusName(), one.getSectionName());
					}
				}
			}
		};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 6) {
			// ����ֵΪ6�� ��ȡ��ά����Ϣ
			Bundle bundle = data.getExtras();
			final String url = bundle.getString("result");

			urlConnRunnable = new Runnable() {
				@Override
				public void run() {
					try {
						getUrlTitle(url);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			qrThread = new Thread(urlConnRunnable);
			qrThread.start();
			progressDialog = ProgressDialog.show(this, null, getString(R.string.waiting_hint));
		}
	}

	/**
	 * �Ӷ�ά���URL��ַ�л�ȡ��ҳ����
	 * 
	 * @param urlString
	 *            URL��ַ
	 * @throws IOException
	 */
	private void getUrlTitle(String urlString) throws IOException {
		int responsecode;
		HttpURLConnection httpConn;
		try {
			URL url = new URL(urlString);
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setConnectTimeout(7000);
		} catch (IOException e) {
			responsecode = -2;
			myHandler.obtainMessage(5).sendToTarget();
			return;
		}
		try {
			responsecode = httpConn.getResponseCode();
		} catch (IOException e) {
			responsecode = -2;
		}
		if (responsecode == HttpURLConnection.HTTP_OK) {
			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream(), "utf-8");
			int i;
			String content = "";
			while ((i = isr.read()) != -1) {
				content = content + (char) i;
				if (content.length() > 2048)
					break;
			}
			String titleText = "<title>";
			String titleEndText = "</title>";
			int titleStart = 0, titleEnd = 0;
			for (i = 0; i < content.length(); i++) {
				if (i + titleEndText.length() >= content.length()) {
					myHandler.obtainMessage(5).sendToTarget();
					return;
				}
				if (content.substring(i, i + titleText.length()).equals(titleText))
					titleStart = i + titleText.length();
				if (content.substring(i, i + titleEndText.length()).equals(titleEndText)) {
					titleEnd = i;
					break;
				}
			}
			content = content.substring(titleStart, titleEnd);
			if (content.length() == 0)
				myHandler.obtainMessage(5).sendToTarget();
			myHandler.obtainMessage(0, content).sendToTarget();
			isr.close();
			httpConn.disconnect();
		} else {
			myHandler.obtainMessage(5).sendToTarget();
		}
	}

	/**
	 * ��ȡֲ���б���̷߳���
	 * 
	 * @param kone
	 *            ֲ����
	 * @param ktwo
	 *            ������
	 * @param kthree
	 *            У����
	 * @param who
	 *            ��������
	 */
	private void plantThread(final String kone, final String ktwo, final String kthree, final String who) {
		myHttpClientUtils = new HttpClientUtils();

		currentThread = new Thread(new Runnable() {
			public void run() {
				ArrayList<Plant> plants = new ArrayList<Plant>();
				if (who.equalsIgnoreCase("plant")) {
					plants = myHttpClientUtils.getPlantByName(kone, ktwo, kthree);
					Bundle bundle = new Bundle();
					if (plants == null || plants.size() == 0) {
						if (myHttpClientUtils.getResponseResult().equalsIgnoreCase("timeout"))
							myHandler.obtainMessage(4).sendToTarget();// ���ӳ�ʱ
						else if (myHttpClientUtils.getResponseResult().equalsIgnoreCase("error"))
							myHandler.obtainMessage(6).sendToTarget();// ���������󷵻�6
						else
							myHandler.obtainMessage(2).sendToTarget();// ��ȡֲ������ʧ�ܷ���2
					} else {
						Message msg = new Message();
						bundle.putString("who", who);
						msg.setData(bundle);
						msg.obj = plants;
						msg.what = 1;
						myHandler.sendMessage(msg);// ��ȡ���ݳɹ�����1
					}
				} else if (who.equalsIgnoreCase("place")) {
					plants = myHttpClientUtils.getByPlace(kone, ktwo);
					if (plants == null || plants.size() == 0) {
						if (myHttpClientUtils.getResponseResult().equalsIgnoreCase("timeout"))
							myHandler.obtainMessage(4).sendToTarget();// ���ӳ�ʱ4
						else if (myHttpClientUtils.getResponseResult().equalsIgnoreCase("error"))
							myHandler.obtainMessage(6).sendToTarget();// ���������󷵻�6
						else
							myHandler.obtainMessage(3).sendToTarget();// ��ȡֲ��ص�ֱ�����ʧ�ܷ���3
					} else {
						Bundle bundle = new Bundle();
						bundle.putString("coordinate", myHttpClientUtils.getMarkBuilding());
						bundle.putString("who", "place");
						Message msg = new Message();
						msg.obj = plants;
						msg.setData(bundle);
						msg.what = 1;
						myHandler.sendMessage(msg);// ��ȡ���ݳɹ�����1
					}
				}
			}
		});
		currentThread.start();
		progressDialog = ProgressDialog.show(this, null, getString(R.string.waiting_hint));
	}

	/**
	 * ��̬���������б�
	 * 
	 * @param keyword
	 *            �ؼ���
	 * @param whichTable
	 *            Tab��������
	 */
	private void mySlurPlant(final String keyword, final int whichTable) {
		myHttpClientUtils = new HttpClientUtils();
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (whichTable == 1) {
					ArrayList<TabInfo> tabInfos = myHttpClientUtils.getSlurPlantByName(keyword);
					Bundle bundle = new Bundle();
					Message msg = new Message();
					bundle.putParcelableArrayList("tabInfos", tabInfos);
					msg.setData(bundle);
					msg.what = 7;
					myHandler.sendMessage(msg);
				} else if (whichTable == 2) {
					ArrayList<TabInfo> distrcits = myHttpClientUtils.getSlurDistrcits(keyword);
					Bundle bundle = new Bundle();
					Message msg = new Message();
					bundle.putParcelableArrayList("distrcits", distrcits);
					msg.setData(bundle);
					msg.what = 8;
					myHandler.sendMessage(msg);
				}
			}
		}).start();
	}

	@Override
	public void onBackPressed() {
		if (progressDialog != null)
			progressDialog.dismiss();
		else
			SearchActivity.this.finish();
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
