package com.nwsuaf.plantcontrol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * �����ƷŴ���С��ͼƬ�ؼ�
 * 
 * @author ���֡�������
 */
public class ZoomImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener,
		ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {
	private boolean mOnce = false; // ��һ��ִ��
	private float mInitScale; // ��ʼ�����ŵ�ֵ����Сʱ����Сֵ��
	private float mMidScale; // ˫���Ŵ�ʱ�������ֵ
	private float mMaxScale; // �Ŵ�����ֵ
	private Matrix mScaleMatrix; // ���ŵ�Matrix
	private ScaleGestureDetector mScaleGestureDetector; // ��ָ��������
	/* �����ƶ� */
	private int mLastPointerCount; // �õ��ϴζ�㴥�ص���������Ҫ�ڴ����ֻ����������仯ʱ������Ӧ�Ĵ�����ֹͼƬ��ʽ��Ծʽ�ĸı�
	private int mTouchSlop; // ϵͳĬ�ϱ�ʾ�����ƶ��¼���ֵ
	private boolean isCanDrag = false; // �Ƿ���ƶ�
	private float mLastX = 0; // �ϴδ�����X����
	private float mLastY = 0; // �ϴδ�����Y����
	private boolean isCheckLeftAndRight; // ���ˮƽ����
	private boolean isCheckTopAndBottom; // ��ⴹֱ����
	/* ˫���Ŵ���С */
	private GestureDetector mGestureDetector; // ˫�������¼������ƶ���
	private boolean isAutoScale; // �Ƿ���������

	public ZoomImageView(Context context) {
		this(context, null);
	}

	public ZoomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mScaleMatrix = new Matrix();
		setScaleType(ScaleType.MATRIX);
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		setOnTouchListener(this); // ���ô����¼�����
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop(); // �õ�ϵͳ�����ƶ��¼���ֵ
		mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onDoubleTap(MotionEvent e) { // ˫���¼�
				if (isAutoScale)
					return true;
				float x = e.getX();
				float y = e.getY(); // ˫�������ĵ�
				if (getScale() < mMidScale && getScale() >= mInitScale) { // ��Ŵ�mMidScale
					postDelayed(new AutoScaleRunnable(mMidScale + 0.01f, x, y), 16);
				} else if (getScale() < mMaxScale && getScale() > mMidScale) { // ��Ŵ�mMaxScale
					postDelayed(new AutoScaleRunnable(mMaxScale, x, y), 16);
				} else if (getScale() == mMaxScale) { // ��С��mMidScale
					postDelayed(new AutoScaleRunnable(mMidScale, x, y), 16);
				} else if (getScale() == mMidScale) // ����С��mMinScale
					postDelayed(new AutoScaleRunnable(mInitScale, x, y), 16);
				isAutoScale = true;

				return true;
			}
		});
	}

	/** ���л��������� */
	public class AutoScaleRunnable implements Runnable {
		private float x; // ���ĵ�λ��
		private float y;
		private float mTagScale; // ��������ֵ
		private final float BIGING = 1.07f; // ���ڷŴ�
		private final float SMILE = 0.93f; // ������С
		private float mScale; // ��ǰ����ֵ

		public AutoScaleRunnable(float mTagScale, float x, float y) {
			this.mTagScale = mTagScale;
			this.x = x;
			this.y = y;
			if (getScale() < mTagScale)
				mScale = BIGING;
			else
				mScale = SMILE;
		}

		@Override
		public void run() {
			mScaleMatrix.postScale(mScale, mScale, x, y);
			checkBorderAndCenterWhenScale();
			setImageMatrix(mScaleMatrix);
			if ((mScale == BIGING && getScale() < mTagScale) || (mScale == SMILE && getScale() > mTagScale)) {
				// �ٴν�������
				postDelayed(this, 16);
			} else { // ����Ŀ�������ֵ
				mScaleMatrix.postScale(mTagScale / getScale(), mTagScale / getScale(), x, y);
				checkBorderAndCenterWhenScale();
				setImageMatrix(mScaleMatrix);
				isAutoScale = false; // ���Ž���
			}
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getViewTreeObserver().removeOnGlobalLayoutListener(this);
	}

	@Override
	public void onGlobalLayout() {
		if (!mOnce) {
			// �õ��ؼ��Ŀ�͸�
			int width = getWidth();
			int height = getHeight();
			// �õ�ͼƬ�Ŀ�͸�
			Drawable d = getDrawable();
			if (d == null) {
				return;
			}
			int dw = d.getIntrinsicWidth();
			int dh = d.getIntrinsicHeight();
			float scale = 1.0f; // ����ֵ
			if (dw > width && dh < height) // ͼƬ��ȴ��ڿؼ���ȣ�ͼƬ�߶�С�ڿؼ��߶�
				scale = width * 1.0f / dw; // �Կ������Ϊ��
			if (dh > height && dw < width) // ͼƬ���С�ڿؼ���ȣ�ͼƬ�߶ȴ��ڿؼ��߶�
				scale = height * 1.0f / dh; // �Ը߶�����Ϊ��
			if (dw > width && dh > height) // ͼƬ��߶����ڿؼ��Ŀ��
				scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);// ȥ��Сֵ
			if (dw < width && dh < height) // ͼƬ��߶�С�ڿؼ���ߣ�ͼƬӦ�÷Ŵ�
				scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
			// �õ���ʼ��ʱ���ű���
			mInitScale = scale;
			mMaxScale = mInitScale * 4;
			mMidScale = mInitScale * 2;
			// ��ͼƬ�ƶ����ؼ������ĵ�
			float dx = getWidth() * 1.0f / 2 - dw * 1.0f / 2;
			float dy = getHeight() * 1.0f / 2 - dh * 1.0f / 2;
			mScaleMatrix.postTranslate(dx, dy);
			mScaleMatrix.postScale(mInitScale, mInitScale, width / 2, height / 2);
			setImageMatrix(mScaleMatrix);
			mOnce = true;
		}
	}

	/**
	 * �õ���ǰ������ֵ
	 */
	public float getScale() {
		float vaules[] = new float[9];
		mScaleMatrix.getValues(vaules);
		return vaules[Matrix.MSCALE_X]; // X,Y��������������һ��
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) { // ���Ž�����
		if (getDrawable() == null)
			return true;
		float scaleFactor = detector.getScaleFactor(); // �õ���ǰ��ָ������Ҫ���ŵ�ֵ
		float scale = getScale();
		if (scaleFactor > 1.0f) { // ��ǰΪ���ڷŴ�
			if (scaleFactor * scale >= mMaxScale) // �Ŵ�󳬹����Ŵ���
				scaleFactor = mMaxScale / scale;
		} else { // ��ǰΪ������С
			if (scaleFactor * scale < mInitScale) // ��СʱС�������С������ֵ
				scaleFactor = mInitScale / scale;
		}
		// ����
		mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
		checkBorderAndCenterWhenScale();

		setImageMatrix(mScaleMatrix);

		return true; // ������Ҫ����true����֤�¼��ܹ�����ִ��
	}

	/**
	 * �õ�ͼƬ�Ŵ��Ժ�Ŀ�͸�
	 */
	private RectF getMatrixRectf() {
		Matrix matrix = mScaleMatrix;
		RectF rectF = new RectF();
		Drawable d = getDrawable();
		if (d != null) {
			rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.mapRect(rectF); // �õ��Ŵ���С�Ժ��ͼƬ��͸�
		}
		return rectF;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) { // ���ſ�ʼ
		return true; // ������Ҫ����true����֤�¼��ܹ�����ִ��
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) { // ���Ž���
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event))
			return true;

		mScaleGestureDetector.onTouchEvent(event);
		float x = 0;
		float y = 0;
		int pointerCount = event.getPointerCount(); // �õ���㴥������
		for (int i = 0; i < pointerCount; i++) {
			x += event.getX(i);
			y += event.getY(i);
		}
		x /= pointerCount; // �������ĵ�X����
		y /= pointerCount; // �������ĵ�Y����

		if (mLastPointerCount != pointerCount) { // ���������ı�ʱ
			mLastX = x;
			mLastY = y;
			isCanDrag = false;
		}
		mLastPointerCount = pointerCount;
		RectF rectF = getMatrixRectf(); // ���ͼƬ���ź�Ŀ��
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (rectF.height() > getHeight() + 0.01f || rectF.width() > getWidth() + 0.01f)
				// ֪ͨ�������¼��������أ��ɵ�ǰ�ؼ������¼�����
				getParent().requestDisallowInterceptTouchEvent(true);
			break;

		case MotionEvent.ACTION_MOVE:
			if (rectF.height() > getHeight() + 0.01f || rectF.width() > getWidth() + 0.01f)
				// ֪ͨ�������¼��������أ��ɵ�ǰ�ؼ������¼�����
				getParent().requestDisallowInterceptTouchEvent(true);
			float dx = x - mLastX;
			float dy = y - mLastY;
			if (!isCanDrag)
				isCanDrag = isMoveAction(dx, dy);
			if (isCanDrag) {
				isCheckLeftAndRight = isCheckTopAndBottom = true; // �ƶ�ʱ��Ĭ�϶��������
				if (getDrawable() != null) {
					// �����ǰ���С�ڿؼ��Ŀ�ȣ�������ˮƽ�ƶ�
					if (rectF.width() <= getWidth()) {
						isCheckLeftAndRight = false;
						dx = 0;
					}
					// �����ǰ�߶�С�ڿؼ��ĸ߶ȣ������������ƶ�
					if (rectF.height() <= getHeight()) {
						// Ϊ�˵��߶�С�ڿؼ��߶�ʱ����ʱ�Ŀհײ����ǰױߣ�������ֱ߽���
						isCheckTopAndBottom = false;
						dy = 0;
					}
					mScaleMatrix.postTranslate(dx, dy);
					checkBorderWhenTranslate();
					setImageMatrix(mScaleMatrix);
				}
			}
			mLastX = x;
			mLastY = y;
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mLastPointerCount = 0;
			break;
		}

		return true;
	}

	/**
	 * �ж��Ƿ����Դ���move�¼�
	 */
	private boolean isMoveAction(float dx, float dy) {
		return Math.sqrt(dx * dx + dy * dy) >= mTouchSlop ? true : false;
	}

	/**
	 * �����ŵ�ʱ����б߽���ƣ������У���ֹ���ŵ�ʱ����ְױ�
	 */
	private void checkBorderAndCenterWhenScale() {
		RectF rectF = getMatrixRectf();
		float dx = 0; // ˮƽ������Ҫ������ƫ����
		float dy = 0; // ��ֱ������Ҫ������ƫ����
		int width = getWidth(); // �ؼ����
		int height = getHeight(); // �ؼ��߶�
		if (rectF.width() > width) { // �Ŵ�ͼƬ���ڿؼ����
			if (rectF.left > 0) // �ұ�ƫ�ƣ���Ҫ�����ƶ�
				dx = -rectF.left;
			if (rectF.right < width) // ���ƫ�ƣ���Ҫ�����ƶ�
				dx = width - rectF.right;
		}
		if (rectF.height() > height) {// �Ŵ�ͼƬ���ڿظ߶�
			if (rectF.top > 0) // ����ƫ�룬��Ҫ�����ƶ�
				dy = -rectF.top;
			if (rectF.bottom < height) // ����ƫ�ƣ���Ҫ�����ƶ�
				dy = height - rectF.bottom;
		}
		if (rectF.width() < width || rectF.height() < height) { // ������С�ڿؼ��Ŀ��ʱ����Ҫ������ʾ
			dx = width * 1.0f / 2 - rectF.width() / 2 - rectF.left;
			dy = height * 1.0f / 2 - rectF.height() / 2 - rectF.top;
		}
		mScaleMatrix.postTranslate(dx, dy);
	}

	/**
	 * ���ƶ�ʱ�����߽�
	 */
	private void checkBorderWhenTranslate() {
		RectF rectF = getMatrixRectf();
		float dx = 0;
		float dy = 0;
		float width = getWidth();
		float height = getHeight();
		if (rectF.top > 0 && isCheckTopAndBottom)
			dy = -rectF.top;
		if (rectF.bottom < height && isCheckTopAndBottom)
			dy = height - rectF.bottom;
		if (rectF.left > 0 && isCheckLeftAndRight)
			dx = -rectF.left;
		if (rectF.right < width && isCheckLeftAndRight)
			dx = width - rectF.right;
		mScaleMatrix.postTranslate(dx, dy);
	}
}
