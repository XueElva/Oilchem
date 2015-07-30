package net.oilchem.communication.sms.service;

import java.util.Random;

import net.oilchem.communication.sms.Constant;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.activity.OilchemWelcomeActivity;
import net.oilchem.communication.sms.data.model.DataSmsList;
import net.oilchem.communication.sms.data.model.OilResponseData;
import net.oilchem.communication.sms.handler.HandlerBase;
import net.oilchem.communication.sms.handler.HandlerFactory;
import net.oilchem.communication.sms.handler.HandlerParams;
import net.oilchem.communication.sms.handler.IRequestListener;
import net.oilchem.communication.sms.util.ApiUtil;
import net.oilchem.communication.sms.util.IApi.API;
import net.oilchem.communication.sms.util.IApi.RequestMethod;
import net.oilchem.communication.sms.util.NetworkUtil;
import net.oilchem.communication.sms.util.SharedPreferenceUtil;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class NotificationCenterService extends Service implements IRequestListener{
	public static final int NOTICENUM_UPDATE_TIME = 60000;
	
	private Handler netWorkHandler = new Handler();
	private Runnable netWorkRunnable;
	private HandlerBase notificationHandler;
    private NotificationManager mNotificationManager;
	
	@Override
	public void onRequestSuccess(RequestMethod method, API api,
			OilResponseData response, HandlerBase handler) {
		switch(api) {
		case API_NOTIFICATION:
			if (notificationHandler == handler) {
				
				DataSmsList data = (DataSmsList) response;
				if (data.getMessages() != null && data.getMessages().size() > 0) {
					SharedPreferenceUtil.setString(Constant.SHAREDREFERENCES_CONFIG, Constant.SHAREDREFERENCES_CONFIG_NOTIFICATION_TS, data.getTs());
					Intent notificationIntent = new Intent(NotificationCenterService.this, OilchemWelcomeActivity.class);
					notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//					notificationIntent.putExtra(Constant.STARTUP_MEANS, Constant.STARTUP_FROM_NOTIFICATION);
					PendingIntent contentIntent = PendingIntent.getActivity(NotificationCenterService.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					Notification notification = new Notification();  	// new Notification(drawableResId, notyInfo.ticker, System.currentTimeMillis()); 
					notification.icon = R.drawable.ic_launcher;
					notification.tickerText = data.getMessages().get(0).getGroupName();
					notification.when = System.currentTimeMillis();
					if (TextUtils.equals(SharedPreferenceUtil.getString(Constant.SHAREDREFERENCES_CONFIG,
							Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_PUSHSOUND), OilchemApplication.getResourceString(R.string.taber_setting_theme_open))) {
						notification.defaults |= Notification.DEFAULT_SOUND;
					}
					notification.defaults |= Notification.DEFAULT_VIBRATE;
					notification.flags |= Notification.FLAG_AUTO_CANCEL;
					notification.setLatestEventInfo(NotificationCenterService.this, data.getMessages().get(0).getGroupName(), data.getMessages().get(0).getContent(), contentIntent);
					mNotificationManager.notify(new Random().nextInt(), notification);
				}
			}
			break;
		}
	}

	@Override
	public void onRequestError(RequestMethod method, API api,
			HandlerBase handler) {
	}

	public class LocalBinder extends Binder {
		public NotificationCenterService getService() {
			return NotificationCenterService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return new LocalBinder();
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (null == notificationHandler) {
			HandlerParams params = ApiUtil.getInstance().initNotification();
			notificationHandler = HandlerFactory.getHandler(params, this);
		}
		if (null == mNotificationManager) {
			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}
		if (null == netWorkHandler) {
			netWorkHandler = new Handler();
		}
		if (null == netWorkRunnable) {
			netWorkRunnable = new Runnable() {
				@Override
				public void run() {
					if (!NetworkUtil.currentNetworkAvailable()) {
						return;
					}
					if (OilchemApplication.isLogined()) {
						ApiUtil.getInstance().sendRequest(notificationHandler);
					}
					netWorkHandler.removeCallbacks(netWorkRunnable);
					netWorkHandler.postDelayed(netWorkRunnable, NOTICENUM_UPDATE_TIME);
				}
			};
			netWorkHandler.removeCallbacks(netWorkRunnable);
			netWorkHandler.postDelayed(netWorkRunnable, NOTICENUM_UPDATE_TIME);
		}
	}

}
