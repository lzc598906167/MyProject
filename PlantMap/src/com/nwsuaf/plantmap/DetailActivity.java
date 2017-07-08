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
 * 植物详情页面
 * 
 * @author 刘林、王俊杰
 */
@SuppressLint("HandlerLeak")
public class DetailActivity extends Activity {
	private Button back_button; // 返回按钮
	private RelativeLayout detailTitleBar; // 标题栏
	private int titleImageHeight; // 标题图片高度
	private ObservableScrollView scrollView; // 详情主界面ScrollView
	private ObservableScrollViewListener scrollViewListener; // 界面监听器
	private ImageView titleImageView; // 标题图片

	private Button nearbyButton; // 搜附近按钮
	private Button navigationButton; // 导航按钮

	private Plant plantInfo; // 植物信息对象
	private TextView detail_title; // 标题
	private TextView nameText; // 植物名称信息
	private TextView latNameText; // 植物拉丁名称信息
	private TextView aliaseNameText; // 植物别名信息
	private TextView familyGenusNameText; // 植物科属种信息
	private TextView distributionText; // 植物分布地点信息
	private TextView exterionText; // 植物外观信息
	private TextView flowerText; // 植物花信息
	private TextView branchText; // 植物枝条信息
	private TextView leafText; // 植物叶子信息
	private TextView fruitText; // 植物果实信息

	private List<PlantImage> imageList; // 植物图片列表
	private int numExterionPics = 0; // 植物外观图片数量
	private int numFlowerPics = 0; // 植物花图片数量
	private int numBranchPics = 0; // 植物枝条图片数量
	private int numLeafPics = 0; // 植物叶子图片数量
	private int numFruitPics = 0; // 植物果实图片数量
	private ArrayList<ArrayList<String>> imageURLList; // 图片地址列表
	private ImageDownloader imageDownloader; // 图片下载器

	private ImageOnClickListener imageOnClicListener; // 图片点击事件监听器

	private LinearLayout exterionImagesLayout; // 植物外观图片容器
	private LinearLayout flowerImagesLayout; // 植物花图片容器
	private LinearLayout branchImagesLayout; // 植物枝条图片容器
	private LinearLayout leafImagesLayout; // 植物叶子图片容器
	private LinearLayout fruitImagesLayout; // 植物果实图片容器

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_page);

		// 状态栏沉浸相关操作
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().getDecorView()
					.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			getWindow().setStatusBarColor(Color.TRANSPARENT);
		}
		detailTitleBar = (RelativeLayout) findViewById(R.id.detail_title_bar);

		// 返回按钮初始化
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

		// 标题栏初始化
		plantInfo = (Plant) bundle.getParcelable("plant");
		detail_title = (TextView) findViewById(R.id.detail_title);
		detail_title.setText(plantInfo.getName());
		detailTitleBar.setBackgroundColor(Color.argb(0x00, 0x32, 0xcd, 0x32));
		detail_title.setTextColor(Color.argb(0x00, 0xff, 0xff, 0xff));

		// 搜周边按钮初始化
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

		// 导航按钮初始化
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

		// 植物信息初始化
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

		// 植物图片容器初始化
		exterionImagesLayout = (LinearLayout) findViewById(R.id.exterion_pictures);
		flowerImagesLayout = (LinearLayout) findViewById(R.id.flower_pictures);
		branchImagesLayout = (LinearLayout) findViewById(R.id.branch_pictures);
		leafImagesLayout = (LinearLayout) findViewById(R.id.leaf_pictures);
		fruitImagesLayout = (LinearLayout) findViewById(R.id.fruit_pictures);

		// 初始化图片
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

		// 加载标题图片
		ShowTitleImage();

		// 详情主界面ScrollView初始化
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
		
		// 数据库信息初始化
		TextView databaseInfoText = (TextView) findViewById(R.id.database_info);
		databaseInfoText.setText(StaticVal.databaseInfo);

		// 加载视野中的其他图片
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
	 * 加载标题图片
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
	 * 模糊标题图片
	 * 
	 * @param titleBitMap
	 *            标题图片
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
	 * 加载图片
	 * 
	 * @param view
	 *            图片控件
	 * @param partIndex
	 *            植物部位索引号
	 * @param index
	 *            图片索引号
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
	 * 加载视野中的图片
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
	 * ScrollView滑动监听器
	 * 
	 * @author 刘林、王俊杰
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
	 * 图片点击事件监听器
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
