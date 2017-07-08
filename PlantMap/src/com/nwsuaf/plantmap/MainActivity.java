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
 * 主界面Activity
 * 
 * @author 王俊杰、刘林
 */
public class MainActivity extends Activity
{
	private Button menu_button; // 菜单按钮，点击进入设置页面
	private Button search_button; // 搜索框按钮，点击进入搜索界面
	private Button main_back_button; // 返回按钮，在显示结果状态下出现，点击返回默认状态
	private Button qr_button; // 二维码扫描按键
	private TextView no_connection_warning; // 无网络连接提示框
	private Button warning_icon; // 感叹号
	private PopupWindow positionMenu; // 切换地点弹出窗口对象
	private ListView positionListView; // 地点列表
	private Button position_button; // 切换地点按钮，点击弹出地点选择列表
	private Button locate_button; // 定位按钮
	private Button narrow_button, enlarge_button, radar_button;// 放大，缩小，定位雷达按钮
	private String keyword; // 搜索关键字
	private boolean resultPage; // 用于标记当前状态是否为显示结果状态
	private long exitTime; // 两次返回键退出程序相关变量，用于计时
	private ConnectionChangeReceiver myReceiver;

	protected MapView pMapView = null; // 百度地图控件

	protected BaiduMap plantMap; // 百度地图对象植物地图

	private DetachableAreaMap mDetachableAreaMap; // 离线地图管理
	private String city = StaticVal.defaultCity; // 默认离线地图需要加载的城市
	private LocationClient pLocationClient; // 定位
	private MyLocationListener pLocationListener; // 定位监听器
	private boolean isFirstLocation = true; // 是否第一次定位，如果是第一次定位的话要将自己的位置显示在地图中间
	private float mCurrentAccracy; // 定位精度
	private double Latitude = 34.292033; // 纬度
	private double Longitude = 108.078618; // 经度
	private LatLng centre = new LatLng(Latitude, Longitude);
	private LatLng endLatLng; // 导航的终点
	private com.baidu.mapapi.map.MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
	private MyOrientationListener myOrientationListener; // 重新定义的方向传感器
	private int mXDirection; // 方向
	/* 获取数据相关 */
	private HttpClientUtils myHttpClientUtils;
	private Boolean isfirst = false;
	private Handler myHandler;
	private ArrayList<Campus> campus = null; // 校区对象数组
	private ArrayList<Plant> plants = null; // 植物对象数组
	private String[] positions = null; // 校区名字数组
	private ArrayAdapter<String> positionListAdapter = null; // 校区列表适配器
	/* 导航相关参数 */
	public static final String TAG = "NaviSDkDemo";
	private static final String APP_FOLDER_NAME = "BNNAVI";
	public static final String ROUTE_PLAN_NODE = "routePlanNode";
	private String mSDCardPath = null;
	String authinfo = null;
	public static List<Activity> activityList = new LinkedList<Activity>();
	private final static String authBaseArr[] = { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION };
	private final static int authBaseRequestCode = 1;

