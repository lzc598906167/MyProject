package com.nwsuaf.plantmap;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nwsuaf.plantcontrol.BlurBitmapUtil;
import com.nwsuaf.plantcontrol.Coordinate;
import com.nwsuaf.plantcontrol.ImageDownloader;
import com.nwsuaf.plantcontrol.ObservableScrollView;
import com.nwsuaf.plantcontrol.ImageDownloader.onImageLoaderListener;
import com.nwsuaf.plantcontrol.ObservableScrollView.ScrollViewListener;
import com.nwsuaf.plantcontrol.Plant;
import com.nwsuaf.plantcontrol.PlantImage;
import com.nwsuaf.plantcontrol.StaticVal;

/**
 * ֲ������ҳ��
 * 
 * @author ���֡�������
 */
@SuppressLint("HandlerLeak")
public class DetailActivity extends Activity {
	private Button back_button; // ���ذ�ť
	private RelativeLayout detailTitleBar; // ������
	private int titleImageHeight; // ����ͼƬ�߶�
	private ObservableScrollView scrollView; // ����������ScrollView
	private ObservableScrollViewListener scrollViewListener; // ���������
	private ImageView titleImageView; // ����ͼƬ

	private Button nearbyButton; // �Ѹ�����ť
	private Button navigationButton; // ������ť

	private Plant plantInfo; // ֲ����Ϣ����
	private TextView detail_title; // ����
	private TextView nameText; // ֲ��������Ϣ
	private TextView latNameText; // ֲ������������Ϣ
	private TextView aliaseNameText; // ֲ�������Ϣ
	private TextView familyGenusNameText; // ֲ���������Ϣ
	private TextView distributionText; // ֲ��ֲ��ص���Ϣ
	private TextView exterionText; // ֲ�������Ϣ
	private TextView flowerText; // ֲ�ﻨ��Ϣ
	private TextView branchText; // ֲ��֦����Ϣ
	private TextView leafText; // ֲ��Ҷ����Ϣ
	private TextView fruitText; // ֲ���ʵ��Ϣ

	private List<PlantImage> imageList; // ֲ��ͼƬ�б�
	private int numExterionPics = 0; // ֲ�����ͼƬ����
	private int numFlowerPics = 0; // ֲ�ﻨͼƬ����
	private int numBranchPics = 0; // ֲ��֦��ͼƬ����
	private int numLeafPics = 0; // ֲ��Ҷ��ͼƬ����
	private int numFruitPics = 0; // ֲ���ʵͼƬ����
	private ArrayList<ArrayList<String>> imageURLList; // ͼƬ��ַ�б�
	private ImageDownloader imageDownloader; // ͼƬ������

	private ImageOnClickListener imageOnClicListener; // ͼƬ����¼�������

