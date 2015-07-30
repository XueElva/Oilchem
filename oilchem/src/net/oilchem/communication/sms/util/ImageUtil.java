package net.oilchem.communication.sms.util;

import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class ImageUtil {
	public static final int MODE_JPEG = 0;
	public static final int MODE_PNG = 1;
	
	public static final int LOAD_RESUME = 0;
	public static final int LOAD_STOP = 1;
	public static final int LOAD_PAUSE = 2;
	
	private static ImageLoader imageLoader = ImageLoader.getInstance();
	static {
		imageLoader.handleSlowNetwork(true);
	}
	
	public static void setImageLoadState(int state) {
		switch(state) {
		case LOAD_STOP:
			imageLoader.stop();
			break;
		case LOAD_RESUME:
			imageLoader.resume();
			break;
		case LOAD_PAUSE:
			imageLoader.pause();
			break;
		default:
			imageLoader.resume();
			break;
		}
	}
	
	public static void cancelTask(ImageView imageView) {
		if (null == imageView) {
			return;
		}
		imageLoader.cancelDisplayTask(imageView);
		String loadingUri = imageLoader.getLoadingUriForView(imageView);
		if (imageLoader.getMemoryCache().keys().contains(loadingUri)) {
			imageLoader.getMemoryCache().remove(loadingUri);
		}
	}
	
	public static void clearImage(ImageView imageView) {
		if (null == imageView) {
			return;
		}
		imageLoader.clearMemoryCache();
		imageLoader.clearDiscCache();
	}
	
	public static void displayImage(String uri, ImageView shownImage) {
		if (null == shownImage || uri == null || TextUtils.isEmpty(uri)) {
			return;
		}
		imageLoader.displayImage(uri, shownImage);
	}
	
	public static void displayImage(String uri, ImageView shownImage, int mode) {
		if (null == shownImage || uri == null || TextUtils.isEmpty(uri)) {
			return;
		}
		if (mode == MODE_JPEG) {
			displayImage(uri, shownImage);
		} else if (mode == MODE_PNG) {
			imageLoader.displayImage(uri, shownImage);
		}
	}
	
	public static void loadImage(String uri, ImageLoadingListener listener) {
		imageLoader.loadImage(uri, listener);
	}
	
	public static String getAuthCodeImage(String width, String height) {
		return String.format(OilchemApiClient.BASE_URL + "/authCode?height=%s&width=%s", width, height);
	}
}
