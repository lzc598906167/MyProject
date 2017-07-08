package com.nwsuaf.plantmap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.nwsuaf.plantcontrol.ImageDownloader;
import com.nwsuaf.plantcontrol.ImageDownloader.onImageLoaderListener;
import com.nwsuaf.plantcontrol.StaticVal;
import com.nwsuaf.plantcontrol.ZoomImageView;

/**
 * È«ÆÁÍ¼Æ¬ä¯ÀÀActivity
 * 
 * @author Íõ¿¡½Ü¡¢ÁõÁÖ
 */
public class FullScreenActivity extends Activity {
	private ViewPager viewPager;
	private ImageView loadingImage;
	private Animation animation;
	private String[] URLList;
	private ImageView[] imageViews;
	private int[] imagesStatus;
	private ImageDownloader imageDownloader; // Í¼Æ¬ÏÂÔØÆ÷

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fullscreen);

		Intent intent = getIntent();
		int listLength = intent.getIntExtra("list_length", 0);
		imageViews = new ImageView[listLength];
		URLList = intent.getStringArrayExtra("url_list");
		imagesStatus = new int[listLength];
		imageDownloader = new ImageDownloader();

		loadingImage = (ImageView) findViewById(R.id.loading_image);
		animation = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
		loadingImage.startAnimation(animation);

		viewPager = (ViewPager) findViewById(R.id.fullscreen_image);
		viewPager.setAdapter(new PagerAdapter() {
			@Override
			public int getCount() {
				return imageViews.length;
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				ZoomImageView imageView = new ZoomImageView(FullScreenActivity.this);
				ShowImage(imageView, position);
				imageViews[position] = imageView;
				container.addView(imageView);
				return imageView;
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				container.removeView(imageViews[position]);
			}
		});
		viewPager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				if (imagesStatus[arg0] == 1) {
					loadingImage.clearAnimation();
					loadingImage.setVisibility(View.GONE);
				} else {
					loadingImage.startAnimation(animation);
					loadingImage.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		viewPager.setCurrentItem(intent.getIntExtra("index", 1));
	}

	/**
	 * ÏÔÊ¾Í¼Æ¬
	 * 
	 * @param view
	 *            Í¼Æ¬¿Ø¼þ
	 * @param index
	 *            Í¼Æ¬Ë÷ÒýºÅ
	 */
	private void ShowImage(final ImageView view, final int index) {
		Bitmap image = null;
		onImageLoaderListener listener = new onImageLoaderListener() {
			@Override
			public void onImageLoader(Bitmap bitmap, String url) {
				view.setImageBitmap(bitmap);
				loadingImage.clearAnimation();
				loadingImage.setVisibility(View.GONE);
				imagesStatus[index] = 1;
			}
		};
		String url = "http://" + StaticVal.ip + ":" + StaticVal.port + URLList[index];
		image = imageDownloader.downloadImage(url, listener);
		if (image != null) {
			view.setImageBitmap(image);
			loadingImage.clearAnimation();
			loadingImage.setVisibility(View.GONE);
			imagesStatus[index] = 1;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}
