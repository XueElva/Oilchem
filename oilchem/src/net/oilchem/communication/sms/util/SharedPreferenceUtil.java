package net.oilchem.communication.sms.util;

import net.oilchem.communication.sms.OilchemApplication;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {
    private final static int MODE = Context.MODE_PRIVATE;
    
    private final static String DEFAULT_STRING = "";

    public static String getString(String name, String key) {
    	Context context = OilchemApplication.getContextFromApplication();
    	SharedPreferences mSharedPreferences = context.getSharedPreferences(name, MODE);
    	return mSharedPreferences.getString(key, DEFAULT_STRING);
    }

    public static String getString(String name, String key, String defaultString) {
    	Context context = OilchemApplication.getContextFromApplication();
    	SharedPreferences mSharedPreferences = context.getSharedPreferences(name, MODE);
    	return mSharedPreferences.getString(key, defaultString);
    }
    
    public static boolean setString(String name, String key, String value) {
    	Context context = OilchemApplication.getContextFromApplication();
    	SharedPreferences mSharedPreferences = context.getSharedPreferences(name, MODE);
    	SharedPreferences.Editor mEditor = mSharedPreferences.edit();
    	mEditor.putString(key, value);
    	return mEditor.commit();
    }

    public static boolean getBoolean(String name, String key) {
    	Context context = OilchemApplication.getContextFromApplication();
    	SharedPreferences mSharedPreferences = context.getSharedPreferences(name, MODE);
    	return mSharedPreferences.getBoolean(key, false);
    }
    
    public static boolean setBoolean(String name, String key, boolean value) {
    	Context context = OilchemApplication.getContextFromApplication();
    	SharedPreferences mSharedPreferences = context.getSharedPreferences(name, MODE);
    	SharedPreferences.Editor mEditor = mSharedPreferences.edit();
    	mEditor.putBoolean(key, value);
    	return mEditor.commit();
    }
    
    public static boolean delete(Context context, String name, String key) {
    	if (context == null) {
    		return false;
    	}
    	SharedPreferences mSharedPreferences = context.getSharedPreferences(name, MODE);
    	SharedPreferences.Editor mEditor = mSharedPreferences.edit();
    	mEditor.remove(key);
    	return mEditor.commit();
    }
    
    public static boolean clear(Context context, String name) {
    	if (context == null) {
    		return false;
    	}
    	SharedPreferences mSharedPreferences = context.getSharedPreferences(name, MODE);
    	SharedPreferences.Editor mEditor = mSharedPreferences.edit();
    	mEditor.clear();
    	return mEditor.commit();
    }
}
