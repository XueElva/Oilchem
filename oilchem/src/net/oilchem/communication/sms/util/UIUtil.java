package net.oilchem.communication.sms.util;

import java.util.HashMap;
import java.util.Map.Entry;

import net.oilchem.communication.sms.OilchemApplication;
import android.content.Context;
import android.util.TypedValue;

public class UIUtil {
	private static float origRatio = 0f;
	private static float currRatio = 0f;

	private static final int INIT_WIDTH = 1080;
	private static final float INIT_DENSITY = 2.5f;
	private static HashMap<String, Boolean> themeMapper = new HashMap<String, Boolean>();
	
	public static HashMap<String, Boolean> getThemeMapper() {
		return themeMapper;
	}

	public static void setThemeMapper(String name) {
		themeMapper.put(name, false);
	}

	public static  void setChangedTheme(boolean _changedTheme) {
		for (Entry<String, Boolean> entry: themeMapper.entrySet()) {
			entry.setValue(true);
		}
	}

	public static boolean isChangedTheme(String name) {
		if (themeMapper.containsKey(name)) {
			return themeMapper.get(name);
		}
		return false;
	}

	public static String getAdaptedPixel(int resId, float ratio) {
		if (origRatio <=0 || currRatio <= 0) {
			origRatio = (float) INIT_WIDTH / (float) INIT_DENSITY;
			currRatio = (float) OilchemApplication.getScreenWidth() / OilchemApplication.getDensity();
		}
		float realDip = OilchemApplication.getContextFromApplication().getResources().getDimension(resId) / OilchemApplication.getDensity();
		return getPixel(realDip, ratio);
	}
	
	public static int getAdaptedPixel_Int(int resId, float ratio) {
		if (origRatio <=0 || currRatio <= 0) {
			origRatio = (float) INIT_WIDTH / (float) INIT_DENSITY;
			currRatio = (float) OilchemApplication.getScreenWidth() / OilchemApplication.getDensity();
		}
		float realDip = OilchemApplication.getContextFromApplication().getResources().getDimension(resId) / OilchemApplication.getDensity();
		return (int)(realDip * currRatio / origRatio * OilchemApplication.getDensity() * ratio);
	}
	
	public static float getPixelByDip(int dip) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, OilchemApplication.getContextFromApplication().getResources().getDisplayMetrics());
	}
	
	public static float getPixelByResId(int resId) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				OilchemApplication.getContextFromApplication().getResources().getDimension(resId),
				OilchemApplication.getContextFromApplication().getResources().getDisplayMetrics());
	}
	
	private static String getPixel(float dip, float ratio) {
		return String.valueOf(dip * currRatio / origRatio * OilchemApplication.getDensity() * ratio);
	}
	
	public static int sp2px(Context context, float spValue) {
		return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources()
				.getDisplayMetrics()) + 0.5f);
	}
	
	public static int dip2px(Context context, float dpValue) {
		return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources()
				.getDisplayMetrics()) + 0.5f);
	}
	
	/**
	 * 根据手机的分辨率从 px 的单位 转成为 dip(像素)
	 */
	 public static int convertPxOrDip(Context context, int px) { 
	     float scale = context.getResources().getDisplayMetrics().density; 
	     return (int)(px/scale + 0.5f*(px>=0?1:-1)); 
	 }  
}