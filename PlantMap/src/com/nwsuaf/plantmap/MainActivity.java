package com.nwsuaf.plantmap;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.clusterutil.clustering.Cluster;
import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.clusterutil.clustering.ClusterManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;
import com.nwsuaf.plantcontrol.Campus;
import com.nwsuaf.plantcontrol.Coordinate;
import com.nwsuaf.plantcontrol.DetachableAreaMap;
import com.nwsuaf.plantcontrol.HttpClientUtils;
import com.nwsuaf.plantcontrol.MyOrientationListener;
import com.nwsuaf.plantcontrol.MyOrientationListener.OnOrientationListener;
import com.nwsuaf.plantcontrol.Plant;
import com.nwsuaf.plantcontrol.StaticVal;

/**
 * ������Activity
 * 
 * @author �����ܡ�����
 */
public class MainActivity extends Activity
{
	private Button menu_button; // �˵���ť�������������ҳ��
	private Button search_button; // ������ť�����������������
	private Button main_back_button; // ���ذ�ť������ʾ���״̬�³��֣��������Ĭ��״̬
	private Button qr_button; // ��ά��ɨ�谴��
	private TextView no_connection_warning; // ������������ʾ��
	private Button warning_icon; // ��̾��
	private PopupWindow positionMenu; // �л��ص㵯�����ڶ���
	private ListView positionListView; // �ص��б�
	private Button position_button; // �л��ص㰴ť����������ص�ѡ���б�
	private Button locate_button; // ��λ��ť
	private Button narrow_button, enlarge_button, radar_button;// �Ŵ���С����λ�״ﰴť
	private String keyword; // �����ؼ���
	private boolean resultPage; // ���ڱ�ǵ�ǰ״̬�Ƿ�Ϊ��ʾ���״̬
	private long exitTime; // ���η��ؼ��˳�������ر��������ڼ�ʱ
	private ConnectionChangeReceiver myReceiver;

	protected MapView pMapView = null; // �ٶȵ�ͼ�ؼ�

	protected BaiduMap plantMap; // �ٶȵ�ͼ����ֲ���ͼ

	private DetachableAreaMap mDetachableAreaMap; // ���ߵ�ͼ����
	private String city = StaticVal.defaultCity; // Ĭ�����ߵ�ͼ��Ҫ���صĳ���
	private LocationClient pLocationClient; // ��λ
	private MyLocationListener pLocationListener; // ��λ������
	private boolean isFirstLocation = true; // �Ƿ��һ�ζ�λ������ǵ�һ�ζ�λ�Ļ�Ҫ���Լ���λ����ʾ�ڵ�ͼ�м�
	private float mCurrentAccracy; // ��λ����
	private double Latitude = 34.292033; // γ��
	private double Longitude = 108.078618; // ����
	private LatLng centre = new LatLng(Latitude, Longitude);
	private LatLng endLatLng; // �������յ�
	private com.baidu.mapapi.map.MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
	private MyOrientationListener myOrientationListener; // ���¶���ķ��򴫸���
	private int mXDirection; // ����
	/* ��ȡ������� */
	private HttpClientUtils myHttpClientUtils;
	private Boolean isfirst = false;
	private Handler myHandler;
	private ArrayList<Campus> campus = null; // У����������
	private ArrayList<Plant> plants = null; // ֲ���������
	private String[] positions = null; // У����������
	private ArrayAdapter<String> positionListAdapter = null; // У���б�������
	/* ������ز��� */
	public static final String TAG = "NaviSDkDemo";
	private static final String APP_FOLDER_NAME = "BNNAVI";
	public static final String ROUTE_PLAN_NODE = "routePlanNode";
	private String mSDCardPath = null;
	String authinfo = null;
	public static List<Activity> activityList = new LinkedList<Activity>();
	private final static String authBaseArr[] = { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION };
	private final static int authBaseRequestCode = 1;

