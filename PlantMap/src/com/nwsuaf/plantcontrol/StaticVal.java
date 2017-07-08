package com.nwsuaf.plantcontrol;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import android.content.Context;

/**
 * 静态变量
 * 
 * @author 刘林、王俊杰
 */
public class StaticVal
{
	/** 应用名 */
	public static String app_name = "植物雷达";
	/** 应用版本号 */
	public static String app_version = "1.0";
	/** IP地址 */
	public static String ip = "172.17.64.154";
	/** 端口号 */
	public static String port = "8080";
	/** 默认城市 */
	public static String defaultCity = "咸阳市";
	/** 默认纬度 */
	public static double defaultLatitude = 0.0;
	/** 默认经度 */
	public static double defaultLongitude = 0.0;
	/** 数据库信息 */
	public static String databaseInfo = "INFO";

	/**
	 * 获取应用静态变量值
	 * 
	 * @param context
	 *            上下文
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