	private ClusterManager<MyMarker> mClusterManager; // 聚合点管理器，地图监听器

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		activityList.add(this);
		initPermission();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SDKInitializer.initialize(getApplicationContext());
		registerReceiver(); // 网络变化监听
		setContentView(R.layout.activity_main);
		initView(); // 初始化控件，添加控件点击事件
		initPlantMap(); // 地图初始化
		initOritationListener(); // 方向初始化
		initLocation();/* 定位 */
		initDetachableAreaMap(); // 加载当前城市的离线地图
		initHandler(); // 初始化线程
		// 初始化导航
		if (initDirs()) initNavi();
		// 获取应用名
		StaticVal.app_name = getString(R.string.app_name);
		// 获取应用版本号
		StaticVal.app_version = getVersion();
	}

	/**
	 * 注册网络监听器
	 */
	private void registerReceiver()
	{
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		myReceiver = new ConnectionChangeReceiver();
		this.registerReceiver(myReceiver, filter);
	}

	/**
	 * 运行时权限处理
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
	 * 初始化地图方法
	 */
	private void initPlantMap()
	{
		pMapView = (MapView) findViewById(R.id.plantmapview);
		// pMapView.removeViewAt(1); // 去掉百度LOGO
		pMapView.showScaleControl(false); // 去掉比例尺
		pMapView.showZoomControls(false); // 去掉缩放控件
		plantMap = pMapView.getMap(); // 得到地图
		plantMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); // 设置普通地图
		/* 改变地图状态，使地图显示在恰当的缩放大小同时设置起始位置 */
		MapStatus pMapStatus = new MapStatus.Builder().target(centre).zoom(19).build();
		MapStatusUpdate pMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(pMapStatus);
		plantMap.setMapStatus(pMapStatusUpdate);
		mClusterManager = new ClusterManager<MyMarker>(this, plantMap);
		mClusterManager.setNarrow_button(narrow_button);
		plantMap.setOnMapStatusChangeListener(mClusterManager);
		// 设置maker点击时的响应
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
	 * 定位初始化
	 */
	private void initLocation()
	{
		/* 定位客户端的设置 */
		pLocationClient = new LocationClient(this);
		pLocationListener = new MyLocationListener();
		/* 注册监听 */
		pLocationClient.registerLocationListener(pLocationListener);
		/* 配置定位 */
		LocationClientOption locationoption = new LocationClientOption();
		locationoption.setCoorType("bd09ll"); // 坐标类型
		locationoption.setOpenGps(true); // 打开GPS
		locationoption.setScanSpan(1000); // 1000毫秒定位一次
		locationoption.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		locationoption.setIsNeedAddress(true); // 返回的定位结果包含地址信息
		locationoption.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向
		pLocationClient.setLocOption(locationoption); // 把配置的信息传给定位客户端，不然所有按默认设置
	}

	/**
	 * 在WIFI网络的情况下，自动下载离线地图
	 */
	private void initDetachableAreaMap()
	{
		mDetachableAreaMap = new DetachableAreaMap();
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取管理器的活动网络信息对象
		NetworkInfo activeInfo = conMgr.getActiveNetworkInfo();
		if (activeInfo != null && activeInfo.isConnected())
		{
			// 查看网络连接类型
			boolean wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
			// 判断是否为WIFI网络
			if (wifiConnected)
			{
				if (!mDetachableAreaMap.isExist(city))
				{
					int cityID = mDetachableAreaMap.getCityIdbyName(city);
					if (cityID != -1) mDetachableAreaMap.start(cityID); // 开始离线下载地图
				}
			}
		}
	}

	/**
	 * 界面上的控件的监听事件初始化
	 */
	private void initView()
	{
		resultPage = false;
		keyword = "";
		exitTime = 0;

		// 放大按钮
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

		// 缩小按钮
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

		// 菜单按钮初始化
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

		// 搜索框按钮初始化
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

		// 二维码扫描按键初始化
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

		// 搜索附近的按钮
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

		// 返回按钮初始化
		main_back_button = (Button) findViewById(R.id.main_back_button);
		main_back_button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				back();
			}
		});

		// 当前位置按钮（定位）
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

		// 切换地点按钮初始化
		position_button = (Button) findViewById(R.id.location_button);
		position_button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				initPositionMenu(arg0.getRootView());
			}
		});
		// 无网络连接提示框初始化
		no_connection_warning = (TextView) findViewById(R.id.no_connection_warning);
		warning_icon = (Button) findViewById(R.id.warning_icon);
		setScreenBehavior(); // 设置屏幕常亮
	}

	/**
	 * 切换地点窗口初始化
	 * 
	 * @param view
	 *            父View
	 */
	private void initPositionMenu(View view)
	{
		if (positionMenu != null && positionMenu.isShowing()) return;

		// 初始化窗口
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

		// 获取数据
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
		// 设置点击窗口外结束活动的监听器
		popupLayout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				positionMenu.dismiss();
			}
		});
		// 设置列表点击标题栏不要退出的监听器
		TextView titleView = (TextView) popupLayout.findViewById(R.id.popup_title);
		titleView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// Do NOTHING
			}
		});
		// 响应点击列表项的监听器
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
	 * 移动到定位的位置上
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
			// 返回值为1：搜索成功并获取搜索关键字，主界面进入显示结果状态
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
			// 返回值为2：搜索植物名称失败，主界面返回默认状态
			keyword = data.getStringExtra("keyword");
			Toast.makeText(getApplicationContext(), getString(R.string.name_not_found) + "“" + keyword + "”", Toast.LENGTH_SHORT).show();
			back();
		}
		else if (resultCode == 3)
		{
			// 返回值为3：搜索植物分布地点失败，主界面返回默认状态
			keyword = data.getStringExtra("keyword");
			Toast.makeText(getApplicationContext(), getString(R.string.place_not_found) + "“" + keyword + "”", Toast.LENGTH_SHORT).show();
			back();
		}
		else if (resultCode == 5)
		{
			// 返回值为5：获取并应用用户偏好设置参数
			setScreenBehavior();
		}
		else if (resultCode == 6)
		{
			// 返回值为6： 获取二维码信息
			Bundle bundle = data.getExtras();
			String url = bundle.getString("result");
			search_button.setText(url);
		}
		else if (resultCode == 7)
		{
			// 返回值为7： 服务器端未响应
			Toast.makeText(getApplicationContext(), R.string.service_fail, Toast.LENGTH_SHORT).show();
			back();
		}
		else if (requestCode == 8)
		{
			// 返回值为7： 获取地点数据失败
			Toast.makeText(getApplicationContext(), R.string.unkonwn_error, Toast.LENGTH_SHORT).show();
			back();
		}
		else if (resultCode == 9)
		{
			// 返回值为9： 搜索附近
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
			// 返回值为10： 导航
			Bundle bundle = data.getExtras();
			Coordinate coordinate = bundle.getParcelable("coordinate");
			endLatLng = new LatLng(coordinate.getLat(), coordinate.getLog());
			center2myLoc();
			if (initDirs()) initNavi();
			BNRoutePlanNode sNode = new BNRoutePlanNode(centre.longitude, centre.latitude, "起点", null, CoordinateType.BD09LL);
			BNRoutePlanNode eNode = new BNRoutePlanNode(endLatLng.longitude, endLatLng.latitude, "终点", null, CoordinateType.BD09LL);
			if (sNode != null && eNode != null)
			{
				List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
				list.add(sNode);
				list.add(eNode);
				if (BaiduNaviManager.isNaviInited())
				{
					BaiduNaviManager.getInstance().launchNavigator(this, // 建议是应用的主Activity
							list, // 传入的算路节点，顺序是起点、途经点、终点，其中途经点最多三个
							1, // 算路偏好 1:推荐 8:少收费 2:高速优先 4:少走高速 16:躲避拥堵
							true, // true表示真实GPS导航，false表示模拟导航
							new DemoRoutePlanListener(sNode)// 开始导航回调监听器，在该监听器里一般是进入导航过程页面
							);
				}
			}
		}
	}

	/**
	 * 导航监听事件之一
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
			// 导航activity
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
	 * @Fields ttsHandler : 导航的handle参数
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
	 * @Fields ttsPlayStateListener : 导航的语音播报机制
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
	 * 导航的存储状态
	 * 
	 * @return 导航的存储状态
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
	 * 导航的权限获取
	 * 
	 * @return 导航权限字符串
	 */
	private String getSdcardDir()
	{
		if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) return Environment.getExternalStorageDirectory().toString();
		return null;
	}

	/**
	 * 导航的权限判断
	 * 
	 * @return 有无权限
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
	 * 初始化导航引擎和引擎的参数
	 */
	@SuppressLint("NewApi")
	private void initNavi()
	{
		// 申请权限
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
				if (0 == status) authinfo = "key校验成功!";
				else authinfo = "key校验失败, " + msg;
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
	 * 导航的参数设置
	 */
	private void initSetting()
	{
		BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
		BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
		// BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
		BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
		BNaviSettingManager.setIsAutoQuitWhenArrived(true);
		Bundle bundle = new Bundle();
		// 必须设置APPID，否则会静音
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
	 * 主界面的结果状态界面
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
	 * 判断并设置屏幕常亮功能
	 */
	private void setScreenBehavior()
	{
		SharedPreferences sharedPreferences = getSharedPreferences("preferences", 0);
		RelativeLayout contentLaout = (RelativeLayout) findViewById(R.id.content);
		if (sharedPreferences.getInt("keep_screen_on", 0) == 1) contentLaout.setKeepScreenOn(true);
		else contentLaout.setKeepScreenOn(false);
	}

	/**
	 * 主界面返回默认状态相关布局改变操作
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
	 * 搜索附近植物的线程方法
	 * 
	 * @param lat
	 *            纬度
	 * @param log
	 *            经度
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
	 * 线程Handler初始化
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
	 * 获取所有校区
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
	 * 地图的方向监听事件
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
				// 构造定位数据
				MyLocationData locData = new MyLocationData.Builder().accuracy(mCurrentAccracy).direction(mXDirection).latitude(Latitude).longitude(Longitude).build();
				// 设置定位数据
				plantMap.setMyLocationData(locData);
			}
		});
	}

	/**
	 * 定位请求回调函数监听器
	 */
	private class MyLocationListener implements BDLocationListener
	{
		@Override
		public void onReceiveLocation(BDLocation location)
		{
			// 将获取的location信息给 map view，销毁后不再处理新接收的位置
			if (location == null || pMapView == null) return;
			// 构造定位数据
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(mXDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mCurrentAccracy = location.getRadius();
			// 设置定位数据
			plantMap.setMyLocationData(locData);
			Latitude = location.getLatitude();
			Longitude = location.getLongitude();
			// 设置自定义图标
			plantMap.setMyLocationData(locData);
			// 第一次定位时，将地图位置移动到当前位置
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
	 * 网络连接状态改变的监听事件以及控件的显示和隐藏
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
	 * 在地图上添加标记物
	 * 
	 * @param plants
	 *            植物对象数组
	 */
	public void addOverlay(final ArrayList<Plant> plants)
	{
		// 清空地图
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
					// 获取经纬度
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
	 * 游标标记物
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
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
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
		// 开启定位
		plantMap.setMyLocationEnabled(true);
		// 如果定位client没有开启，开启定位
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
		// 关闭定位
		plantMap.setMyLocationEnabled(false);
		if (pLocationClient.isStarted()) pLocationClient.stop();
		myOrientationListener.stop();
		super.onStop();
	}
}