	private ClusterManager<MyMarker> mClusterManager; // �ۺϵ����������ͼ������

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		activityList.add(this);
		initPermission();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SDKInitializer.initialize(getApplicationContext());
		registerReceiver(); // ����仯����
		setContentView(R.layout.activity_main);
		initView(); // ��ʼ���ؼ�����ӿؼ�����¼�
		initPlantMap(); // ��ͼ��ʼ��
		initOritationListener(); // �����ʼ��
		initLocation();/* ��λ */
		initDetachableAreaMap(); // ���ص�ǰ���е����ߵ�ͼ
		initHandler(); // ��ʼ���߳�
		// ��ʼ������
		if (initDirs()) initNavi();
		// ��ȡӦ����
		StaticVal.app_name = getString(R.string.app_name);
		// ��ȡӦ�ð汾��
		StaticVal.app_version = getVersion();
	}

	/**
	 * ע�����������
	 */
	private void registerReceiver()
	{
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		myReceiver = new ConnectionChangeReceiver();
		this.registerReceiver(myReceiver, filter);
	}

	/**
	 * ����ʱȨ�޴���
	 */
	private void initPermission()
	{
		int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
		int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
		int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if (locationPermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED || writePermission != PackageManager.PERMISSION_GRANTED)
			ActivityCompat
					.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
	}

	/**
	 * ��ʼ����ͼ����
	 */
	private void initPlantMap()
	{
		pMapView = (MapView) findViewById(R.id.plantmapview);
		// pMapView.removeViewAt(1); // ȥ���ٶ�LOGO
		pMapView.showScaleControl(false); // ȥ��������
		pMapView.showZoomControls(false); // ȥ�����ſؼ�
		plantMap = pMapView.getMap(); // �õ���ͼ
		plantMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); // ������ͨ��ͼ
		/* �ı��ͼ״̬��ʹ��ͼ��ʾ��ǡ�������Ŵ�Сͬʱ������ʼλ�� */
		MapStatus pMapStatus = new MapStatus.Builder().target(centre).zoom(19).build();
		MapStatusUpdate pMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(pMapStatus);
		plantMap.setMapStatus(pMapStatusUpdate);
		mClusterManager = new ClusterManager<MyMarker>(this, plantMap);
		mClusterManager.setNarrow_button(narrow_button);
		plantMap.setOnMapStatusChangeListener(mClusterManager);
		// ����maker���ʱ����Ӧ
		plantMap.setOnMarkerClickListener(mClusterManager);
		mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyMarker>()
		{
			@Override
			public boolean onClusterClick(Cluster<MyMarker> cluster)
			{
				List<MyMarker> items = (List<MyMarker>) cluster.getItems();
				LatLngBounds.Builder builder2 = new LatLngBounds.Builder();
				for (MyMarker myItem : items)
					builder2 = builder2.include(myItem.getPosition());
				LatLngBounds latlngBounds = builder2.build();
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(latlngBounds, pMapView.getWidth(), pMapView.getHeight());
				plantMap.animateMapStatus(u);

				return false;
			}
		});
		mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyMarker>()
		{
			@Override
			public boolean onClusterItemClick(MyMarker item)
			{
				Plant plant = item.getmPlant();
				Bundle bundle = new Bundle();
				bundle.putParcelable("plant", plant);
				Intent intent = new Intent();
				intent.putExtra("bundle", bundle);
				intent.setClass(MainActivity.this, DetailActivity.class);
				startActivityForResult(intent, 0);
				return false;
			}
		});
	}

	/**
	 * ��λ��ʼ��
	 */
	private void initLocation()
	{
		/* ��λ�ͻ��˵����� */
		pLocationClient = new LocationClient(this);
		pLocationListener = new MyLocationListener();
		/* ע����� */
		pLocationClient.registerLocationListener(pLocationListener);
		/* ���ö�λ */
		LocationClientOption locationoption = new LocationClientOption();
		locationoption.setCoorType("bd09ll"); // ��������
		locationoption.setOpenGps(true); // ��GPS
		locationoption.setScanSpan(1000); // 1000���붨λһ��
		locationoption.setLocationMode(LocationMode.Hight_Accuracy);// ���ö�λģʽ
		locationoption.setIsNeedAddress(true); // ���صĶ�λ���������ַ��Ϣ
		locationoption.setNeedDeviceDirect(true); // ���صĶ�λ��������ֻ���ͷ�ķ���
		pLocationClient.setLocOption(locationoption); // �����õ���Ϣ������λ�ͻ��ˣ���Ȼ���а�Ĭ������
	}

	/**
	 * ��WIFI���������£��Զ��������ߵ�ͼ
	 */
	private void initDetachableAreaMap()
	{
		mDetachableAreaMap = new DetachableAreaMap();
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// ��ȡ�������Ļ������Ϣ����
		NetworkInfo activeInfo = conMgr.getActiveNetworkInfo();
		if (activeInfo != null && activeInfo.isConnected())
		{
			// �鿴������������
			boolean wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
			// �ж��Ƿ�ΪWIFI����
			if (wifiConnected)
			{
				if (!mDetachableAreaMap.isExist(city))
				{
					int cityID = mDetachableAreaMap.getCityIdbyName(city);
					if (cityID != -1) mDetachableAreaMap.start(cityID); // ��ʼ�������ص�ͼ
				}
			}
		}
	}

	/**
	 * �����ϵĿؼ��ļ����¼���ʼ��
	 */
	private void initView()
	{
		resultPage = false;
		keyword = "";
		exitTime = 0;

		// �Ŵ�ť
		enlarge_button = (Button) findViewById(R.id.enlarge_button);
		enlarge_button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (mClusterManager.getZoomLevel() < 21)
				{
					plantMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
					narrow_button.setEnabled(true);
				}
				else
				{
					Toast.makeText(getApplicationContext(), R.string.max_scale, Toast.LENGTH_SHORT).show();
					enlarge_button.setEnabled(false);
				}
			}
		});

		// ��С��ť
		narrow_button = (Button) findViewById(R.id.narrow_button);
		narrow_button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (mClusterManager.getZoomLevel() > 17.000)
				{
					plantMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
					enlarge_button.setEnabled(true);
				}
				else
				{
					Toast.makeText(getApplicationContext(), R.string.min_scale, Toast.LENGTH_SHORT).show();
					narrow_button.setEnabled(false);
				}
			}
		});

		// �˵���ť��ʼ��
		menu_button = (Button) findViewById(R.id.menu_button);
		menu_button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SettingActivity.class);
				startActivityForResult(intent, 0);
			}
		});

		// ������ť��ʼ��
		search_button = (Button) findViewById(R.id.search_button);
		search_button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SearchActivity.class);
				intent.putExtra("origin_keyword", keyword);
				startActivityForResult(intent, 0);
			}
		});

		// ��ά��ɨ�谴����ʼ��
		qr_button = (Button) findViewById(R.id.qr_button);
		qr_button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SearchActivity.class);
				intent.putExtra("origin_keyword", keyword);
				startActivityForResult(intent, 0);
			}
		});

		// ���������İ�ť
		radar_button = (Button) findViewById(R.id.radar_button);
		radar_button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				plantMap.clear();
				plants = null;
				if (resultPage == false) returnResult();
				else
				{
					keyword = "";
					search_button.setText(R.string.search_hint);
				}
				center2myLoc();
				showNearbyplantThread(Latitude, Longitude);
			}
		});

		// ���ذ�ť��ʼ��
		main_back_button = (Button) findViewById(R.id.main_back_button);
		main_back_button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				back();
			}
		});

		// ��ǰλ�ð�ť����λ��
		locate_button = (Button) findViewById(R.id.locate_button);
		locate_button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				isFirstLocation = true;
				center2myLoc();
			}
		});

		// �л��ص㰴ť��ʼ��
		position_button = (Button) findViewById(R.id.location_button);
		position_button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				initPositionMenu(arg0.getRootView());
			}
		});
		// ������������ʾ���ʼ��
		no_connection_warning = (TextView) findViewById(R.id.no_connection_warning);
		warning_icon = (Button) findViewById(R.id.warning_icon);
		setScreenBehavior(); // ������Ļ����
	}

	/**
	 * �л��ص㴰�ڳ�ʼ��
	 * 
	 * @param view
	 *            ��View
	 */
	private void initPositionMenu(View view)
	{
		if (positionMenu != null && positionMenu.isShowing()) return;

		// ��ʼ������
		final ViewGroup nullParent = null;
		RelativeLayout popupLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.position_menu, nullParent);
		positionMenu = new PopupWindow(popupLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		positionMenu.setFocusable(true);
		positionMenu.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		positionMenu.setAnimationStyle(R.style.PopupAnimation);
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		positionMenu.showAtLocation(view, Gravity.START | Gravity.BOTTOM, 0, -location[1]);
		positionListView = (ListView) popupLayout.findViewById(R.id.position_list);

		// ��ȡ����
		if (campus == null)
		{
			locationThread();
			positions = new String[0];
		}
		else
		{
			positions = new String[campus.size()];
			for (int i = 0; i < campus.size(); i++)
				positions[i] = campus.get(i).getName();
		}
		positionListAdapter = new ArrayAdapter<String>(this, R.layout.list_item, positions);
		positionListView.setAdapter(positionListAdapter);
		// ���õ�������������ļ�����
		popupLayout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				positionMenu.dismiss();
			}
		});
		// �����б�����������Ҫ�˳��ļ�����
		TextView titleView = (TextView) popupLayout.findViewById(R.id.popup_title);
		titleView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// Do NOTHING
			}
		});
		// ��Ӧ����б���ļ�����
		positionListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				String item = (String) arg0.getItemAtPosition(arg2);
				positionMenu.dismiss();
				if (campus != null)
				{
					for (int i = 0; i < campus.size(); i++)
					{
						if (campus.get(i).getName().equals(item))
						{
							LatLng centre = new LatLng(campus.get(i).getCoordinate().getLat(), campus.get(i).getCoordinate().getLog());
							MapStatusUpdate us = MapStatusUpdateFactory.newLatLng(centre);
							plantMap.animateMapStatus(us);
							Toast.makeText(getApplicationContext(), getString(R.string.switch_to) + item, Toast.LENGTH_SHORT).show();
							return;
						}
					}
				}
			}
		});
	}

	/**
	 * �ƶ�����λ��λ����
	 */
	private void center2myLoc()
	{
		centre = new LatLng(Latitude, Longitude);
		MapStatusUpdate us = MapStatusUpdateFactory.newLatLngZoom(centre, 19);
		plantMap.animateMapStatus(us);
		narrow_button.setEnabled(true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == 1)
		{
			// ����ֵΪ1�������ɹ�����ȡ�����ؼ��֣������������ʾ���״̬
			keyword = data.getStringExtra("keyword");
			search_button.setText(keyword);
			plants = data.getParcelableArrayListExtra("plants");
			if (plants != null)
			{
				for (Plant p : plants)
					p.clearPartCoodinate((float) 4.0);
			}
			isfirst = true;
			if (data.getStringExtra("who").equalsIgnoreCase("place"))
			{
				isfirst = false;
				String oneString = data.getStringExtra("coordinate");
				Coordinate coordinate = new Coordinate(oneString);
				LatLng mycentre = new LatLng(coordinate.getLat(), coordinate.getLog());
				MapStatusUpdate us = MapStatusUpdateFactory.newLatLngZoom(mycentre, (float) 19.00);
				plantMap.animateMapStatus(us);
			}
			addOverlay(plants);
			returnResult();
		}
		else if (resultCode == 2)
		{
			// ����ֵΪ2������ֲ������ʧ�ܣ������淵��Ĭ��״̬
			keyword = data.getStringExtra("keyword");
			Toast.makeText(getApplicationContext(), getString(R.string.name_not_found) + "��" + keyword + "��", Toast.LENGTH_SHORT).show();
			back();
		}
		else if (resultCode == 3)
		{
			// ����ֵΪ3������ֲ��ֲ��ص�ʧ�ܣ������淵��Ĭ��״̬
			keyword = data.getStringExtra("keyword");
			Toast.makeText(getApplicationContext(), getString(R.string.place_not_found) + "��" + keyword + "��", Toast.LENGTH_SHORT).show();
			back();
		}
		else if (resultCode == 5)
		{
			// ����ֵΪ5����ȡ��Ӧ���û�ƫ�����ò���
			setScreenBehavior();
		}
		else if (resultCode == 6)
		{
			// ����ֵΪ6�� ��ȡ��ά����Ϣ
			Bundle bundle = data.getExtras();
			String url = bundle.getString("result");
			search_button.setText(url);
		}
		else if (resultCode == 7)
		{
			// ����ֵΪ7�� ��������δ��Ӧ
			Toast.makeText(getApplicationContext(), R.string.service_fail, Toast.LENGTH_SHORT).show();
			back();
		}
		else if (requestCode == 8)
		{
			// ����ֵΪ7�� ��ȡ�ص�����ʧ��
			Toast.makeText(getApplicationContext(), R.string.unkonwn_error, Toast.LENGTH_SHORT).show();
			back();
		}
		else if (resultCode == 9)
		{
			// ����ֵΪ9�� ��������
			Bundle bundle = data.getExtras();
			Coordinate coordinate = bundle.getParcelable("coordinate");
			plants.clear();
			showNearbyplantThread(coordinate.getLat(), coordinate.getLog());
			LatLng latLng = new LatLng(coordinate.getLat(), coordinate.getLog());
			MapStatusUpdate us = MapStatusUpdateFactory.newLatLngZoom(latLng, 19);
			plantMap.animateMapStatus(us);
			narrow_button.setEnabled(true);
		}
		else if (resultCode == 10)
		{
			// ����ֵΪ10�� ����
			Bundle bundle = data.getExtras();
			Coordinate coordinate = bundle.getParcelable("coordinate");
			endLatLng = new LatLng(coordinate.getLat(), coordinate.getLog());
			center2myLoc();
			if (initDirs()) initNavi();
			BNRoutePlanNode sNode = new BNRoutePlanNode(centre.longitude, centre.latitude, "���", null, CoordinateType.BD09LL);
			BNRoutePlanNode eNode = new BNRoutePlanNode(endLatLng.longitude, endLatLng.latitude, "�յ�", null, CoordinateType.BD09LL);
			if (sNode != null && eNode != null)
			{
				List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
				list.add(sNode);
				list.add(eNode);
				if (BaiduNaviManager.isNaviInited())
				{
					BaiduNaviManager.getInstance().launchNavigator(this, // ������Ӧ�õ���Activity
							list, // �������·�ڵ㣬˳������㡢;���㡢�յ㣬����;�����������
							1, // ��·ƫ�� 1:�Ƽ� 8:���շ� 2:�������� 4:���߸��� 16:���ӵ��
							true, // true��ʾ��ʵGPS������false��ʾģ�⵼��
							new DemoRoutePlanListener(sNode)// ��ʼ�����ص����������ڸü�������һ���ǽ��뵼������ҳ��
							);
				}
			}
		}
	}

	/**
	 * ���������¼�֮һ
	 */
	public class DemoRoutePlanListener implements RoutePlanListener
	{
		private BNRoutePlanNode mBNRoutePlanNode = null;

		public DemoRoutePlanListener(BNRoutePlanNode node)
		{
			mBNRoutePlanNode = node;
		}

		@Override
		public void onJumpToNavigator()
		{
			for (Activity ac : activityList)
			{
				if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) return;
			}
			// ����activity
			Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
			intent.putExtras(bundle);
			startActivity(intent);
		}

		@Override
		public void onRoutePlanFailed()
		{
			Toast.makeText(getApplicationContext(), R.string.calculate_route_error, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * @Fields ttsHandler : ������handle����
	 */
	@SuppressLint("HandlerLeak")
	private Handler ttsHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			int type = msg.what;
			switch (type) {
			case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG:
				break;
			case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG:
				break;
			default:
				break;
			}
		}
	};

	/**
	 * @Fields ttsPlayStateListener : ������������������
	 */
	private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener()
	{
		@Override
		public void playEnd()
		{
		}

		@Override
		public void playStart()
		{
		}
	};

	/**
	 * �����Ĵ洢״̬
	 * 
	 * @return �����Ĵ洢״̬
	 */
	private boolean initDirs()
	{
		mSDCardPath = getSdcardDir();
		if (mSDCardPath == null) return false;
		File f = new File(mSDCardPath, APP_FOLDER_NAME);
		if (!f.exists())
		{
			try
			{
				f.mkdir();
			} catch (Exception e)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * ������Ȩ�޻�ȡ
	 * 
	 * @return ����Ȩ���ַ���
	 */
	private String getSdcardDir()
	{
		if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) return Environment.getExternalStorageDirectory().toString();
		return null;
	}

	/**
	 * ������Ȩ���ж�
	 * 
	 * @return ����Ȩ��
	 */
	private boolean hasBasePhoneAuth()
	{
		PackageManager pm = this.getPackageManager();
		for (String auth : authBaseArr)
		{
			if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) return false;
		}
		return true;
	}

	/**
	 * ��ʼ���������������Ĳ���
	 */
	@SuppressLint("NewApi")
	private void initNavi()
	{
		// ����Ȩ��
		if (android.os.Build.VERSION.SDK_INT >= 23)
		{
			if (!hasBasePhoneAuth())
			{
				this.requestPermissions(authBaseArr, authBaseRequestCode);
				return;
			}
		}

		BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new NaviInitListener()
		{
			@Override
			public void onAuthResult(int status, String msg)
			{
				if (0 == status) authinfo = "keyУ��ɹ�!";
				else authinfo = "keyУ��ʧ��, " + msg;
				MainActivity.this.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
					}
				});
			}

			@Override
			public void initSuccess()
			{
				initSetting();
			}

			@Override
			public void initStart()
			{
			}

			@Override
			public void initFailed()
			{
				Toast.makeText(MainActivity.this, R.string.navi_init_fail, Toast.LENGTH_SHORT).show();
			}
		}, mTTSCallback, ttsHandler, ttsPlayStateListener);
	}

	/**
	 * �����Ĳ�������
	 */
	private void initSetting()
	{
		BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
		BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
		// BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
		BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
		BNaviSettingManager.setIsAutoQuitWhenArrived(true);
		Bundle bundle = new Bundle();
		// ��������APPID������ᾲ��
		bundle.putString(BNCommonSettingParam.TTS_APP_ID, "8738328");
		BNaviSettingManager.setNaviSdkParam(bundle);
	}

	private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback()
	{
		@Override
		public void stopTTS()
		{
		}

		@Override
		public void resumeTTS()
		{
		}

		@Override
		public void releaseTTSPlayer()
		{
		}

		@Override
		public int playTTSText(String speech, int bPreempt)
		{
			return 0;
		}

		@Override
		public void phoneHangUp()
		{
		}

		@Override
		public void phoneCalling()
		{
		}

		@Override
		public void pauseTTS()
		{
		}

		@Override
		public void initTTSPlayer()
		{
		}

		@Override
		public int getTTSState()
		{
			return 0;
		}
	};

	/**
	 * ������Ľ��״̬����
	 */
	public void returnResult()
	{
		RelativeLayout.LayoutParams back_button_layoutparams = (LayoutParams) main_back_button.getLayoutParams();
		back_button_layoutparams.addRule(RelativeLayout.ALIGN_PARENT_START);
		main_back_button.setLayoutParams(back_button_layoutparams);
		main_back_button.setVisibility(View.VISIBLE);

		float scale = search_button.getResources().getDisplayMetrics().density;
		RelativeLayout.LayoutParams search_button_layoutparams = new RelativeLayout.LayoutParams(0, (int) (30 * scale + 0.5f));
		search_button_layoutparams.addRule(RelativeLayout.END_OF, R.id.main_back_button);
		search_button_layoutparams.addRule(RelativeLayout.START_OF, R.id.menu_button);
		search_button_layoutparams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		search_button.setLayoutParams(search_button_layoutparams);
		resultPage = true;
	}

	@Override
	public void onBackPressed()
	{
		if (resultPage == true) back();
		else if (System.currentTimeMillis() - exitTime > 2000)
		{
			Toast.makeText(getApplicationContext(), R.string.exit_hint, Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		}
		else finish();
	}

	/**
	 * �жϲ�������Ļ��������
	 */
	private void setScreenBehavior()
	{
		SharedPreferences sharedPreferences = getSharedPreferences("preferences", 0);
		RelativeLayout contentLaout = (RelativeLayout) findViewById(R.id.content);
		if (sharedPreferences.getInt("keep_screen_on", 0) == 1) contentLaout.setKeepScreenOn(true);
		else contentLaout.setKeepScreenOn(false);
	}

	/**
	 * �����淵��Ĭ��״̬��ز��ָı����
	 */
	private void back()
	{
		keyword = "";
		plantMap.clear();
		mClusterManager.clearItems();
		search_button.setText(R.string.search_hint);
		plants = null;
		main_back_button.setVisibility(View.GONE);
		float scale = search_button.getResources().getDisplayMetrics().density;
		RelativeLayout.LayoutParams search_button_layoutparams = new RelativeLayout.LayoutParams(0, (int) (30 * scale + 0.5f));
		search_button_layoutparams.setMarginStart((int) (10 * scale + 0.5f));
		search_button_layoutparams.addRule(RelativeLayout.ALIGN_PARENT_START);
		search_button_layoutparams.addRule(RelativeLayout.START_OF, R.id.menu_button);
		search_button_layoutparams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		search_button.setLayoutParams(search_button_layoutparams);
		resultPage = false;
	}

	/**
	 * ��������ֲ����̷߳���
	 * 
	 * @param lat
	 *            γ��
	 * @param log
	 *            ����
	 */
	private void showNearbyplantThread(final Double lat, final Double log)
	{

		myHttpClientUtils = new HttpClientUtils();
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				plants = myHttpClientUtils.getNearbyPlant(lat, log);

				Bundle bundle = new Bundle();
				bundle.putParcelableArrayList("plants", plants);
				Message msg = new Message();
				msg.setData(bundle);
				msg.what = 1;
				myHandler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * �߳�Handler��ʼ��
	 */
	@SuppressLint("HandlerLeak")
	private void initHandler()
	{
		myHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				if (msg.what == 1)
				{
					plants = msg.getData().getParcelableArrayList("plants");

					if (plants != null)
					{

						addOverlay(plants);
					}
				}
				else if (msg.what == 2)
				{
					campus = msg.getData().getParcelableArrayList("campus");
					if (campus != null && campus.size() != 0)
					{
						positions = new String[campus.size()];
						for (int i = 0; i < campus.size(); i++)
							positions[i] = campus.get(i).getName();
						positionListAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.list_item, positions);
						positionListView.setAdapter(positionListAdapter);
					}
				}
			}
		};
	}

	/**
	 * ��ȡ����У��
	 */
	private void locationThread()
	{
		myHttpClientUtils = new HttpClientUtils();
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				campus = myHttpClientUtils.getTheCampus();
				Bundle bundle = new Bundle();
				bundle.putParcelableArrayList("campus", campus);
				Message msg = new Message();
				msg.setData(bundle);
				msg.what = 2;
				myHandler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * ��ͼ�ķ�������¼�
	 */
	private void initOritationListener()
	{
		myOrientationListener = new MyOrientationListener(getApplicationContext());
		myOrientationListener.setOnOrientationListener(new OnOrientationListener()
		{
			@Override
			public void onOrientationChanged(float x)
			{
				mXDirection = (int) x;
				// ���춨λ����
				MyLocationData locData = new MyLocationData.Builder().accuracy(mCurrentAccracy).direction(mXDirection).latitude(Latitude).longitude(Longitude).build();
				// ���ö�λ����
				plantMap.setMyLocationData(locData);
			}
		});
	}

	/**
	 * ��λ����ص�����������
	 */
	private class MyLocationListener implements BDLocationListener
	{
		@Override
		public void onReceiveLocation(BDLocation location)
		{
			// ����ȡ��location��Ϣ�� map view�����ٺ��ٴ����½��յ�λ��
			if (location == null || pMapView == null) return;
			// ���춨λ����
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(mXDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mCurrentAccracy = location.getRadius();
			// ���ö�λ����
			plantMap.setMyLocationData(locData);
			Latitude = location.getLatitude();
			Longitude = location.getLongitude();
			// �����Զ���ͼ��
			plantMap.setMyLocationData(locData);
			// ��һ�ζ�λʱ������ͼλ���ƶ�����ǰλ��
			BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.radar_button_style);
			MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
			plantMap.setMyLocationConfiguration(config);

			if (isFirstLocation)
			{
				city = location.getCity();
				isFirstLocation = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				plantMap.animateMapStatus(u);
			}
		}

		@Override
		public void onConnectHotSpotMessage(String arg0, int arg1)
		{
		}
	}

	/**
	 * ��������״̬�ı�ļ����¼��Լ��ؼ�����ʾ������
	 */
	public class ConnectionChangeReceiver extends BroadcastReceiver
	{
		@SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context context, Intent intent)
		{
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected())
			{
				no_connection_warning.setVisibility(View.VISIBLE);
				warning_icon.setVisibility(View.VISIBLE);
			}
			else
			{
				no_connection_warning.setVisibility(View.GONE);
				warning_icon.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * �ڵ�ͼ����ӱ����
	 * 
	 * @param plants
	 *            ֲ���������
	 */
	public void addOverlay(final ArrayList<Plant> plants)
	{
		// ��յ�ͼ
		plantMap.clear();
		mClusterManager.clearItems();
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				List<MyMarker> items = new ArrayList<MyMarker>();

				for (Plant plant : plants)
				{
					// ��ȡ��γ��
					int i = 0;

					for (; i < plant.getCoordinate().size(); i++)
					{
						LatLng latLng = new LatLng(plant.getCoordinate().get(i).getLat(), plant.getCoordinate().get(i).getLog());
						items.add(new MyMarker(latLng, plant));
						if (isfirst)
						{
							MapStatusUpdate us = MapStatusUpdateFactory.newLatLngZoom(latLng, 20);
							plantMap.animateMapStatus(us);
							isfirst = false;
						}
					}
				}
				mClusterManager.addItems(items);
			}
		}).start();
	}

	/**
	 * �α�����
	 *
	 */
	public class MyMarker implements ClusterItem
	{
		private final LatLng mPosition;
		private final Plant mPlant;

		public MyMarker(LatLng latLng, Plant mPlant)
		{
			mPosition = latLng;
			this.mPlant = mPlant;
		}

		public Plant getmPlant()
		{
			return mPlant;
		}

		@Override
		public LatLng getPosition()
		{
			return mPosition;
		}

		@Override
		public BitmapDescriptor getBitmapDescriptor()
		{
			View view = View.inflate(getApplicationContext(), R.layout.marker, null);
			TextView tView = (TextView) view.findViewById(R.id.icon_text);
			tView.setText(mPlant.getName() + "");
			return BitmapDescriptorFactory.fromView(view);
		}
	}

	/**
	 * ��ȡ�汾��
	 * 
	 * @return ��ǰӦ�õİ汾��
	 */
	public String getVersion()
	{
		try
		{
			PackageManager manager = getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e)
		{
			return "";
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart()
	{
		// ������λ
		plantMap.setMyLocationEnabled(true);
		// �����λclientû�п�����������λ
		if (!pLocationClient.isStarted()) pLocationClient.start();
		myOrientationListener.start();
		super.onStart();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		pMapView.onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		pMapView.onPause();
	}

	@Override
	protected void onDestroy()
	{
		pMapView.onDestroy();
		pMapView = null;
		campus = null;
		this.unregisterReceiver(myReceiver);
		mDetachableAreaMap.destroy();
		super.onDestroy();
	}

	@Override
	protected void onStop()
	{
		// �رն�λ
		plantMap.setMyLocationEnabled(false);
		if (pLocationClient.isStarted()) pLocationClient.stop();
		myOrientationListener.stop();
		super.onStop();
	}
}
