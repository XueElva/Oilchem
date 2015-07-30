package net.oilchem.communication.sms.util;

import net.oilchem.communication.sms.Constant;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.handler.BinaryHandlerBase;
import net.oilchem.communication.sms.handler.HandlerBase;
import net.oilchem.communication.sms.handler.HandlerParams;

import java.util.HashMap;

import android.util.Log;

public class ApiUtil implements IApi {
	private HashMap<String, String> commonParams;
	
	public HashMap<String, String> getParams() {
		return commonParams;
	}

	private ApiUtil(HashMap<String, String> params) {
		commonParams = params;
	}

	public static class SingletonInstance {
		private static ApiUtil instance;
		
		private static ApiUtil getInstance() {
			if (instance == null) {
				HashMap<String, String> commonParams = new HashMap<String, String>();
				if (OilchemApplication.isLogined()) {
					commonParams.put(Constant.API_PARAMS_ACCESS_TOKEN, OilchemApplication.getUser().getAccessToken());
				}
				instance = new ApiUtil(commonParams);
			}
			return instance;
		}

		public static void updateInstance() {
			if (instance != null) {
				HashMap<String, String> commonParams = new HashMap<String, String>();
				if (OilchemApplication.isLogined()) {
					commonParams.put(Constant.API_PARAMS_ACCESS_TOKEN, OilchemApplication.getUser().getAccessToken());
				}
				instance = new ApiUtil(commonParams);
			}
		}
	}
	
	public static ApiUtil getInstance() {
		return SingletonInstance.getInstance();
	}
	
	public void updateInstance() {
		SingletonInstance.updateInstance();
	}
	
	public void sendRequest(HandlerBase handler) {
		OilchemApiClient.sendRequest(handler);
	}

	public void sendRequest(String url, BinaryHandlerBase authCodeHandler) {
		OilchemApiClient.sendRequest(url, authCodeHandler);
	}
	
	@Override
	public HandlerParams initConfig() {
		HandlerParams params = new HandlerParams(RequestMethod.GET, API.API_CONFIG, getParams());
		return params;
	}

	@Override
	public HandlerParams initSmsSearch(String key, String ts) {
		HandlerParams params = new HandlerParams(RequestMethod.GET, API.API_SMS_SEARCH, getParams());
		params.put(Constant.API_PARAMS_KEY, key);
		params.put(Constant.API_PARAMS_TS, ts);
		return params;
	}

    @Override
    public HandlerParams initPushReply(String msgId, String username, String reply) {
        HandlerParams params = new HandlerParams(RequestMethod.GET, API.API_PUSHREPLY, getParams());
        params.put(Constant.API_PARAMS_MSGID, msgId);
        params.put(Constant.API_PARAMS_USERNAME, username);
        params.put(Constant.API_PARAMS_REPLY, reply);
        return params;
    }

    @Override
    public HandlerParams initGetReplies(String msgId, String username) {
        HandlerParams params = new HandlerParams(RequestMethod.GET, API.API_GET_REPLIES, getParams());
        params.put(Constant.API_PARAMS_MSGID, msgId);
        params.put(Constant.API_PARAMS_USERNAME, username);
        return params;
    }

    @Override
	public HandlerParams initLogin(String username, String password) {
		HandlerParams params = new HandlerParams(RequestMethod.GET, API.API_LOGIN, getParams());
		params.put(Constant.API_PARAMS_USERNAME, username);
		params.put(Constant.API_PARAMS_PASSWORD, password);
//		params.put(Constant.API_PARAMS_USERNAME, "13002714165");
//		params.put(Constant.API_PARAMS_PASSWORD, OilUtil.getMD5("abc123"));
		params.put(Constant.API_PARAMS_IMEI, OilchemApplication.getDeviceId());
		return params;
	}
	
	@Override
	public HandlerParams initPhoneCode(String phoneNumber) {
		HandlerParams params = new HandlerParams(RequestMethod.GET, API.API_PHONECODE, getParams());
		params.put(Constant.API_PARAMS_REGCLIENTID, Constant.API_PARAMS_REGUSERNAME);
		params.put(Constant.API_PARAMS_REGUSERNAME, phoneNumber);
		return params;
	}

	@Override
	public HandlerParams initRegister(String phoneNumber, String phoneCode, String password) {
		HandlerParams params = new HandlerParams(RequestMethod.POST, API.API_REGISTER, getParams());
		params.put(Constant.API_PARAMS_REGACTION, "android");
		params.put(Constant.API_PARAMS_REGUSERNAME, phoneNumber);
		params.put(Constant.API_PARAMS_REGVKEY, phoneCode);
		params.put(Constant.API_PARAMS_REGPASSWORD, password);
		return params;
	}

	@Override
	public HandlerParams initCertificationCode(String width, String height) {
		HandlerParams params = new HandlerParams(RequestMethod.GET, API.API_CERTIFICATION_CODE, getParams());
		params.put(Constant.API_PARAMS_WIDTH, width);
		params.put(Constant.API_PARAMS_HEIGHT, height);
		return params;
	}

	@Override
	public HandlerParams initWelcomeByDay(String ts) {
		HandlerParams params = new HandlerParams(RequestMethod.GET, API.API_WELCOME_GET_BY_DAY, getParams());
		params.put(Constant.API_PARAMS_TS, ts);
		params.put(Constant.API_PARAMS_MAXROW, 1000); //最大行数
		return params;
	}
	
	@Override
	public HandlerParams initWelcome(String ts) {
		HandlerParams params = new HandlerParams(RequestMethod.GET, API.API_WELCOME, getParams());
		params.put(Constant.API_PARAMS_TS, ts);
		return params;
	}
	
	

	@Override
	public HandlerParams initUpgrade(String versionCode) {
		HandlerParams params = new HandlerParams(RequestMethod.GET, API.API_UPGRADE, getParams());
		params.put(Constant.API_PARAMS_VERSION, versionCode);
		return params;
	}

	@Override
	public HandlerParams initLogout() {
		HandlerParams params = new HandlerParams(RequestMethod.GET, API.API_LOGOUT, getParams());
		if (OilchemApplication.isLogined()) {
			params.put(Constant.API_PARAMS_USERNAME, OilchemApplication.getUser().getUsername());
		}
		return params;
	}

	@Override
	public HandlerParams initNotificationChange(String groupId, String allowPush) {
		HandlerParams params = new HandlerParams(RequestMethod.GET, API.API_NOTIFICATION_CHANGE, getParams());
		params.put(Constant.API_PARAMS_GROUPID, groupId);
		params.put(Constant.API_PARAMS_ALLOWPUSH, allowPush);
		return params;
	}

	@Override
	public HandlerParams initNotification() {
		HandlerParams params = new HandlerParams(RequestMethod.POST, API.API_NOTIFICATION, getParams());
//		params.put(Constant.API_PARAMS_GROUPIDS, OilchemApplication.getGroupIds());
		String value=SharedPreferenceUtil.getString(Constant.SHAREDREFERENCES_CONFIG, 
				Constant.SHAREDREFERENCES_CONFIG_NOTIFICATION_TS, "");
		params.put(Constant.API_PARAMS_TS,value);
		return params;
	}

	@Override
	public HandlerParams initGetCategories() {
		HandlerParams params = new HandlerParams(RequestMethod.GET, API.API_GET_CATEGORIES, getParams());
		return params;
	}

	

}
