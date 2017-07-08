package com.nwsuaf.plantcontrol;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * ģ��ͼƬ����
 * 
 * @author ���֡�������
 */
public class BlurBitmapUtil {
	// ͼƬ���ű���
	private static final float BITMAP_SCALE = 0.4f;

	/**
	 * ģ��ͼƬ
	 * 
	 * @param context
	 *            ������
	 * @param image
	 *            ͼƬ����
	 * @param blurRadius
	 *            ģ���뾶
	 * @return Bitmap
	 */
	public static Bitmap blurBitmap(Context context, Bitmap image, float blurRadius) {
		// ����ͼƬ��С��ĳ���
		int width = Math.round(image.getWidth() * BITMAP_SCALE);
		int height = Math.round(image.getHeight() * BITMAP_SCALE);

		// ����С���ͼƬ��ΪԤ��Ⱦ��ͼƬ
		Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
		// ����һ����Ⱦ������ͼƬ
		Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

		// ����RenderScript�ں˶���
		RenderScript rs = RenderScript.create(context);
		// ����һ��ģ��Ч����RenderScript�Ĺ��߶���
		ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

		// ����RenderScript��û��ʹ��VM�������ڴ�,������Ҫʹ��Allocation���������ͷ����ڴ�ռ�
		// ����Allocation�����ʱ����ʵ�ڴ��ǿյ�,��Ҫʹ��copyTo()����������ȥ
		Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
		Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

		// ������Ⱦ��ģ���̶�, 25f�����ģ����
		blurScript.setRadius(blurRadius);
		// ����blurScript����������ڴ�
		blurScript.setInput(tmpIn);
		// ��������ݱ��浽����ڴ���
		blurScript.forEach(tmpOut);

		// ��������䵽Allocation��
		tmpOut.copyTo(outputBitmap);

		return outputBitmap;
	}
}
