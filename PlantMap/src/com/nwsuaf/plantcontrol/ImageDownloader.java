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
 * ֲ��ͼƬ������
 * 
 * @author ���֡�������
 */
public class ImageDownloader {
	private LruCache<String, Bitmap> mMemoryCache;
	private ExecutorService mImageThreadPool = null;

	public ImageDownloader() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int mCacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(mCacheSize) {
			// ������д�˷�����������Bitmap�Ĵ�С
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
	 * ��ͼƬ���뻺��
	 * 
	 * @param key
	 *            �洢�ؼ���
	 * @param bitmap
	 *            ͼƬ����
	 */
	private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null && bitmap != null)
			mMemoryCache.put(key, bitmap);
	}

	/**
	 * �ӻ�����ȡ��ͼƬ
	 * 
	 * @param key
	 *            ͼƬ�ؼ���
	 * @return ͼƬ
	 */
	private Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	/**
	 * ����ͼƬ
	 * 
	 * @param url
	 *            URL��ַ
	 * @param listener
	 *            ������ɼ�����
	 * @return ͼƬ
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

					// ��Bitmap�����ڴ滺��
					addBitmapToMemoryCache(subUrl, bitmap);
				}
			});
		}
		return null;
	}

	/**
	 * ��ͼƬURL��ַ��ȡͼƬ
	 * 
	 * @param path
	 *            ��ַ
	 * @return ͼƬ
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
	 * ȡ����������
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
