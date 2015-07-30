package net.oilchem.communication.sms.data.model;

import java.io.Serializable;
import java.util.ArrayList;

import android.text.TextUtils;

public class DataConfig extends OilResponseData {
	private static final long serialVersionUID = -3342554894085587625L;

	private OilConfig config;

	public DataConfig(OilConfig config) {
		this.config = config;
	}

	public OilConfig getConfig() {
		return config;
	}

	public void setConfig(OilConfig config) {
		this.config = config;
	}

	public static class OilConfig implements Serializable {
		private static final long serialVersionUID = -6570149817647921040L;

		private ArrayList<OilSMSCategory> categories;
		private String customerServicename;
		private String customerServiceNumber;
		private String registrationNumber;
		private String pageSizeWhileSearchingLocalSMS;
		private String latestAppVersion;
		private String appDownload;
		private String getPushTimeInterval;

		public int getGetPushTimeInterval() {
			if (TextUtils.isEmpty(getPushTimeInterval)) {
				return 60000;
			}
			try {
				return Integer.valueOf(getPushTimeInterval) * 1000;
			} catch(Exception e) {
				return 60000;
			}
		}

		public void setGetPushTimeInterval(String getPushTimeInterval) {
			this.getPushTimeInterval = getPushTimeInterval;
		}

		public OilConfig(String customerServiceName,
				String customerServiceNumber, String registrationNumber,
				String pageSizeWhileSearchingLocalSMS, String latestAppVersion,
				String appDownload) {
			this.customerServicename = customerServiceName;
			this.customerServiceNumber = customerServiceNumber;
			this.registrationNumber = registrationNumber;
			this.pageSizeWhileSearchingLocalSMS = pageSizeWhileSearchingLocalSMS;
			this.latestAppVersion = latestAppVersion;
			this.appDownload = appDownload;
		}

		public ArrayList<OilSMSCategory> getCategories() {
			return categories;
		}

		public void setCategories(ArrayList<OilSMSCategory> categories) {
			this.categories = categories;
		}

		public String getCustomerServiceName() {
			return customerServicename;
		}

		public void setCustomerServiceName(String customerServiceName) {
			this.customerServicename = customerServiceName;
		}

		public String getCustomerServiceNumber() {
			return customerServiceNumber;
		}

		public void setCustomerServiceNumber(String customerServiceNumber) {
			this.customerServiceNumber = customerServiceNumber;
		}

		public String getRegistrationNumber() {
			return registrationNumber;
		}

		public void setRegistrationNumber(String registrationNumber) {
			this.registrationNumber = registrationNumber;
		}

		public int getPageSizeWhileSearchingLocalSMS() {
			int count = 0;
			try {
				count = Integer.valueOf(pageSizeWhileSearchingLocalSMS);
			} catch(Exception e) {
			}
			return count; 
		}

		public void setPageSizeWhileSearchingLocalSMS(
				String pageSizeWhileSearchingLocalSMS) {
			this.pageSizeWhileSearchingLocalSMS = pageSizeWhileSearchingLocalSMS;
		}

		public String getLatestAppVersion() {
			return latestAppVersion;
		}

		public void setLatestAppVersion(String latestAppVersion) {
			this.latestAppVersion = latestAppVersion;
		}

		public String getAppDownload() {
			return appDownload;
		}

		public void setAppDownload(String appDownload) {
			this.appDownload = appDownload;
		}
	}

	public static class OilSMSCategory implements Serializable {
		private static final long serialVersionUID = -3282452465008356934L;

		private String name;
		private String allowPush;
		private String groupId;
		
		@Override
		public String toString() {
			if (pushable()) {
				return groupId;
			}
			return "";
		}

		public String getGroupId() {
			return groupId;
		}

		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}

		public String getName() {
			return name;
		}

		public String getAllowPush() {
			return allowPush;
		}
		
		public void setName(String name) {
			this.name = name;
		}

		public boolean pushable() {
			if ("1".equals(allowPush)) {
				return true;
			}
			return false;
		}
		
		public void setPushable(String pushable) {
			this.allowPush = pushable;
		}
		
		public static String getPushableForApi(boolean pushable) {
			if (pushable) return "1";
			return "0";
		}
	}
}
