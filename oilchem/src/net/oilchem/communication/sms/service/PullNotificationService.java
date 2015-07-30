package net.oilchem.communication.sms.service;

import java.util.Random;

import net.oilchem.communication.sms.Constant;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.activity.ErrorDialog;
import net.oilchem.communication.sms.activity.LoginActivity;
import net.oilchem.communication.sms.activity.OilchemWelcomeActivity;
import net.oilchem.communication.sms.activity.OilchemSmsListActivity;
import net.oilchem.communication.sms.data.model.DataSmsList;
import net.oilchem.communication.sms.data.model.OilResponseData;
import net.oilchem.communication.sms.handler.HandlerBase;
import net.oilchem.communication.sms.handler.HandlerFactory;
import net.oilchem.communication.sms.handler.HandlerParams;
import net.oilchem.communication.sms.handler.IRequestListener;
import net.oilchem.communication.sms.util.ApiUtil;
import net.oilchem.communication.sms.util.NetworkUtil;
import net.oilchem.communication.sms.util.IApi.API;
import net.oilchem.communication.sms.util.IApi.RequestMethod;
import net.oilchem.communication.sms.util.SharedPreferenceUtil;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


public class PullNotificationService extends WakefulIntentService {
	private HandlerBase notificationHandler;
    private NotificationManager mNotificationManager;
    private Context ctx;
    private PullNotificationTask task;
    private HandlerBase  logoutHandler;
	public PullNotificationService() {
		super("PullNotificationService");
	
	}

	/**
	 * 震动方法 
	 */
	public void playVibrate() {
		Vibrator vibrator;
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 100, 400, 100, 400 }; // 停止 开启 停止 开启
		vibrator.vibrate(pattern, -1);
	}

	@Override
	protected void doWakefulWork(Context context, Intent intent) {
		if (null == mNotificationManager) {
			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}
		ctx = context;
		if (!NetworkUtil.currentNetworkAvailable() || !OilchemApplication.isLogined()) {
			return;
		}
		task = new PullNotificationTask();
		task.execute("");
	}
	
	private class PullNotificationTask extends AsyncTask<String, Void, Void>  implements IRequestListener {

		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		@Override
		public void onRequestSuccess(RequestMethod method, API api,
				OilResponseData response, HandlerBase handler) {
			switch(api) {
			case API_NOTIFICATION: //轮询返回
				try {
					if (notificationHandler == handler) {
					
						DataSmsList data = (DataSmsList) response;					
						if (data.getMessages() != null && data.getMessages().size() > 0) {
							SharedPreferenceUtil.setString(Constant.SHAREDREFERENCES_CONFIG, Constant.SHAREDREFERENCES_CONFIG_NOTIFICATION_TS, data.getTs());
							int notificationId = new Random().nextInt();
							Intent notificationIntent = new Intent(ctx, OilchemWelcomeActivity.class);
							notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							notificationIntent.putExtra(OilchemSmsListActivity.PARAMS_PUSH_DATA, data);
							notificationIntent.setAction("oilchem" + notificationId);
							PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
							
							Notification notification = null;
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
								Notification.Builder builder = new Notification.Builder(PullNotificationService.this)
								.setSmallIcon(R.drawable.ic_launcher)
								.setTicker(data.getMessages().get(0).getGroupName())
								.setWhen(System.currentTimeMillis())
								.setContentIntent(contentIntent)
								.setContentTitle(data.getMessages().get(0).getGroupName())
								.setContentText(data.getMessages().get(0).getContent());
								//震动改为可配置
								//int defaults = Notification.DEFAULT_VIBRATE;
								int defaults = 0;
								if (TextUtils.equals(SharedPreferenceUtil.getString(Constant.SHAREDREFERENCES_CONFIG,
										Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_PUSHSOUND, OilchemApplication.getResourceString(R.string.taber_setting_theme_open)), OilchemApplication.getResourceString(R.string.taber_setting_theme_open))) {
									defaults |= Notification.DEFAULT_SOUND;
								}
								if (defaults != 0) {
									builder.setDefaults(defaults);
								}
								builder.setAutoCancel(true);
								notification = builder.build();
							} else {
								notification = new Notification();  	// new Notification(drawableResId, notyInfo.ticker, System.currentTimeMillis()); 
								notification.icon = R.drawable.ic_launcher;
								notification.tickerText = data.getMessages().get(0).getGroupName();
								notification.when = System.currentTimeMillis();
								//震动改为可配置
								//notification.defaults |= Notification.DEFAULT_VIBRATE;
								if (TextUtils.equals(SharedPreferenceUtil.getString(Constant.SHAREDREFERENCES_CONFIG,
										Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_PUSHSOUND, OilchemApplication.getResourceString(R.string.taber_setting_theme_open)), OilchemApplication.getResourceString(R.string.taber_setting_theme_open))) {
									notification.defaults |= Notification.DEFAULT_SOUND;
								}
								notification.flags |= Notification.FLAG_AUTO_CANCEL;
								notification.setLatestEventInfo(PullNotificationService.this, data.getMessages().get(0).getGroupName(), data.getMessages().get(0).getContent(), contentIntent);
							}
							mNotificationManager.notify(notificationId, notification);
							//不明原因导致震动只在4.3有效，因此直接调用震动即可，这算是个补丁
							if (TextUtils.equals(SharedPreferenceUtil.getString(Constant.SHAREDREFERENCES_CONFIG,
									Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_PUSHVIBRATE, OilchemApplication.getResourceString(R.string.taber_setting_theme_open)), OilchemApplication.getResourceString(R.string.taber_setting_theme_open))) {
								playVibrate();
							}
							
						}
					}
				} catch (Exception e) {
				}
				break;
			default:
				break;
			}
		}
		
		@Override
		public void onRequestError(RequestMethod method, API api,
				HandlerBase handler) {
			//accesstToken验证失败
			if(api==API.API_ACCESSTOKAN_ERROR){
				Intent intent=new Intent(PullNotificationService.this,ErrorDialog.class);
				startActivity(intent);
			}
		}

		@Override
		protected Void doInBackground(String... arg0) {
			//轮询
			HandlerParams params = ApiUtil.getInstance().initNotification();
			notificationHandler = HandlerFactory.getHandler(params, PullNotificationTask.this);
			if (OilchemApplication.isLogined()) {
				ApiUtil.getInstance().sendRequest(notificationHandler);
			} 
			return null;
		}
	}
	


}
