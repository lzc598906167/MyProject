package com.nwsuaf.plantcontrol;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * ���򴫸м�����
 * 
 * @author ���֡�������
 */
public class MyOrientationListener implements SensorEventListener {
	private Context context; // ������
	private SensorManager sensorManager; // ������������
	private Sensor sensor; // ������
	private float lastX; // ���򴫸������������꣬����ֻ��עX
	private OnOrientationListener onOrientationListener;

	public MyOrientationListener(Context context) {
		this.context = context;
	}

	// ��ʼ
	@SuppressWarnings("deprecation")
	public void start() {
		// ��ô�����������
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		// ��÷��򴫸���
		if (sensorManager != null)
			sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		// ע��
		if (sensor != null)
			sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
	}

	// ֹͣ���
	public void stop() {
		sensorManager.unregisterListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			// �������ǿ��Եõ����ݣ�Ȼ�������Ҫ������
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
