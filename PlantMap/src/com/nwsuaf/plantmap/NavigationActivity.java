package com.nwsuaf.plantmap;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRouteGuideManager.CustomizedLayerItem;
import com.baidu.navisdk.adapter.BNRouteGuideManager.OnNavigationListener;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviBaseCallbackModel;
import com.baidu.navisdk.adapter.BaiduNaviCommonModule;
import com.baidu.navisdk.adapter.NaviModuleFactory;
import com.baidu.navisdk.adapter.NaviModuleImpl;

/**
 * 导航界面Activity
 * 
 * @author 刘林、王俊杰
 */
public class NavigationActivity extends Activity {
	private BNRoutePlanNode mBNRoutePlanNode = null;
	private BaiduNaviCommonModule mBaiduNaviCommonModule = null;

	// 是否使用通用接口
	private boolean useCommonInterface = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MainActivity.activityList.add(this);
		createHandler();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		}
		View view = null;
		if (useCommonInterface) {
			// 使用通用接口
			mBaiduNaviCommonModule = NaviModuleFactory.getNaviModuleManager().getNaviCommonModule(
					NaviModuleImpl.BNaviCommonModuleConstants.ROUTE_GUIDE_MODULE, this,
					BNaviBaseCallbackModel.BNaviBaseCallbackConstants.CALLBACK_ROUTEGUIDE_TYPE, mOnNavigationListener);
			if (mBaiduNaviCommonModule != null) {
				mBaiduNaviCommonModule.onCreate();
				view = mBaiduNaviCommonModule.getView();
			}
		} else // 使用传统接口
			view = BNRouteGuideManager.getInstance().onCreate(this, mOnNavigationListener);

		if (view != null)
			setContentView(view);

		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null)
				mBNRoutePlanNode = (BNRoutePlanNode) bundle.getSerializable(MainActivity.ROUTE_PLAN_NODE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (useCommonInterface) {
			if (mBaiduNaviCommonModule != null)
				mBaiduNaviCommonModule.onResume();
		} else
			BNRouteGuideManager.getInstance().onResume();

		if (hd != null)
			hd.sendEmptyMessageAtTime(MSG_SHOW, 2000);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (useCommonInterface) {
			if (mBaiduNaviCommonModule != null)
				mBaiduNaviCommonModule.onPause();
		} else
			BNRouteGuideManager.getInstance().onPause();
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (useCommonInterface) {
			if (mBaiduNaviCommonModule != null)
				mBaiduNaviCommonModule.onDestroy();
		} else
			BNRouteGuideManager.getInstance().onDestroy();
		MainActivity.activityList.remove(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (useCommonInterface) {
			if (mBaiduNaviCommonModule != null)
				mBaiduNaviCommonModule.onStop();
		} else
			BNRouteGuideManager.getInstance().onStop();
	}

	@Override
	public void onBackPressed() {
		if (useCommonInterface) {
			if (mBaiduNaviCommonModule != null)
				mBaiduNaviCommonModule.onBackPressed(false);
		} else
			BNRouteGuideManager.getInstance().onBackPressed(false);
	}

	@Override
	public void onConfigurationChanged(android.content.res.Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (useCommonInterface) {
			if (mBaiduNaviCommonModule != null)
				mBaiduNaviCommonModule.onConfigurationChanged(newConfig);
		} else
			BNRouteGuideManager.getInstance().onConfigurationChanged(newConfig);
	};

	@SuppressWarnings("deprecation")
	private void addCustomizedLayerItems() {
		List<CustomizedLayerItem> items = new ArrayList<CustomizedLayerItem>();
		CustomizedLayerItem item1 = null;
		if (mBNRoutePlanNode != null) {
			item1 = new CustomizedLayerItem(mBNRoutePlanNode.getLongitude(), mBNRoutePlanNode.getLatitude(),
					mBNRoutePlanNode.getCoordinateType(), getResources().getDrawable(R.drawable.ic_launcher),
					CustomizedLayerItem.ALIGN_CENTER);
			items.add(item1);

			BNRouteGuideManager.getInstance().setCustomizedLayerItems(items);
		}
		BNRouteGuideManager.getInstance().showCustomizedLayer(true);
	}

	private static final int MSG_SHOW = 1;
	private static final int MSG_HIDE = 2;
	private Handler hd = null;

	private void createHandler() {
		if (hd == null) {
			hd = new Handler(getMainLooper()) {
				@Override
				public void handleMessage(android.os.Message msg) {
					if (msg.what == MSG_SHOW)
						addCustomizedLayerItems();
					else if (msg.what == MSG_HIDE)
						BNRouteGuideManager.getInstance().showCustomizedLayer(false);
				};
			};
		}
	}

	private OnNavigationListener mOnNavigationListener = new OnNavigationListener() {
		@Override
		public void onNaviGuideEnd() {
			finish();
		}

		@Override
		public void notifyOtherAction(int actionType, int arg1, int arg2, Object obj) {
		}
	};
}
