package com.nwsuaf.plantcontrol;

import java.util.ArrayList;

import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.nwsuaf.plantmap.R;

/**
 * ���Markerͼ�㹤����
 * 
 * @author ���֡�������
 */
public class MyAddMakerOverlay {
	private Marker marker;

	private LatLng latLng = null;
	private OverlayOptions options;
	private BaiduMap addBaiduMap;

	public MyAddMakerOverlay(BaiduMap plantMap) {
		addBaiduMap = plantMap;
	}

	/**
	 * ��ȡmarker
	 * 
	 * @return Marker
	 */
	public Marker getMarker() {
		return marker;
	}

	/**
	 * ���ͼ��
	 * 
	 * @param plants
	 *            Ŀ��ֲ������
	 */
	public void addOverlay(ArrayList<Plant> plants) {
		// ��յ�ͼ
		addBaiduMap.clear();
		// ����marker����ʾͼ��
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.item_icon);
		for (Plant plant : plants) {
			// ��ȡ��γ��
			for (int i = 0; i < plant.getCoordinate().size(); i++) {
				latLng = new LatLng(plant.getCoordinate().get(i).getLat(), plant.getCoordinate().get(i).getLog());
				// ����marker
				options = new MarkerOptions().position(latLng)// ����λ��
						.icon(bitmap)// ����ͼ����ʽ
						.zIndex(9) // ����marker���ڲ㼶
						.draggable(false); // ����������ק;
				// ���marker
				marker = (Marker) addBaiduMap.addOverlay(options);

				// ʹ��markerЯ��plant��Ϣ��������¼���ʱ�����ͨ��marker���plant��Ϣ
				Bundle bundle = new Bundle(); // plant����ʵ�����л��ӿ�
				bundle.putParcelable("plant", plant);
				marker.setExtraInfo(bundle);
			}
		}
	}
}
