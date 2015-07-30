package net.oilchem.communication.sms;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.bugly.crashreport.CrashReport;

import net.oilchem.communication.sms.data.model.DataConfig;
import net.oilchem.communication.sms.data.model.DataConfig.OilConfig;
import net.oilchem.communication.sms.data.model.DataConfig.OilSMSCategory;
import net.oilchem.communication.sms.data.model.DataLogin;
import net.oilchem.communication.sms.util.CrashHandler;
import net.oilchem.communication.sms.util.DatabaseUtil;
import net.oilchem.communication.sms.util.JsonUtil;
import net.oilchem.communication.sms.util.SharedPreferenceUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.UUID;

public class OilchemApplication extends Application {
	private static Context mContext = null;

	public static int SCREEN_WIDTH = Constant.INVALID_INT;
	public static int SCREEN_HEIGHT = Constant.INVALID_INT;
	public static float DENSITY = 1.0f;
    public static  File COLLECTION_PATH;
	private static DataConfig config;
	private static DataLogin user;
	private static String versionCode, versionName;
	private static String deviceId;

	public static String getDeviceId() {
		return deviceId;
	}

	private String getDeviceId(Context context) {
		String deviceId = null;
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (manager != null) {
			deviceId = manager.getDeviceId();
		}
		if (TextUtils.isEmpty(deviceId)) {
			deviceId = Secure.getString(context.getContentResolver(),
					Secure.ANDROID_ID);
		}
		if (TextUtils.isEmpty(deviceId)) {
			deviceId = deviceId(getContextFromApplication());
		}
		return deviceId.replaceAll(",", "");
	}

