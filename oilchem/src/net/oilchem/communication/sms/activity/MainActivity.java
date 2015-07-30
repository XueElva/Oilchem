package net.oilchem.communication.sms.activity;

import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.data.OilchemContract;
import net.oilchem.communication.sms.data.OilchemDbHelper;
import net.oilchem.communication.sms.data.model.DataConfig;
import net.oilchem.communication.sms.data.model.DataUpgrade;
import net.oilchem.communication.sms.data.model.OilResponseData;
import net.oilchem.communication.sms.handler.HandlerBase;
import net.oilchem.communication.sms.handler.HandlerFactory;
import net.oilchem.communication.sms.handler.HandlerParams;
import net.oilchem.communication.sms.handler.IRequestListener;
import net.oilchem.communication.sms.service.NotificationCenterService;
import net.oilchem.communication.sms.service.PullNotificationService;
import net.oilchem.communication.sms.service.WakefulIntentService;
import net.oilchem.communication.sms.service.WakefulListener;
import net.oilchem.communication.sms.util.ApiUtil;
import net.oilchem.communication.sms.util.DatabaseUtil;
import net.oilchem.communication.sms.util.IApi.API;
import net.oilchem.communication.sms.util.IApi.RequestMethod;
import net.oilchem.communication.sms.util.OilUtil;
import net.oilchem.communication.sms.view.SearchBar;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.media.audiofx.NoiseSuppressor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.FrameLayout;

public class MainActivity extends OilActivityBase implements OnClickListener,
		IRequestListener {
	
	private TextView mImageRegister, mTextLogin,mVersion;
	private FrameLayout mLayoutLogin;
	private SearchBar mSearchbar;
	private HandlerBase configHandler;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_main);
		mVersion=(TextView) findViewById(R.id.activity_main_text_main);
		mLayoutLogin = (FrameLayout) findViewById(R.id.activity_main_layout_login);
		mTextLogin = (TextView) findViewById(R.id.activity_main_text_login);
		mImageRegister = (TextView) findViewById(R.id.activity_main_layout_register);
		mImageRegister.setText(Html.fromHtml("<u>" + mImageRegister.getText()
				+ "</u>"));
		mSearchbar = (SearchBar) findViewById(R.id.activity_main_view_searchbar);
		// mTextFooter = (TextView)
		// findViewById(R.id.activity_main_text_footer);
		
		try {
			mVersion.setText("隆众短讯通 v"+ this.getPackageManager().getPackageInfo(
						getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mLayoutLogin.setOnClickListener(this);
		mImageRegister.setOnClickListener(this);
		// OilchemApplication.initFooter(this, mTextFooter);
		mSearchbar.getSearchBtn().setOnClickListener(this);
		HandlerParams params = ApiUtil.getInstance().initConfig();
		configHandler = HandlerFactory.getHandler(params, this);
		ApiUtil.getInstance().sendRequest(configHandler);
		initWindowConfiguration();

		// if(OilchemApplication.isLogined()) {
		WakefulIntentService.scheduleAlarms(new WakefulListener(), this, false);
		// }
	}

	private void initWindowConfiguration() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		OilchemApplication.setScreenWidth(dm.widthPixels);
		OilchemApplication.setScreenHeight(dm.heightPixels - getStatusHeight());
		OilchemApplication.setDensity(dm.density);
	}

	public int getStatusHeight() {
		int statusHeight = 0;
		Rect localRect = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;
		if (0 == statusHeight) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer.parseInt(localClass
						.getField("status_bar_height").get(localObject)
						.toString());
				statusHeight = this.getResources().getDimensionPixelSize(i5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (OilchemApplication.isLogined()) {
			
			//初始化表名
			OilchemDbHelper.DATABASE_NAME="oilchem_"+OilchemApplication.getUser().getUsername();
	
			
			
			mImageRegister.setVisibility(View.INVISIBLE);
			mTextLogin.setText(OilchemApplication
					.getResourceString(R.string.enter));
		} else {
			mImageRegister.setVisibility(View.VISIBLE);
			mTextLogin.setText(OilchemApplication
					.getResourceString(R.string.login));
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.activity_main_layout_register:
			Intent intent = new Intent(view.getContext(),
					RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.activity_main_layout_login:
			if (OilchemApplication.isLogined()) {
				Intent intent1 = new Intent(this, OilchemWelcomeActivity.class);
				startActivity(intent1);
			} else {
				Intent loginIntent = new Intent(view.getContext(),
						LoginActivity.class);
				startActivity(loginIntent);
			}
			break;
		case R.id.view_searchbar_image_btn:
			if (TextUtils.isEmpty(mSearchbar.getQuery())) {
				OilUtil.showToast(R.string.toast_should_not_empty);
				return;
			}
			Intent smsListIntent = new Intent(view.getContext(),
					OilchemSmsListActivity.class);
			smsListIntent.putExtra(OilchemSmsListActivity.PARAMS_QUERY,
					mSearchbar.getQuery());
			smsListIntent.putExtra(OilchemSmsListActivity.PARAMS_LOCAL, false);
			startActivity(smsListIntent);
			break;
		default:
			break;
		}
	}

	@Override
	public void onRequestSuccess(RequestMethod method, API api,
			OilResponseData response, HandlerBase handler) {
		switch (api) {
		case API_CONFIG:
			if (configHandler == handler) {
				DataConfig data = (DataConfig) response;
				OilchemApplication.setConfig(data);
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
		
		if(api==API.API_ACCESSTOKAN_ERROR && OilchemApplication.isLogined()){
			Intent intent=new Intent(MainActivity.this,ErrorDialog.class);
			startActivity(intent);
		}
	}
}
