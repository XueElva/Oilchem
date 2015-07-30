package net.oilchem.communication.sms.util;

import net.oilchem.communication.sms.Constant;
import net.oilchem.communication.sms.handler.HandlerParams;

public interface IApi {
	public enum RequestMethod {
		GET(Constant.NETWORK_METHOD_GET),
		POST(Constant.NETWORK_METHOD_POST),
		PUT(Constant.NETWORK_METHOD_PUT),
		DELETE(Constant.NETWORK_METHOD_DELETE),
		INVALID(Constant.INVALID_INT);
		
		private int method;

		RequestMethod(int method) {
			this.method = method;
		}
		
		@Override
		public String toString() {
			return String.valueOf(method);
		}
	}
	
	public static enum API {
		API_CONFIG("/sms/getConfig.do"),
		API_SMS_SEARCH("/sms/getMessageTrial.do"),
		API_LOGIN("/user/userLogin.do"),
		//for test
//		API_PHONECODE("http://10.101.126.38/oil/phonecode.json"),
//		API_REGISTER("http://10.101.126.38/oil/reg.json"),
		API_PHONECODE("http://www.oilchem.net/reg/getcode/"),
		API_REGISTER("http://www.oilchem.net/reg/"),
		API_WELCOME("/sms/getMessages.do"),
		API_WELCOME_GET_BY_DAY("/sms/getDayMsg.do"), //按天取
        API_GET_REPLIES("/sms/getReplies.do"),
        API_PUSHREPLY("/sms/pushReply.do"),
		API_UPGRADE("/user/updateApp.do"),
		API_LOGOUT("/user/userLogout.do"),
		API_NOTIFICATION_CHANGE("/sms/changePushStat.do"),
		API_NOTIFICATION("/sms/getPushSMS.do"),
		API_CERTIFICATION_CODE("/user/authCode.do"),
		API_GET_CATEGORIES("/sms/getCategories.do"),
		API_ACCESSTOKAN_ERROR("");
//		API_CONFIG("/getConfig"),
//		API_SMS_SEARCH("/getMessageTrial"),
//		API_LOGIN("/userLogin"),
//		API_REGISTER("/userRegister"),
//		API_WELCOME("/getMessages"),
//		API_UPGRADE("/updateApp"),
//		API_LOGOUT("/logout"),
//		API_NOTIFICATION_CHANGE("/changePushStat"),
//		API_NOTIFICATION("/getPushSMS"),
//		API_CERTIFICATION_CODE("/getAuthCode"),
//		API_GET_CATEGORIES("/getCategories");
		
		private String content;
		
		API(String content) {
			this.content = content;
		}
		
		@Override
		public String toString() {
			return content;
		}
	}
	
	public HandlerParams initConfig();

	public HandlerParams initSmsSearch(String key, String ts);

    public HandlerParams initPushReply(String msgId,String username, String reply);

    public HandlerParams initGetReplies(String msgId,String username);
	
	public HandlerParams initLogin(String username, String password); //password md5
	
	/**
	 * 手机注册短信码获取请求参数
	 * @param phoneNumber
	 * @return
	 */
	public HandlerParams initPhoneCode(String phoneNumber);
	
	/**
	 * 手机注册请求参数
	 * @param phoneNumber
	 * @param authCode
	 * @return
	 */
	public HandlerParams initRegister(String phoneNumber, String phoneCode, String password);
	
	public HandlerParams initCertificationCode(String width, String height); //验证码
	
	public HandlerParams initWelcomeByDay(String ts);
	
	public HandlerParams initWelcome(String ts);
	
	public HandlerParams initUpgrade(String versionCode);
	
	public HandlerParams initLogout();
	
	public HandlerParams initNotificationChange(String category, String allowPush);
	
	public HandlerParams initNotification();
	
	public HandlerParams initGetCategories();
}
