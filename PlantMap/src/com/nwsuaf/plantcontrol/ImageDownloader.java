package com.nwsuaf.plantcontrol;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;

/**
 * 植物图片下载器
 * 
 * @author 刘林、王俊杰
 */
public class ImageDownloader {
	private LruCache<String, Bitmap> mMemoryCache;
	private ExecutorService mImageThreadPool = null;

	public ImageDownloader() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int mCacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(mCacheSize) {
			// 必须重写此方法，来测量Bitmap的大小
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
	}

	private ExecutorService getThreadPool() {
		if (mImageThreadPool == null) {
			synchronized (ExecutorService.class) {
				if (mImageThreadPool == null)
					mImageThreadPool = Executors.newFixedThreadPool(3);
			}
		}
		return mImageThreadPool;
	}

	/**
	 * 将图片存入缓存
	 * 
	 * @param key
	 *            存储关键字
	 * @param bitmap
	 *            图片对象
	 */
	private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null && bitmap != null)
			mMemoryCache.put(key, bitmap);
	}

	/**
	 * 从缓存中取得图片
	 * 
	 * @param key
	 *            图片关键字
	 * @return 图片
	 */
	private Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	/**
	 * 下载图片
	 * 
	 * @param url
	 *            URL地址
	 * @param listener
	 *            下载完成监听器
	 * @return 图片
	 */
	public Bitmap downloadImage(final String url, final onImageLoaderListener listener) {
		final String subUrl = url.replaceAll("[^\\w]", "");
		Bitmap bitmap = getBitmapFromMemCache(subUrl);
		if (bitmap != null)
			return bitmap;
		else {
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					listener.onImageLoader((Bitmap) msg.obj, url);
				}
			};
			getThreadPool().execute(new Runnable() {
				@Override
				public void run() {
					Bitmap bitmap = getBitmapFromUrl(url);
					Message msg = handler.obtainMessage();
					msg.obj = bitmap;
					handler.sendMessage(msg);

					// 将Bitmap加入内存缓存
					addBitmapToMemoryCache(subUrl, bitmap);
				}
			});
		}
		return null;
	}

	/**
	 * 从图片URL地址获取图片
	 * 
	 * @param path
	 *            地址
	 * @return 图片
	 */
	private Bitmap getBitmapFromUrl(String path) {
		URL url;
		try {
			url = new URL(path);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == 200) {
				InputStream inputStream = conn.getInputStream();
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				return bitmap;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 取消所有任务
	 */
	public synchronized void cancelTask() {
		if (mImageThreadPool != null) {
			mImageThreadPool.shutdownNow();
			mImageThreadPool = null;
		}
	}

	public interface onImageLoaderListener {
		void onImageLoader(Bitmap bitmap, String url);
	}
}