	private synchronized String deviceId(Context context) {
		File installation = new File(context.getFilesDir(), "INSTALLATION");
		String sID = null;
		try {
			if (!installation.exists()) {
				writeInstallationFile(installation);
			}
			sID = readInstallationFile(installation);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sID;
	}

	private String readInstallationFile(File installation) throws IOException {
		RandomAccessFile f = new RandomAccessFile(installation, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes, "UTF-8");
	}

	private void writeInstallationFile(File installation) throws IOException {
		FileOutputStream out = new FileOutputStream(installation);
		String id = UUID.randomUUID().toString();
		out.write(id.getBytes("UTF-8"));
		out.close();
	}

	public static DataLogin getUser() {
		return user;
	}

	public static boolean isLogined() {
		if (user != null && !TextUtils.isEmpty(user.getAccessToken())) {
			return true;
		}
		return false;
	}

	public static void setUser(DataLogin user) {
		OilchemApplication.user = user;
		SharedPreferenceUtil.setString(Constant.SHAREDREFERENCES_CONFIG,
				Constant.SHAREDREFERENCES_CONFIG_USER,
				JsonUtil.toJson(user, DataLogin.class));
	}

	public static DataConfig getConfig() {
		if (null == config) {
			config = new DataConfig(new OilConfig("", "", "", "10", "", ""));
		}
		return config;
	}

	public static void setConfig(DataConfig config) {
		OilchemApplication.config = config;
		SharedPreferenceUtil.setString(Constant.SHAREDREFERENCES_CONFIG,
				Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION,
				JsonUtil.toJson(config, DataConfig.class));
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		initConfigAndUser();
		initImageLoader(mContext);
		initApplicationMessage();
		COLLECTION_PATH=getFilesDir();
//		initDatabaseOperation();
		// 异常捕获，并发送以往信息
		// CrashHandler.getInstance().init(this);
		// Thread t = new Thread(new Runnable(){
		// @Override
		// public void run() { 
		// CrashHandler.getInstance().sendPreviousReportsToServer();
		// }
		// });
		// t.start();
		// t = null;

		String appId = "1104211531"; // 上Bugly(bugly.qq.com)注册产品获取的AppId

		boolean isDebug = false; // true代表App处于调试阶段，false代表App发布阶段

		CrashReport.initCrashReport(mContext, appId, isDebug); // 初始化SDK
	}

//	private void initDatabaseOperation() {
//		DatabaseUtil.getInstance();
//	}

	private void initApplicationMessage() {
		PackageInfo packageInfo = null;
		try {
			packageInfo = getContextFromApplication().getPackageManager()
					.getPackageInfo(
							getContextFromApplication().getPackageName(),
							PackageManager.GET_CONFIGURATIONS);
			if (null != packageInfo) {
				versionCode = String.valueOf(packageInfo.versionCode);
				versionName = packageInfo.versionName;
			}
			deviceId = getDeviceId(mContext);
		} catch (Exception e) {
		}
	}

	public static String getVersionCode() {
		return versionCode;
	}

	public static String getVersionName() {
		return versionName;
	}

	private void initConfigAndUser() {
		String userString = SharedPreferenceUtil.getString(
				Constant.SHAREDREFERENCES_CONFIG,
				Constant.SHAREDREFERENCES_CONFIG_USER);
		String configString = SharedPreferenceUtil.getString(
				Constant.SHAREDREFERENCES_CONFIG,
				Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION);
		user = JsonUtil.fromJson(userString, DataLogin.class);
		config = JsonUtil.fromJson(configString, DataConfig.class);
	}

	public static Context getContextFromApplication() {
		return mContext;
	}

	public static void setScreenWidth(int width) {
		SCREEN_WIDTH = width;
	}

	public static void setScreenHeight(int height) {
		SCREEN_HEIGHT = height;
	}

	public static int getScreenWidth() {
		return SCREEN_WIDTH;
	}

	public static int getScreentHeight() {
		return SCREEN_HEIGHT;
	}

	public static float getDensity() {
		return DENSITY;
	}

	public static void setDensity(float density) {
		DENSITY = density;
	}

	public static void updateConfig(OilSMSCategory oilSMSCategory) {
		if (null != getConfig().getConfig().getCategories()) {
			for (OilSMSCategory category : getConfig().getConfig()
					.getCategories()) {
				if (TextUtils.equals(category.getName(),
						oilSMSCategory.getName())) {
					category.setPushable(oilSMSCategory.getAllowPush());
				}
			}
		}
	}

	public static String getGroupIdByCategoryName(String string) {
		if (null == config) {
			return "";
		}
		if (null != config.getConfig().getCategories()) {
			for (OilSMSCategory category : config.getConfig().getCategories()) {
				if (TextUtils.equals(string, category.getName())) {
					return category.getGroupId();
				}
			}
		}
		return "";
	}

	public static String getGroupIds() {
		if (null != getConfig() && null != getConfig().getConfig()
				&& null != config.getConfig().getCategories()) {
			ArrayList<String> groupIds = new ArrayList<String>();
			for (int i = 0; i < config.getConfig().getCategories().size(); i++) {
				if (!TextUtils.isEmpty(config.getConfig().getCategories()
						.get(i).toString())) {
					groupIds.add(config.getConfig().getCategories().get(i)
							.toString());
				}
			}
			return TextUtils.join(",", groupIds);
		}
		return "";
	}

	public static int getCategoriesCount() {
		if (null != getConfig() && null != getConfig().getConfig()
				&& null != config.getConfig().getCategories()) {
			return config.getConfig().getCategories().size();
		}
		return 0;
	}

	public static String getGroupNameByGroupId(String groupId) {
		if (null != getConfig() && null != getConfig().getConfig()
				&& null != config.getConfig().getCategories()) {
			for (int i = 0; i < config.getConfig().getCategories().size(); i++) {
				if (TextUtils.equals(config.getConfig().getCategories().get(i)
						.getGroupId(), groupId)) {
					return config.getConfig().getCategories().get(i).getName();
				}
			}
		}
		return "";
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getCacheDirectory(context);
		ImageLoaderConfiguration config;
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.displayer(new FadeInBitmapDisplayer(500) {
					@Override
					public Bitmap display(Bitmap bitmap, ImageView imageView,
							LoadedFrom loadedFrom) {
						if (loadedFrom != LoadedFrom.MEMORY_CACHE) {
							return super.display(bitmap, imageView, loadedFrom);
						} else {
							imageView.setImageBitmap(bitmap);
							return bitmap;
						}
					}
				}).resetViewBeforeLoading(true).delayBeforeLoading(0)
				.cacheInMemory(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY) // default
				.bitmapConfig(Bitmap.Config.RGB_565) // default
				.build();
		int maxWidth = 480, maxHeight = 800;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			Canvas c = new Canvas();
			maxWidth = c.getMaximumBitmapWidth();
			maxHeight = c.getMaximumBitmapHeight();
			c = null;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			config = new ImageLoaderConfiguration.Builder(context)
					.taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR)
					.threadPoolSize(3)
					// default 3
					.threadPriority(Thread.MIN_PRIORITY)
					// default Thread.NORM_PRIORITY - 1
					.tasksProcessingOrder(QueueProcessingType.FIFO)
					// default FIFO
					.discCache(new UnlimitedDiscCache(cacheDir))
					// default
					.discCacheFileCount(200)
					.memoryCache(new WeakMemoryCache())
					.discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
					.imageDownloader(new BaseImageDownloader(context)) // default
					.defaultDisplayImageOptions(options) // default
					.build();
		} else {
			config = new ImageLoaderConfiguration.Builder(context)
					// .discCacheExtraOptions(maxWidth, maxHeight,
					// CompressFormat.JPEG, 100)
					.memoryCacheExtraOptions(maxWidth, maxHeight)
					.threadPoolSize(3)
					// default
					.threadPriority(Thread.MAX_PRIORITY - 2)
					// default
					.tasksProcessingOrder(QueueProcessingType.FIFO)
					// default
					.discCache(new UnlimitedDiscCache(cacheDir))
					// default
					.discCacheFileCount(100)
					.discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
					.imageDownloader(new BaseImageDownloader(context)) // default
					.defaultDisplayImageOptions(options) // default
					.build();
		}
		ImageLoader.getInstance().init(config);
	}

	public static int getGlobalTextColor() {
		return Color.BLACK;
	}

	public static int getGlobalBackground() {
		return Color.WHITE;
	}

	public static void exitUser() {
		setUser(null);
	}

	private static String getFooterText() {
		if (null != getConfig().getConfig()
				&& !TextUtils.isEmpty(getConfig().getConfig()
						.getRegistrationNumber())) {
			return String.format(getResourceString(R.string.main_contacter),
					getConfig().getConfig().getRegistrationNumber());
		}
		return "";
	}

	public static String getResourceString(int resId) {
		try {
			return getContextFromApplication().getResources().getString(resId);
		} catch (Exception e) {
			return "";
		}
	}

	public static void initFooter(final Context context,
			final TextView mTextFooter) {
		mTextFooter.setText(getFooterText());
		mTextFooter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					String number = getConfig().getConfig()
							.getRegistrationNumber().replaceAll("-", "")
							.replaceAll(" ", "");
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri
							.parse("tel:" + number));
					context.startActivity(intent);
				} catch (Exception e) {
				}
			}
		});
	}
}
