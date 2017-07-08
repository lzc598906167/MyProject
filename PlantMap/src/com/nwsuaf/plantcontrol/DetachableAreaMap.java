package com.nwsuaf.plantcontrol;

import java.util.ArrayList;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;

/**
 * 离线地图管理
 * 
 * @author 刘林、王俊杰
 */
public class DetachableAreaMap implements MKOfflineMapListener {
	private MKOfflineMap mOffline = null;

	/** 已下载的离线地图信息列表 */
	public ArrayList<MKOLUpdateElement> localMapList = null;

	public DetachableAreaMap() {
		mOffline = new MKOfflineMap();
		mOffline.init(this);
	}

	/**
	 * 判断是否存在当前城市的离线地图
	 * 
	 * @param cityName
	 *            城市名
	 * @return 存在标识
	 */
	public boolean isExist(String cityName) {
		localMapList = mOffline.getAllUpdateInfo();
		if (localMapList == null)
			return false;
		for (MKOLUpdateElement element1 : localMapList) {
			if (!element1.cityName.equals(cityName) || element1.ratio != 100) {
				remove();
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取城市ID
	 * 
	 * @param cityName
	 *            城市名
	 * @return 城市ID
	 */
	public int getCityIdbyName(String cityName) {
		ArrayList<MKOLSearchRecord> records = mOffline.searchCity(cityName);
		if (records == null || records.size() != 1)
			return -1;
		// 显示查找出来的城市id
		return records.get(0).cityID;
	}

	/**
	 * 获取城市离线地图大小
	 * 
	 * @param cityName
	 *            城市名
	 * @return 城市离线地图大小
	 */
	public int getCitySize(String cityName) {
		ArrayList<MKOLSearchRecord> records = mOffline.searchCity(cityName);
		if (records == null || records.size() != 1)
			return -1;
		// 显示查找出来的城市id
		return records.get(0).size;
	}

	/**
	 * 删除离线地图
	 * 
	 * @return 成功标识
	 */
	public boolean remove() {
		localMapList = mOffline.getAllUpdateInfo();
		if (localMapList != null) {
			for (int i = 0; i < localMapList.size(); i++) {
				MKOLUpdateElement element = localMapList.get(i);
				if (!mOffline.remove(element.cityID))
					return false;
			}
		}
		return true;
	}

	/**
	 * 开始下载离线地图
	 * 
	 * @param cityid
	 *            城市ID
	 */
	public void start(final int cityid) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mOffline.start(cityid);
			}
		}).start();
	}

	@Override
	public void onGetOfflineMapState(int arg0, int arg1) {
	}

	/**
	 * 销毁离线地图对象
	 */
	public void destroy() {
		mOffline.destroy();
	}
}
