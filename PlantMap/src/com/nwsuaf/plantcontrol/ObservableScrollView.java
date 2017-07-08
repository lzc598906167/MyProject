package com.nwsuaf.plantcontrol;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 带滚动监听的ScrollView
 * 
 * @author 刘林、王俊杰
 */
public class ObservableScrollView extends ScrollView {
	private int lastY; // Y坐标

	/**
	 * ScrollView监听器
	 */
	public interface ScrollViewListener {
		/**
		 * 滚动事件监听
		 * 
		 * @param scrollView
		 *            ScrollView对象
		 * @param x
		 *            x坐标
		 * @param y
		 *            y坐标
		 * @param oldx
		 *            旧的x坐标
		 * @param oldy
		 *            旧的y坐标
		 */
		void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);

		/**
		 * 滚动停止事件监听
		 */
		void onScrollFinished();
	}

	private ScrollViewListener scrollViewListener = null;

	public ObservableScrollView(Context context) {
		super(context);
	}

	public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ObservableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScrollViewListener(ScrollViewListener scrollViewListener) {
		this.scrollViewListener = scrollViewListener;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);
		if (scrollViewListener != null) {
			scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
			lastY = y;
			checkStateHandler.removeMessages(0);
			checkStateHandler.sendEmptyMessageDelayed(0, 100);
		}
	}

	private Handler checkStateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (lastY == getScrollY()) {
				//  如果上次的位置和当前的位置相同，可认为是在空闲状态
				scrollViewListener.onScrollFinished();
			}
		}
	};
}
