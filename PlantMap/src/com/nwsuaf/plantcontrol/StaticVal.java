package com.nwsuaf.plantcontrol;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import android.content.Context;

/**
 * ��̬����
 * 
 * @author ���֡�������
 */
public class StaticVal
{
	/** Ӧ���� */
	public static String app_name = "ֲ���״�";
	/** Ӧ�ð汾�� */
	public static String app_version = "1.0";
	/** IP��ַ */
	public static String ip = "172.17.64.154";
	/** �˿ں� */
	public static String port = "8080";
	/** Ĭ�ϳ��� */
	public static String defaultCity = "������";
	/** Ĭ��γ�� */
	public static double defaultLatitude = 0.0;
	/** Ĭ�Ͼ��� */
	public static double defaultLongitude = 0.0;
	/** ���ݿ���Ϣ */
	public static String databaseInfo = "INFO";

	/**
	 * ��ȡӦ�þ�̬����ֵ
	 * 
	 * @param context
	 *            ������
	 */
	public static void getProperties(Context context)
	{
		Properties properties = new Properties();
		try
		{
			InputStream inputStream = context.getAssets().open("config.properties");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			properties.load(bufferedReader);
			ip = properties.getProperty("IP");
			port = properties.getProperty("PORT");
			defaultCity = properties.getProperty("DEFAULT_CITY");
			defaultLatitude = Double.parseDouble(properties.getProperty("DEFAULT_LATITUDE"));
			defaultLongitude = Double.parseDouble(properties.getProperty("DEFAULT_LONGITUDE"));
			databaseInfo = properties.getProperty("DATABASE_INFO");
		} catch (FileNotFoundException e)
		{
			return;
		} catch (IOException e)
		{
			return;
		}
	}
}
