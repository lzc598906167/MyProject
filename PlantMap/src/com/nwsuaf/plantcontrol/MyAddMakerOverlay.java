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
 * 添加Marker图层工具类
 * 
 * @author 刘林、王俊杰
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
	 * 获取marker
	 * 
	 * @return Marker
	 */
	public Marker getMarker() {
		return marker;
	}

	/**
	 * 添加图层
	 * 
	 * @param plants
	 *            目标植物数组
	 */
	public void addOverlay(ArrayList<Plant> plants) {
		// 清空地图
		addBaiduMap.clear();
		// 创建marker的显示图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.item_icon);
		for (Plant plant : plants) {
			// 获取经纬度
			for (int i = 0; i < plant.getCoordinate().size(); i++) {
				latLng = new LatLng(plant.getCoordinate().get(i).getLat(), plant.getCoordinate().get(i).getLog());
				// 设置marker
				options = new MarkerOptions().position(latLng)// 设置位置
						.icon(bitmap)// 设置图标样式
						.zIndex(9) // 设置marker所在层级
						.draggable(false); // 设置手势拖拽;
				// 添加marker
				marker = (Marker) addBaiduMap.addOverlay(options);

				// 使用marker携带plant信息，当点击事件的时候可以通过marker获得plant信息
				Bundle bundle = new Bundle(); // plant必须实现序列化接口
				bundle.putParcelable("plant", plant);
				marker.setExtraInfo(bundle);
			}
		}
	}
}
