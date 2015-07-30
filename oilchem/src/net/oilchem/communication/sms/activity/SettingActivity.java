package net.oilchem.communication.sms.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.oilchem.communication.sms.Constant;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.data.model.DataLogout;
import net.oilchem.communication.sms.data.model.DataUpgrade;
import net.oilchem.communication.sms.data.model.OilResponseData;
import net.oilchem.communication.sms.handler.HandlerBase;
import net.oilchem.communication.sms.handler.HandlerFactory;
import net.oilchem.communication.sms.handler.HandlerParams;
import net.oilchem.communication.sms.handler.IRequestListener;
import net.oilchem.communication.sms.service.PullNotificationService;
import net.oilchem.communication.sms.util.*;
import net.oilchem.communication.sms.util.IApi.API;
import net.oilchem.communication.sms.util.IApi.RequestMethod;
import net.oilchem.communication.sms.view.TaberView;
import net.oilchem.communication.sms.view.TaberView.TaberSelectedListener;
import net.oilchem.communication.sms.view.TitleBar;

public class SettingActivity extends OilActivityBase implements
		OnClickListener, IRequestListener, TaberSelectedListener {

	private TextView mTextLogout, mTextUsername, mTextCache, mTextUpgrade,
			mTextAbout, mTextSuggesiton, mTextClear;
	private TitleBar titlebar;
	private LinearLayout mLayoutUsername;
	private TaberView mTaberTheme, mTaberTextSize, mTaberPushSound,
			mTaberPushVibrate, mTaberAdapterMode;
	private HandlerBase updagradeHandler, logoutHandler;
	public static boolean adapterModeChanged;
	private AlertDialog.Builder builder;
	private AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_setting);
		mTextLogout = (TextView) findViewById(R.id.activity_setting_text_logout);
		titlebar = (TitleBar) findViewById(R.id.activity_setting_titlebar);
		mLayoutUsername = (LinearLayout) findViewById(R.id.activity_setting_username);
		mTextUsername = (TextView) findViewById(R.id.activity_setting_text_username);
		mTextCache = (TextView) findViewById(R.id.activity_setting_text_cache);
		mTextUpgrade = (TextView) findViewById(R.id.activity_setting_text_upgrade);
		mTextAbout = (TextView) findViewById(R.id.activity_setting_text_about);
		mTextClear = (TextView) findViewById(R.id.activity_setting_text_clearcache);
		mTextSuggesiton = (TextView) findViewById(R.id.activity_setting_text_suggestion);
		mTaberTheme = (TaberView) findViewById(R.id.activity_setting_taber_mode);
		mTaberTextSize = (TaberView) findViewById(R.id.activity_setting_taber_fontsize);
		mTaberPushSound = (TaberView) findViewById(R.id.activity_setting_taber_push_sound);
		mTaberPushVibrate = (TaberView) findViewById(R.id.activity_setting_taber_push_vibrate);
		mTaberAdapterMode = (TaberView) findViewById(R.id.activity_setting_taber_adapter_mode);
		mTaberTheme
				.initTaber(
						OilchemApplication
								.getResourceString(R.string.taber_setting_theme),
						SharedPreferenceUtil
								.getString(
										Constant.SHAREDREFERENCES_CONFIG,
										Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_THEME,
										OilchemApplication
												.getResourceString(R.string.taber_setting_theme_close)));
		mTaberPushSound
				.initTaber(
						OilchemApplication
								.getResourceString(R.string.taber_setting_theme),
						SharedPreferenceUtil
								.getString(
										Constant.SHAREDREFERENCES_CONFIG,
										Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_PUSHSOUND,
										OilchemApplication
												.getResourceString(R.string.taber_setting_theme_open)));
		mTaberPushVibrate
				.initTaber(
						OilchemApplication
								.getResourceString(R.string.taber_setting_theme),
						SharedPreferenceUtil
								.getString(
										Constant.SHAREDREFERENCES_CONFIG,
										Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_PUSHVIBRATE,
										OilchemApplication
												.getResourceString(R.string.taber_setting_theme_open)));
		mTaberTextSize
				.initTaber(
						OilchemApplication
								.getResourceString(R.string.taber_setting_fontsize),
						SharedPreferenceUtil
								.getString(
										Constant.SHAREDREFERENCES_CONFIG,
										Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_FONTSIZE,
										OilchemApplication
												.getResourceString(R.string.taber_setting_fontsize_big)));
		mTaberAdapterMode
				.initTaber(
						OilchemApplication
								.getResourceString(R.string.taber_setting_adptermode),
						SharedPreferenceUtil
								.getString(
										Constant.SHAREDREFERENCES_CONFIG,
										Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_ADAPTERMODE,
										OilchemApplication
												.getResourceString(R.string.taber_setting_adptermode_time)));
		mTaberTheme.setListener(this);
		mTaberTextSize.setListener(this);
		mTaberAdapterMode.setListener(this);
		mTaberPushSound.setListener(this);
		mTaberPushVibrate.setListener(this);
		mTextCache.setOnClickListener(this);
		mTextUpgrade.setOnClickListener(this);
		mTextAbout.setOnClickListener(this);
		mTextSuggesiton.setOnClickListener(this);
		mTextClear.setOnClickListener(this);
		mTextUpgrade.setText(String.format(mTextUpgrade.getText().toString(),
				OilchemApplication.getVersionName()));
		if (OilchemApplication.isLogined()) {
			mTextUsername.setText(OilchemApplication.getUser().getUsername());
		} else {
			mLayoutUsername.setVisibility(View.GONE);
			mTextLogout.setVisibility(View.GONE);
		}
		mTextLogout.setOnClickListener(this);
		initTitlebar();
		// 判断用的是versionName
		HandlerParams params = ApiUtil.getInstance().initUpgrade(
				OilchemApplication.getVersionName());
		updagradeHandler = HandlerFactory.getHandler(params, this);
		adapterModeChanged = false;
	}

	private void initTitlebar() {
		titlebar.setTitle(this,
				OilchemApplication.getResourceString(R.string.title_setting));
		titlebar.setLeft(this, TitleBar.ACTION_BACK);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (null != mTextCache) {
			mTextCache.setText(SharedPreferenceUtil.getString(
					Constant.SHAREDREFERENCES_CONFIG,
					Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_CACHETIME,
					OilchemApplication
							.getResourceString(R.string.cachetime_forever)));
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {//清除缓存
		case R.id.activity_setting_text_clearcache:
			if (DatabaseUtil.clearExpiredData(true) >= 0) {
				OilUtil.showToast(R.string.toast_clear_ok);
			} else {
				OilUtil.showToast(R.string.toast_clear_error);
			}
			break;
		case R.id.activity_setting_text_logout: //注销
			HandlerParams params = ApiUtil.getInstance().initLogout();
			logoutHandler = HandlerFactory.getHandler(params, this);
			ApiUtil.getInstance().sendRequest(logoutHandler);
			break;
		case R.id.activity_setting_text_cache:
			if (this == null) {
				return;
			}
			final AlertDialog dialog = new AlertDialog.Builder(
					new ContextThemeWrapper(this, R.style.OilDialog)).create();
			dialog.setTitle(OilchemApplication
					.getResourceString(R.string.cachetime_title));
			LinearLayout layout = (LinearLayout) View.inflate(this,
					R.layout.dialog_cachetime, null);
			dialog.setView(layout);
			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
			p.gravity = Gravity.CENTER;
			for (int i = 0; i < 4; i++) {
				TextView text = new TextView(this);
				text.setLayoutParams(p);
				text.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (v instanceof TextView) {
							SharedPreferenceUtil
									.setString(
											Constant.SHAREDREFERENCES_CONFIG,
											Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_CACHETIME,
											((TextView) v).getText().toString());
							if (dialog.isShowing()) {
								dialog.dismiss();
							}
							if (null != mTextCache) {
								mTextCache.setText(SharedPreferenceUtil
										.getString(
												Constant.SHAREDREFERENCES_CONFIG,
												Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_CACHETIME,
												OilchemApplication
														.getResourceString(R.string.cachetime_forever)));
							}
						}
					}
				});
				text.setTextSize(20);
				text.setGravity(Gravity.CENTER);
				switch (i) {
				case 0:
					text.setText(R.string.cachetime_forever);
					break;
				case 1:
					text.setText(R.string.cachetime_four);
					break;
				case 2:
					text.setText(R.string.cachetime_three);
					break;
				case 3:
					text.setText(R.string.cachetime_two);
					break;
				}
				layout.addView(text);
				dialog.show();
			}
			break;
		case R.id.activity_setting_text_about:
			Intent intent1 = new Intent(this, WebViewActivity.class);
			intent1.putExtra(WebViewActivity.PARAMS_URL,
					OilchemApiClient.BASE_URL + "/html/about.do?accessToken="
							+ OilchemApplication.getUser().getAccessToken());
			startActivity(intent1);
			break;
		case R.id.activity_setting_text_suggestion:
			Intent intent2 = new Intent(this, WebViewActivity.class);
			intent2.putExtra(WebViewActivity.PARAMS_URL,
					OilchemApiClient.BASE_URL
							+ "/html/suggestion.do?accessToken="
							+ OilchemApplication.getUser().getAccessToken());
			startActivity(intent2);
			break;
		case R.id.activity_setting_text_upgrade:
			ApiUtil.getInstance().sendRequest(updagradeHandler);
			break;
		}
	}

	@Override
	public void onRequestSuccess(RequestMethod method, API api,
			OilResponseData response, HandlerBase handler) {
		switch (api) {
		case API_UPGRADE:
			if (updagradeHandler == handler) {
				if (this == null) {
					return;
				}
				final DataUpgrade data = (DataUpgrade) response;
				if (data.updatable()) {
					builder = new AlertDialog.Builder(SettingActivity.this)
							.setTitle(
									OilchemApplication
											.getResourceString(R.string.dialog_getanupgrade))
							.setPositiveButton(R.string.dialog_ok,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											Intent intent = new Intent();
											intent.setAction("android.intent.action.VIEW");
											Uri content_url = Uri.parse(data
													.getDownloadUrl());
											intent.setData(content_url);
											startActivity(intent);
										}
									})
							.setNegativeButton(R.string.dialog_cancel,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
										}
									});
					if (!this.isFinishing()) {
						dialog = builder.create();
						dialog.show();
					}
				} else {
					OilUtil.showToast(R.string.toast_latestversion);
				}
			}
			break;
		case API_LOGOUT:
			if (logoutHandler == handler) {
				DataLogout logoutData = (DataLogout) response;
				if (logoutData.logoutSuccessful()) {
					OilchemApplication.exitUser();
					DatabaseUtil.clearDatabaseUtilInstance();
					Intent intent = new Intent(this, MainActivity.class);
					startActivity(intent);
					this.finish();
				}
				OilUtil.showToast(R.string.toast_logout);
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	@Override
	public void onRequestError(RequestMethod method, API api,
			HandlerBase handler) {
		switch (api) {
		case API_LOGOUT:
			OilUtil.showToast(R.string.toast_logout_error);
			break;
		case API_ACCESSTOKAN_ERROR:
			//accesstToken验证失败
			if(OilchemApplication.isLogined()){
				Intent intent=new Intent(SettingActivity.this,ErrorDialog.class);
				startActivity(intent);
			}
				
			break;
		default:
			break;
		}
	}

	@Override
	public void onTaberSelected(TextView v, String selectedTaberName) {
		if (mTaberTheme.inThisTaber(v)) {
			SharedPreferenceUtil.setString(Constant.SHAREDREFERENCES_CONFIG,
					Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_THEME,
					selectedTaberName);
			UIUtil.setChangedTheme(true);
			if (UIUtil.isChangedTheme(this.getClass().getSimpleName())) {
				this.finish();
				startActivity(new Intent(this, this.getClass()));
			}
		} else if (mTaberTextSize.inThisTaber(v)) {
			SharedPreferenceUtil.setString(Constant.SHAREDREFERENCES_CONFIG,
					Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_FONTSIZE,
					selectedTaberName);
		} else if (mTaberPushSound.inThisTaber(v)) {
			SharedPreferenceUtil.setString(Constant.SHAREDREFERENCES_CONFIG,
					Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_PUSHSOUND,
					selectedTaberName);
		} else if (mTaberPushVibrate.inThisTaber(v)) {
			SharedPreferenceUtil.setString(Constant.SHAREDREFERENCES_CONFIG,
					Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_PUSHVIBRATE,
					selectedTaberName);
		} else if (mTaberAdapterMode.inThisTaber(v)) {
			SharedPreferenceUtil.setString(Constant.SHAREDREFERENCES_CONFIG,
					Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_ADAPTERMODE,
					selectedTaberName);
			adapterModeChanged = true;
		}
	}

}
