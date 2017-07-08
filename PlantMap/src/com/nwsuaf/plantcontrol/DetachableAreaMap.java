package com.nwsuaf.plantcontrol;

import java.util.ArrayList;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;

/**
 * ���ߵ�ͼ����
 * 
 * @author ���֡�������
 */
public class DetachableAreaMap implements MKOfflineMapListener {
	private MKOfflineMap mOffline = null;

	/** �����ص����ߵ�ͼ��Ϣ�б� */
	public ArrayList<MKOLUpdateElement> localMapList = null;

	public DetachableAreaMap() {
		mOffline = new MKOfflineMap();
		mOffline.init(this);
	}

	/**
	 * �ж��Ƿ���ڵ�ǰ���е����ߵ�ͼ
	 * 
	 * @param cityName
	 *            ������
	 * @return ���ڱ�ʶ
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
	 * ��ȡ����ID
	 * 
	 * @param cityName
	 *            ������
	 * @return ����ID
	 */
	public int getCityIdbyName(String cityName) {
		ArrayList<MKOLSearchRecord> records = mOffline.searchCity(cityName);
		if (records == null || records.size() != 1)
			return -1;
		// ��ʾ���ҳ����ĳ���id
		return records.get(0).cityID;
	}

	/**
	 * ��ȡ�������ߵ�ͼ��С
	 * 
	 * @param cityName
	 *            ������
	 * @return �������ߵ�ͼ��С
	 */
	public int getCitySize(String cityName) {
		ArrayList<MKOLSearchRecord> records = mOffline.searchCity(cityName);
		if (records == null || records.size() != 1)
			return -1;
		// ��ʾ���ҳ����ĳ���id
		return records.get(0).size;
	}

	/**
	 * ɾ�����ߵ�ͼ
	 * 
	 * @return �ɹ���ʶ
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
	 * ��ʼ�������ߵ�ͼ
	 * 
	 * @param cityid
	 *            ����ID
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
	 * �������ߵ�ͼ����
	 */
	public void destroy() {
		mOffline.destroy();
	}
}
