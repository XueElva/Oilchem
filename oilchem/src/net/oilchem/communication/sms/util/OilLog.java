package net.oilchem.communication.sms.util;

import android.util.Log;

public class OilLog {
	private static boolean DEBUG = true;
	
	public static void d(String log) {
		if (DEBUG) {
			Log.d("Oil", log);
		}
	}
}
