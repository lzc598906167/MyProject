package com.nwsuaf.plantcontrol;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 方向传感监听器
 * 
 * @author 刘林、王俊杰
 */
public class MyOrientationListener implements SensorEventListener {
	private Context context; // 上下文
	private SensorManager sensorManager; // 传感器管理者
	private Sensor sensor; // 传感器
	private float lastX; // 方向传感器有三个坐标，现在只关注X
	private OnOrientationListener onOrientationListener;

	public MyOrientationListener(Context context) {
		this.context = context;
	}

	// 开始
	@SuppressWarnings("deprecation")
	public void start() {
		// 获得传感器管理器
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		// 获得方向传感器
		if (sensorManager != null)
			sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		// 注册
		if (sensor != null)
			sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
	}

	// 停止检测
	public void stop() {
		sensorManager.unregisterListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			// 这里我们可以得到数据，然后根据需要来处理
			float x = event.values[SensorManager.DATA_X];
			if (Math.abs(x - lastX) > 1.0) {
				onOrientationListener.onOrientationChanged(x);
			}
			lastX = x;
		}
	}

	public void setOnOrientationListener(OnOrientationListener onOrientationListener) {
		this.onOrientationListener = onOrientationListener;
	}

	public interface OnOrientationListener {
		void onOrientationChanged(float x);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}
