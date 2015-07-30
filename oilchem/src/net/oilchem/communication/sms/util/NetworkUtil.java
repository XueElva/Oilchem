package net.oilchem.communication.sms.util;

import net.oilchem.communication.sms.OilchemApplication;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
	public static final String NETWORK_WIFI = "wifi";
	public static final String NETWORK_3GWAP = "3gwap";
	public static final String NETWORK_3GNET = "3gnet";
	public static final String NETWORK_3G = "3g";
	public static final String NETWORK_OTHER = "other";
	
	public static NetInfo currentNetInfo;
	
	public static NetInfo getCurrentNetInfo() {
		if (null == currentNetInfo) {
			updateNetInfo();
		}
		return currentNetInfo;
	}
	
	public static boolean currentNetworkAvailable() {
		updateNetInfo();
		return currentNetInfo.isConnected();
	}
	
	public static void updateNetInfo() { 
		ConnectivityManager manager = (ConnectivityManager) OilchemApplication.getContextFromApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netWorkInfo = manager.getActiveNetworkInfo();
		if (currentNetInfo == null) {
			currentNetInfo = new NetInfo();
		}
		if (null != netWorkInfo) {
			if (netWorkInfo.isConnected()) {
				switch(netWorkInfo.getType()) {
					case ConnectivityManager.TYPE_WIFI:
						currentNetInfo.setInfo(ConnectivityManager.TYPE_WIFI, NETWORK_WIFI);
						break;
					case ConnectivityManager.TYPE_MOBILE:
						if (null != netWorkInfo.getExtraInfo() && netWorkInfo.getExtraInfo().startsWith(NETWORK_3G)) { 
							currentNetInfo.setInfo(ConnectivityManager.TYPE_MOBILE, NETWORK_3G);
						}
						break;
					default:
						currentNetInfo.setInfo(ConnectivityManager.TYPE_MOBILE, NETWORK_OTHER);
						break;
				}
			}
		} else {
			if (null != currentNetInfo) {
				currentNetInfo.setConnected(false);
			}
		}
		if (null != netWorkInfo && null != currentNetInfo) {
			currentNetInfo.setConnected(netWorkInfo.isConnected());
		}
	}
	
	public static class NetInfo {
		private String name;
		private int id;
		private boolean connected;

		public NetInfo() {
			this.name = NETWORK_OTHER;
			this.id = ConnectivityManager.TYPE_WIFI;
			this.connected = false;
		}

		public String getName() {
			return name;
		}

		public int getId() {
			return id;
		}
		
		public void setInfo(int state, String name) {
			this.id = state;
			this.name = name;
		}

		public boolean isConnected() {
			return connected;
		}
		
		public boolean isWifi() {
			return this.id == ConnectivityManager.TYPE_WIFI ? true : false;
		}

		public void setConnected(boolean connected) {
			this.connected = connected;
		}
		
		public String toString() {
			return String.format("NET-STATE: name: %s == id: %s == isconnected: %s", name, id, connected);
		}
	}
}