	private LinearLayout exterionImagesLayout; // ֲ�����ͼƬ����
	private LinearLayout flowerImagesLayout; // ֲ�ﻨͼƬ����
	private LinearLayout branchImagesLayout; // ֲ��֦��ͼƬ����
	private LinearLayout leafImagesLayout; // ֲ��Ҷ��ͼƬ����
	private LinearLayout fruitImagesLayout; // ֲ���ʵͼƬ����

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_page);

		// ״̬��������ز���
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().getDecorView()
					.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			getWindow().setStatusBarColor(Color.TRANSPARENT);
		}
		detailTitleBar = (RelativeLayout) findViewById(R.id.detail_title_bar);

		// ���ذ�ť��ʼ��
		back_button = (Button) findViewById(R.id.detail_back_button);
		back_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		String spaceString = "        ";
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("bundle");

		// ��������ʼ��
		plantInfo = (Plant) bundle.getParcelable("plant");
		detail_title = (TextView) findViewById(R.id.detail_title);
		detail_title.setText(plantInfo.getName());
		detailTitleBar.setBackgroundColor(Color.argb(0x00, 0x32, 0xcd, 0x32));
		detail_title.setTextColor(Color.argb(0x00, 0xff, 0xff, 0xff));

		// ���ܱ߰�ť��ʼ��
		nearbyButton = (Button) findViewById(R.id.nearby_button);
		nearbyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				Coordinate coordinate = plantInfo.getCoordinate().get(0);
				bundle.putParcelable("coordinate", coordinate);
				intent.putExtras(bundle);
				setResult(9, intent);
				finish();
			}
		});

		// ������ť��ʼ��
		navigationButton = (Button) findViewById(R.id.navigation_btn);
		navigationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				Coordinate coordinate = plantInfo.getCoordinate().get(0);
				bundle.putParcelable("coordinate", coordinate);
				intent.putExtras(bundle);
				setResult(10, intent);
				finish();
			}
		});

		// ֲ����Ϣ��ʼ��
		nameText = (TextView) findViewById(R.id.plant_name);
		nameText.setText(plantInfo.getName());
		latNameText = (TextView) findViewById(R.id.plant_latname);
		latNameText.setText(plantInfo.getLatName());
		aliaseNameText = (TextView) findViewById(R.id.plant_aliaseName);
		aliaseNameText.setText(plantInfo.getAliaseName());
		familyGenusNameText = (TextView) findViewById(R.id.plant_familyGenusName);
		familyGenusNameText.setText(plantInfo.getFamilyName() + " " + plantInfo.getGenusName());
		distributionText = (TextView) findViewById(R.id.plant_distribution);
		distributionText.setText(plantInfo.getDistribution());
		exterionText = (TextView) findViewById(R.id.plant_exterion);
		exterionText.setText(spaceString + plantInfo.getExterion());
		flowerText = (TextView) findViewById(R.id.plant_flower);
		flowerText.setText(spaceString + plantInfo.getFlower());
		branchText = (TextView) findViewById(R.id.plant_branch);
		branchText.setText(spaceString + plantInfo.getBranch());
		leafText = (TextView) findViewById(R.id.plant_leaf);
		leafText.setText(spaceString + plantInfo.getLeaf());
		fruitText = (TextView) findViewById(R.id.plant_fruit);
		fruitText.setText(spaceString + plantInfo.getFruit());

		// ֲ��ͼƬ������ʼ��
		exterionImagesLayout = (LinearLayout) findViewById(R.id.exterion_pictures);
		flowerImagesLayout = (LinearLayout) findViewById(R.id.flower_pictures);
		branchImagesLayout = (LinearLayout) findViewById(R.id.branch_pictures);
		leafImagesLayout = (LinearLayout) findViewById(R.id.leaf_pictures);
		fruitImagesLayout = (LinearLayout) findViewById(R.id.fruit_pictures);

		// ��ʼ��ͼƬ
		imageList = plantInfo.getPlantImages();
		numExterionPics = 0;
		numFlowerPics = 0;
		numBranchPics = 0;
		numLeafPics = 0;
		numFruitPics = 0;
		imageDownloader = new ImageDownloader();
		imageURLList = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < 5; i++)
			imageURLList.add(new ArrayList<String>());
		imageOnClicListener = new ImageOnClickListener();
		for (int i = 0; i < imageList.size(); i++) {
			PlantImage plantImage = imageList.get(i);
			float scale = getResources().getDisplayMetrics().density;
			int height = (int) (200 * scale + 0.5f);
			LinearLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, height);
			int margin = (int) (10 * scale + 0.5f);
			layoutParams.setMargins(margin * 2, margin, margin * 2, margin);
			if (plantImage.getType().equals("exterior")) {
				ImageView imageView = new ImageView(this);
				imageView.setId(110 + numExterionPics);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageView.setBackgroundColor(Color.rgb(0xdc, 0xdc, 0xdc));
				imageView.setOnClickListener(imageOnClicListener);
				exterionImagesLayout.addView(imageView, layoutParams);
				imageURLList.get(0).add(plantImage.getPlantUrl());
				numExterionPics++;
			} else if (plantImage.getType().equals("flower")) {
				ImageView imageView = new ImageView(this);
				imageView.setId(120 + numFlowerPics);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageView.setBackgroundColor(Color.rgb(0xdc, 0xdc, 0xdc));
				imageView.setOnClickListener(imageOnClicListener);
				flowerImagesLayout.addView(imageView, layoutParams);
				imageURLList.get(1).add(plantImage.getPlantUrl());
				numFlowerPics++;
			} else if (plantImage.getType().equals("branch")) {
				ImageView imageView = new ImageView(this);
				imageView.setId(130 + numBranchPics);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageView.setBackgroundColor(Color.rgb(0xdc, 0xdc, 0xdc));
				imageView.setOnClickListener(imageOnClicListener);
				branchImagesLayout.addView(imageView, layoutParams);
				imageURLList.get(2).add(plantImage.getPlantUrl());
				numBranchPics++;
			} else if (plantImage.getType().equals("leaf")) {
				ImageView imageView = new ImageView(this);
				imageView.setId(140 + numLeafPics);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageView.setBackgroundColor(Color.rgb(0xdc, 0xdc, 0xdc));
				imageView.setOnClickListener(imageOnClicListener);
				leafImagesLayout.addView(imageView, layoutParams);
				imageURLList.get(3).add(plantImage.getPlantUrl());
				numLeafPics++;
			} else if (plantImage.getType().equals("fruit")) {
				ImageView imageView = new ImageView(this);
				imageView.setId(150 + numFruitPics);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageView.setBackgroundColor(Color.rgb(0xdc, 0xdc, 0xdc));
				imageView.setOnClickListener(imageOnClicListener);
				fruitImagesLayout.addView(imageView, layoutParams);
				imageURLList.get(4).add(plantImage.getPlantUrl());
				numFruitPics++;
			}
		}

		// ���ر���ͼƬ
		ShowTitleImage();

		// ����������ScrollView��ʼ��
		scrollView = (ObservableScrollView) findViewById(R.id.scrollView1);
		scrollViewListener = new ObservableScrollViewListener();
		titleImageView = (ImageView) findViewById(R.id.title_image);
		ViewTreeObserver observer = titleImageView.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				titleImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				titleImageHeight = titleImageView.getHeight();
				scrollView.setScrollViewListener(scrollViewListener);
			}
		});
		
		// ���ݿ���Ϣ��ʼ��
		TextView databaseInfoText = (TextView) findViewById(R.id.database_info);
		databaseInfoText.setText(StaticVal.databaseInfo);

		// ������Ұ�е�����ͼƬ
		final Handler loadImageHandle = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				LoadVisibleImages();
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(100);
					Message msg = loadImageHandle.obtainMessage();
					loadImageHandle.sendMessage(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * ���ر���ͼƬ
	 */
	private void ShowTitleImage() {
		Bitmap image = null;
		onImageLoaderListener listener = new onImageLoaderListener() {
			@Override
			public void onImageLoader(Bitmap bitmap, String url) {
				blueTitleImage(bitmap);
			}
		};
		if (imageURLList.get(0).size() == 0)
			return;
		String url = "http://" + StaticVal.ip + ":" + StaticVal.port + imageURLList.get(0).get(0);
		image = imageDownloader.downloadImage(url, listener);
		if (image != null)
			blueTitleImage(image);
	}

	/**
	 * ģ������ͼƬ
	 * 
	 * @param titleBitMap
	 *            ����ͼƬ
	 */
	private void blueTitleImage(Bitmap titleBitMap) {
		if (titleBitMap == null)
			return;
		titleBitMap = titleBitMap.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(titleBitMap);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		ColorMatrix brightnessMatrix = new ColorMatrix();
		brightnessMatrix.setScale((float) 0.7, (float) 0.7, (float) 0.7, (float) 1);
		paint.setColorFilter(new ColorMatrixColorFilter(brightnessMatrix));
		canvas.drawBitmap(titleBitMap, 0, 0, paint);
		Bitmap bluredTitleBitMap = BlurBitmapUtil.blurBitmap(DetailActivity.this, titleBitMap, 5f);
		titleImageView.setImageDrawable(new BitmapDrawable(null, bluredTitleBitMap));
		titleImageView.setScaleType(ScaleType.CENTER_CROP);
		nameText.setTextColor(Color.rgb(0xff, 0xff, 0xff));
		latNameText.setTextColor(Color.rgb(0xff, 0xff, 0xff));
		familyGenusNameText.setTextColor(Color.rgb(0xff, 0xff, 0xff));
		nearbyButton.setTextColor(Color.rgb(0xff, 0xff, 0xff));
		navigationButton.setTextColor(Color.rgb(0xff, 0xff, 0xff));
	}

	/**
	 * ����ͼƬ
	 * 
	 * @param view
	 *            ͼƬ�ؼ�
	 * @param partIndex
	 *            ֲ�ﲿλ������
	 * @param index
	 *            ͼƬ������
	 */
	private void ShowImage(final ImageView view, final int partIndex, final int index) {
		Bitmap image = null;
		onImageLoaderListener listener = new onImageLoaderListener() {
			@Override
			public void onImageLoader(Bitmap bitmap, String url) {
				view.setImageBitmap(bitmap);
			}
		};
		String url = "http://" + StaticVal.ip + ":" + StaticVal.port + imageURLList.get(partIndex).get(index);
		image = imageDownloader.downloadImage(url, listener);
		if (image != null)
			view.setImageBitmap(image);
	}

	/**
	 * ������Ұ�е�ͼƬ
	 */
	private void LoadVisibleImages() {
		Rect scrollBounds = new Rect();
		scrollView.getHitRect(scrollBounds);
		scrollBounds.top -= 500;
		if (scrollBounds.top < 0)
			scrollBounds.top = 0;
		scrollBounds.bottom += 500;
		if (exterionImagesLayout.getLocalVisibleRect(scrollBounds)) {
			for (int i = 0; i < numExterionPics; i++) {
				final ImageView imageView = (ImageView) findViewById(110 + i);
				if (imageView.getLocalVisibleRect(scrollBounds))
					ShowImage(imageView, 0, i);
				else
					imageView.setBackgroundColor(Color.rgb(0xdc, 0xdc, 0xdc));
			}
		}
		if (flowerImagesLayout.getLocalVisibleRect(scrollBounds)) {
			for (int i = 0; i < numFlowerPics; i++) {
				ImageView imageView = (ImageView) findViewById(120 + i);
				if (imageView.getLocalVisibleRect(scrollBounds))
					ShowImage(imageView, 1, i);
				else
					imageView.setBackgroundColor(Color.rgb(0xdc, 0xdc, 0xdc));
			}
		}
		if (branchImagesLayout.getLocalVisibleRect(scrollBounds)) {
			for (int i = 0; i < numBranchPics; i++) {
				ImageView imageView = (ImageView) findViewById(130 + i);
				if (imageView.getLocalVisibleRect(scrollBounds))
					ShowImage(imageView, 2, i);
				else
					imageView.setBackgroundColor(Color.rgb(0xdc, 0xdc, 0xdc));
			}
		}
		if (leafImagesLayout.getLocalVisibleRect(scrollBounds)) {
			for (int i = 0; i < numLeafPics; i++) {
				ImageView imageView = (ImageView) findViewById(140 + i);
				if (imageView.getLocalVisibleRect(scrollBounds))
					ShowImage(imageView, 3, i);
				else
					imageView.setBackgroundColor(Color.rgb(0xdc, 0xdc, 0xdc));
			}
		}
		if (fruitImagesLayout.getLocalVisibleRect(scrollBounds)) {
			for (int i = 0; i < numFruitPics; i++) {
				ImageView imageView = (ImageView) findViewById(150 + i);
				if (imageView.getLocalVisibleRect(scrollBounds))
					ShowImage(imageView, 4, i);
				else
					imageView.setBackgroundColor(Color.rgb(0xdc, 0xdc, 0xdc));
			}
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

	/**
	 * ScrollView����������
	 * 
	 * @author ���֡�������
	 */
	private class ObservableScrollViewListener implements ScrollViewListener {
		@Override
		public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
			if (y <= titleImageHeight) {
				float scale = (float) y / titleImageHeight;
				float alpha = (255 * scale);
				if (alpha < 0)
					alpha = 0;
				if (alpha > 255)
					alpha = 255;
				detailTitleBar.setBackgroundColor(Color.argb((int) alpha, 0x32, 0xcd, 0x32));
				detail_title.setTextColor(Color.argb((int) alpha, 0xff, 0xff, 0xff));
			} else {
				detailTitleBar.setBackgroundColor(Color.argb(0xff, 0x32, 0xcd, 0x32));
				detail_title.setTextColor(Color.argb(0xff, 0xff, 0xff, 0xff));
			}
		}

		@Override
		public void onScrollFinished() {
			LoadVisibleImages();
		}
	}

	/**
	 * ͼƬ����¼�������
	 */
	private class ImageOnClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent();
			intent.setClass(DetailActivity.this, FullScreenActivity.class);
			String[] imagesURL;
			int id = arg0.getId();
			if (id >= 150) {
				imagesURL = new String[numFruitPics];
				for (int i = 0; i < numFruitPics; i++)
					imagesURL[i] = imageURLList.get(4).get(i);
				intent.putExtra("list_length", numFruitPics);
				intent.putExtra("url_list", imagesURL);
				intent.putExtra("index", id - 150);
			} else if (id >= 140) {
				imagesURL = new String[numLeafPics];
				for (int i = 0; i < numLeafPics; i++)
					imagesURL[i] = imageURLList.get(3).get(i);
				intent.putExtra("list_length", numLeafPics);
				intent.putExtra("url_list", imagesURL);
				intent.putExtra("index", id - 140);
			} else if (id >= 130) {
				imagesURL = new String[numBranchPics];
				for (int i = 0; i < numBranchPics; i++)
					imagesURL[i] = imageURLList.get(2).get(i);
				intent.putExtra("list_length", numBranchPics);
				intent.putExtra("url_list", imagesURL);
				intent.putExtra("index", id - 130);
			} else if (id >= 120) {
				imagesURL = new String[numFlowerPics];
				for (int i = 0; i < numFlowerPics; i++)
					imagesURL[i] = imageURLList.get(1).get(i);
				intent.putExtra("list_length", numFlowerPics);
				intent.putExtra("url_list", imagesURL);
				intent.putExtra("index", id - 120);
			} else if (id >= 110) {
				imagesURL = new String[numExterionPics];
				for (int i = 0; i < numExterionPics; i++)
					imagesURL[i] = imageURLList.get(0).get(i);
				intent.putExtra("list_length", numExterionPics);
				intent.putExtra("url_list", imagesURL);
				intent.putExtra("index", id - 110);
			}
			startActivity(intent);
		}
	}
}
