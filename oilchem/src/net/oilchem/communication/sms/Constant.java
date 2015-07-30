package net.oilchem.communication.sms;

public class Constant {
	public static final int NETWORK_METHOD_GET = 0;
	public static final int NETWORK_METHOD_POST = 1;
	public static final int NETWORK_METHOD_PUT = 2;
	public static final int NETWORK_METHOD_DELETE = 3;
	public static final int INVALID_INT = -1;
	
	public static final String API_PARAMS_FLAG = "flag";
	public static final String API_PARAMS_DATA = "data";
	public static final String API_PARAMS_STATUS = "stat";
	public static final String API_PARAMS_ERROR_MESSAGE = "error";
	public static final String API_PARAMS_ACCESS_TOKEN = "accessToken";
	public static final String API_PARAMS_KEY = "key";
	public static final String API_PARAMS_TS = "ts";
	public static final String API_PARAMS_MAXROW = "maxRow";
	public static final String API_PARAMS_USERNAME = "username";
	public static final String API_PARAMS_IMEI = "imei";
	public static final String API_PARAMS_PASSWORD = "password";
	public static final String API_PARAMS_PHONENUMBER = "cell";
	public static final String API_PARAMS_AUTHCODE = "authCode";
	public static final String API_PARAMS_WIDTH = "width";
	public static final String API_PARAMS_HEIGHT = "height";
	public static final String API_PARAMS_VERSION = "version";
	public static final String API_PARAMS_GROUPID = "groupId";
	public static final String API_PARAMS_GROUPIDS = "groupIds";
	public static final String API_PARAMS_ALLOWPUSH = "allowPush";
	public static final String API_RESPONSE_LOGIN = "login";
	//注册新增
	public static final String API_PARAMS_REGCLIENTID = "clientid";
	public static final String API_PARAMS_REGUSERNAME = "UserName";
	public static final String API_PARAMS_REGACTION = "action";
	public static final String API_PARAMS_REGVKEY = "vkey";
	public static final String API_PARAMS_REGPASSWORD = "PassWord";
	
	
	public static final String SHAREDREFERENCES_CONFIG = "config";
	public static final String SHAREDREFERENCES_CONFIG_USER = "user";
	public static final String SHAREDREFERENCES_CONFIG_NOTIFICATION_TS = "notification_ts";
	public static final String SHAREDREFERENCES_CONFIG_CONFIGURATION = "configuration";
	public static final String SHAREDREFERENCES_CONFIG_CONFIGURATION_PEROID = "peroid";
	public static final String SHAREDREFERENCES_CONFIG_CONFIGURATION_THEME = "configuration_theme";
	public static final String SHAREDREFERENCES_CONFIG_CONFIGURATION_FONTSIZE = "configuration_fontsize";
	public static final String SHAREDREFERENCES_CONFIG_CONFIGURATION_PUSHSOUND = "configuration_pushsound";
	public static final String SHAREDREFERENCES_CONFIG_CONFIGURATION_PUSHVIBRATE = "configuration_pushvibrate";
	public static final String SHAREDREFERENCES_CONFIG_CONFIGURATION_ADAPTERMODE = "configuration_adaptermode";
	public static final String SHAREDREFERENCES_CONFIG_CONFIGURATION_CACHETIME = "configuration_cachetime";
	public static final String SHAREDREFERENCES_WELCOME_LASTTS = "last_ts";
	//保存上次用户名
	public static final String SHAREDREFERENCES_LOGIN_SAVEUSER = "saved_username";
	//当前版本名，用于标注是否新装或升级
	public static final String SHAREDREFERENCES_LATEST_VERSION = "latest_version";

    public static final String API_PARAMS_MSGID = "msgId";
    public static final String API_PARAMS_REPLY = "reply";

    public static class string {
	}
}
