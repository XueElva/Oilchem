package net.oilchem.communication.sms.activity;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.oilchem.communication.sms.Constant;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.adapter.SmsListAdapter;
import net.oilchem.communication.sms.adapter.SmsListCategorierExpandableAdapter;
import net.oilchem.communication.sms.adapter.SmsListExpandableAdapter;
import net.oilchem.communication.sms.data.OilchemContract;
import net.oilchem.communication.sms.data.model.DataConfig;
import net.oilchem.communication.sms.data.model.DataSmsList;
import net.oilchem.communication.sms.data.model.DataUpgrade;
import net.oilchem.communication.sms.data.model.OilResponseData;
import net.oilchem.communication.sms.data.model.SmsInfo;
import net.oilchem.communication.sms.handler.HandlerBase;
import net.oilchem.communication.sms.handler.HandlerFactory;
import net.oilchem.communication.sms.handler.HandlerParams;
import net.oilchem.communication.sms.handler.IRequestListener;
import net.oilchem.communication.sms.service.PullNotificationService;
import net.oilchem.communication.sms.util.ApiUtil;
import net.oilchem.communication.sms.util.DatabaseUtil;
import net.oilchem.communication.sms.util.IApi.API;
import net.oilchem.communication.sms.util.IApi.RequestMethod;
import net.oilchem.communication.sms.util.OilUtil;
import net.oilchem.communication.sms.util.SharedPreferenceUtil;
import net.oilchem.communication.sms.util.XmlUtil;
import net.oilchem.communication.sms.view.SearchBar;
import net.oilchem.communication.sms.view.SmsListTitleBar;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;

public class OilchemWelcomeActivity extends OilActivityBase implements
		IRequestListener, OnClickListener {
	public static String todayTimeStamp; // 今天的时间戳
	public static final String PARAMS_PUSH_DATA = "push_data";
	public static final String PARAMS_LOGINED = "push_data";
	private int loadPosition = -1;
	private int expandGroup = 0;
	private PullToRefreshExpandableListView mExpandableListView;
	private BaseExpandableListAdapter mAdapter;
	private SmsListTitleBar titlebar;
	private SearchBar searchBar;
	private HandlerBase smsListHandler;
	private HandlerBase configHandler;
	private String offisialLastTs;
	private ListView dialogListView;
	private SmsListAdapter dialogAdapter;
	private DataSmsList data;
	private boolean justLogined;
	private boolean mClassifyByName; // 按组名分类
	private HandlerBase updagradeHandler;
	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	private AlertDialog progressDialog;
	private TextView loadingMessage;

	private SharedPreferences sp;
	private Editor editor;
	private boolean[] refreshed; // 前六天是否已刷新

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_welcome);

		data = new DataSmsList();
		todayTimeStamp = getTime(0);

		sp = getSharedPreferences("refreshTime", MODE_PRIVATE);
		editor = sp.edit();
		String[] time = new String[6];
		time[0] = sp.getString("lastRefreshTime0", ""); // 昨天组刷新的时间戳
		time[1] = sp.getString("lastRefreshTime1", ""); // 前天组
		time[2] = sp.getString("lastRefreshTime2", ""); // 大前天组
		time[3] = sp.getString("lastRefreshTime3", ""); // ..
		time[4] = sp.getString("lastRefreshTime4", ""); // ..
		time[5] = sp.getString("lastRefreshTime5", ""); // ..

		refreshed = new boolean[6];

		// 判断各个组今日是否已刷新
		for (int i = 0; i < time.length; i++) {
			if (time[i].equals("")
					|| Long.parseLong(time[i]) < Long.parseLong(todayTimeStamp)) {
				// 该组今日未刷新
				refreshed[i] = false;
			} else {
				refreshed[i] = true;
			}
		}

		handlePush(getIntent());

		mExpandableListView = (PullToRefreshExpandableListView) findViewById(R.id.activity_welcome_expandablelistview);

		// 下拉刷新
		mExpandableListView
				.setOnRefreshListener(new OnRefreshListener<ExpandableListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ExpandableListView> arg) {

						loadWelcomeMessages(todayTimeStamp);
						loadPosition = -1;
						expandGroup = loadPosition + 1;
					}
				});

		// 如果该组下没有数据的话，点击获取数据
		mExpandableListView.getRefreshableView().setOnGroupClickListener(
				new OnGroupClickListener() {

					@Override
					public boolean onGroupClick(ExpandableListView parent,
							View v, final int groupPosition, long id) {

						if (groupPosition > 0
								&& !mExpandableListView.getRefreshableView()
										.isGroupExpanded(groupPosition) && !mClassifyByName) {
							if (mAdapter.getChildrenCount(groupPosition) == 0) {
								// 没有数据
								final String date = getDate(groupPosition);
								AlertDialog.Builder builder = new AlertDialog.Builder(
										OilchemWelcomeActivity.this);
								builder.setTitle("提示");
								builder.setMessage("是否加载" + date + "的短讯?");
								builder.setPositiveButton("加载",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												loadWelcomeMessages(getTime(groupPosition));
												loadPosition = groupPosition - 1;
												expandGroup = loadPosition + 1;
												loadingMessage.setText("正在加载"
														+ date + "的短讯...");
												progressDialog.show();
											}
										});

								builder.setNegativeButton("取消",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub

											}
										});

								builder.create();
								builder.show();

							} else if (((SmsListExpandableAdapter) mAdapter)
									.isOnlyCollectedMessages(getTime(groupPosition))) {
								// 只有收藏的数据
								loadWelcomeMessages(getTime(groupPosition));

								loadPosition = groupPosition - 1;
								expandGroup = loadPosition + 1;
							} else if (groupPosition <= 6
									&& !refreshed[groupPosition - 1]) {
								// 该组今日未刷新
								loadWelcomeMessages(((SmsListExpandableAdapter) mAdapter)
										.getLastRefreshTime(getTime(groupPosition)));

								loadPosition = groupPosition - 1;
								expandGroup = loadPosition + 1;
							}
						}
						return false;
					}
				});

		titlebar = (SmsListTitleBar) findViewById(R.id.activity_welcome_titlebar);
		searchBar = (SearchBar) findViewById(R.id.activity_welcome_searchbar);
		searchBar.getSearchBtn().setOnClickListener(this);
		searchBar.getEdit().requestFocus(); // 将edittext 点击弹出键盘屏蔽掉
		offisialLastTs = SharedPreferenceUtil.getString(
				Constant.SHAREDREFERENCES_CONFIG,
				Constant.SHAREDREFERENCES_WELCOME_LASTTS);
		if (null != getIntent()) {
			justLogined = getIntent().getBooleanExtra(PARAMS_LOGINED, false);
		}
		if (justLogined) {
			// 判断用的是versionName
			HandlerParams params1 = ApiUtil.getInstance().initUpgrade(
					OilchemApplication.getVersionName());
			updagradeHandler = HandlerFactory.getHandler(params1, this);
			ApiUtil.getInstance().sendRequest(updagradeHandler);
		}
		initTitlebar();

		AlertDialog.Builder builder = new AlertDialog.Builder(
				OilchemWelcomeActivity.this);
		View dialogView = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.progress_dialog, null);
		loadingMessage = (TextView) dialogView
				.findViewById(R.id.loadingMessage);
		builder.setView(dialogView);
		progressDialog = builder.create();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				loadWelcomeMessages(todayTimeStamp);// 今天
				loadPosition = -1;
				expandGroup = loadPosition + 1;
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d("push",
				"handle intent"
						+ intent.getSerializableExtra(OilchemSmsListActivity.PARAMS_PUSH_DATA));
		handlePush(intent);
	}

	private void handlePush(Intent intent) {
		// if(getIntent() != null) {
		// DataSmsList pushData = (DataSmsList)
		// getIntent().getSerializableExtra(PARAMS_PUSH_DATA);
		// if (null != pushData && pushData.getMessages() != null &&
		// pushData.getMessages().size() > 0) {
		// Dialog dialog = new AlertDialog.Builder(this).show();
		// dialog.setContentView(R.layout.dialog_push);
		// dialogListView = (ListView)
		// dialog.findViewById(R.id.dialog_push_listview);
		// dialogAdapter = new SmsListAdapter(this);
		// dialogAdapter.setData(pushData.getMessages());
		// dialogListView.setAdapter(dialogAdapter);
		// }
		// }
		if (intent != null
				&& intent.hasExtra(OilchemSmsListActivity.PARAMS_PUSH_DATA)) {
			Log.d("value", "handlePush");
			Intent intent1 = new Intent(this, OilchemSmsListActivity.class);
			intent1.putExtra(
					OilchemSmsListActivity.PARAMS_PUSH_DATA,
					intent.getSerializableExtra(OilchemSmsListActivity.PARAMS_PUSH_DATA));
			startActivityForResult(intent1, 1);
		}
	}

	private void initRequestConfig() {
		if (justLogined
				|| null == OilchemApplication.getConfig().getConfig()
				|| null == OilchemApplication.getConfig().getConfig()
						.getCategories()) {
			HandlerParams params = ApiUtil.getInstance().initConfig();
			configHandler = HandlerFactory.getHandler(params, this);
			ApiUtil.getInstance().sendRequest(configHandler);
		}
	}

	/**
	 * 加载咨询
	 * 
	 * @param day
	 *            0：今天 1:昨天...
	 */
	private void loadWelcomeMessages(String time) {
		HandlerParams handlerParams;

		if (TextUtils
				.equals(SharedPreferenceUtil
						.getString(
								Constant.SHAREDREFERENCES_CONFIG,
								Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_ADAPTERMODE,
								OilchemApplication
										.getResourceString(R.string.taber_setting_adptermode_time)),
						OilchemApplication
								.getResourceString(R.string.taber_setting_adptermode_category))) {
			// 按组名分类
			mClassifyByName = true;
			offisialLastTs = SharedPreferenceUtil.getString(
					Constant.SHAREDREFERENCES_CONFIG,
					Constant.SHAREDREFERENCES_WELCOME_LASTTS);
			Log.d("value","按组名");
			handlerParams = ApiUtil.getInstance().initWelcome(offisialLastTs);
		} else {
			Log.d("value","按时间");
			// 按天分类
			mClassifyByName = false;
			// 刷新该天
			offisialLastTs = time;
			handlerParams = ApiUtil.getInstance().initWelcomeByDay(offisialLastTs);
		}

		

		smsListHandler = HandlerFactory.getHandler(handlerParams, this);
		ApiUtil.getInstance().sendRequest(smsListHandler);
	}

	/**
	 * 获得指定时间的时间戳
	 */
	private String getTime(int day) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		String date2 = df.format(new Date()) + " 00:00:00.000";
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date;
		long timestamp = 0;
		try {
			date = df2.parse(date2); // 今天
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int dayGap = day;
			cal.add(Calendar.DATE, -dayGap);
			timestamp = cal.getTimeInMillis();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return String.valueOf(timestamp);
	}

	private String getDate(int position) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int dayGap = position;
		cal.add(Calendar.DATE, -dayGap);
		Date date = cal.getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");// 设置日期格式
		return df.format(date);
	}

	// String shangJiId = "'1064827'";
	String shangJiId = "'100','101','102'";

	static boolean isLeft = true;

	private void initTitlebar() {
		// titlebar.setTitle(this,
		// OilchemApplication.getResourceString(R.string.title_welcome));
		titlebar.setTwoTitle(this,
				OilchemApplication.getResourceString(R.string.title_welcome),
				OilchemApplication.getResourceString(R.string.title_shangji));
		titlebar.setLeftTitleListener(this, new OnClickListener() {
			@Override
			public void onClick(View view) {
				titlebar.getTitle_btn_left().setBackgroundResource(
						R.drawable.smsbutton_bg);
				titlebar.getTitle_btn_left().setTextColor(Color.WHITE);
				// titlebar.getTitle_btn_left().setBackgroundColor(Color.GRAY);
				titlebar.getTitle_btn_right().setTextColor(Color.BLACK);
				titlebar.getTitle_btn_right().setBackgroundColor(Color.WHITE);
				long timeFloat = 0;
				if (!TextUtils.isEmpty(offisialLastTs)) {
					timeFloat = getFloorSevenDateFloat(offisialLastTs);
				}
				//短讯
				ArrayList<SmsInfo> smsList = DatabaseUtil 
						.getInstance()
						.query(" WHERE "
								+ String.format(
										"%s >= '%s' and %s not in ( %s) ",
										OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS,
										timeFloat,
										OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_GROUPID,
										shangJiId));
				if (null != OilchemWelcomeActivity.this.data) {
					OilchemWelcomeActivity.this.data.setMessages(smsList);
				}

				isLeft = true;
				initData(data);
			}
		});
		titlebar.setRightTitleListener(this, new OnClickListener() {
			@Override
			public void onClick(View view) {
				// Toast.makeText(OilchemWelcomeActivity.this, "隆众商机",
				// Toast.LENGTH_SHORT).show();
				titlebar.getTitle_btn_left().setTextColor(Color.BLACK);
				titlebar.getTitle_btn_left().setBackgroundColor(Color.WHITE);
				// titlebar.getTitle_btn_right().setBackgroundColor(Color.GRAY);
				titlebar.getTitle_btn_right().setBackgroundResource(
						R.drawable.smsbutton_bg);
				titlebar.getTitle_btn_right().setTextColor(Color.WHITE);
				long timeFloat = 0;
				if (!TextUtils.isEmpty(offisialLastTs)) {
					timeFloat = getFloorSevenDateFloat(offisialLastTs);
				}
				ArrayList<SmsInfo> smsList = DatabaseUtil
						.getInstance()
						.query(" WHERE "
								+ String.format(
										"%s >= '%s' and %s in  (%s) ",
										OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS,
										timeFloat,
										OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_GROUPID,
										shangJiId));

				if (null != OilchemWelcomeActivity.this.data) {
					OilchemWelcomeActivity.this.data.setMessages(smsList);

				}
				isLeft = false;
				initData(data);
			}
		});
		// titlebar.getTitle_btn_left().setBackgroundColor(Color.GRAY);
		titlebar.getTitle_btn_left().setBackgroundResource(
				R.drawable.smsbutton_bg);
		titlebar.getTitle_btn_left().setTextColor(Color.WHITE);
		titlebar.getTitle_btn_right().setBackgroundColor(Color.WHITE);
		titlebar.getTitle_btn_right().setTextColor(Color.BLACK);
		isLeft = true;

		titlebar.setLeftListener(this, R.drawable.category,
				new OnClickListener() {// 消息设置界面
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(view.getContext(),
								NotificationManageActivity.class);
						startActivity(intent);
					}
				});
		titlebar.setRightListener(this, R.drawable.setting,
				new OnClickListener() {// 打开应用设置界面
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(view.getContext(),
								SettingActivity.class);
						startActivity(intent);
					}
				});
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
		if (!OilchemApplication.isLogined()) {
			this.finish();
		}
		if (SettingActivity.adapterModeChanged) {
			if (null != data) {
				initData(data);
			} else {
				mAdapter = null;
			}
			SettingActivity.adapterModeChanged = false;
		}
		if (null == mAdapter) {
			loadWelcomeMessages(todayTimeStamp);
			loadPosition = -1;
			expandGroup = loadPosition + 1;
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onRestart() {
		// 从数据库加载数据
		getDataFromDatabase();
		super.onRestart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onRequestSuccess(RequestMethod method, API api,
			OilResponseData response, HandlerBase handler) {
		switch (api) {
		case API_WELCOME_GET_BY_DAY:// 资讯获取完毕
		case API_WELCOME:
			if (smsListHandler == handler) {

				mExpandableListView.onRefreshComplete();
				DataSmsList data = (DataSmsList) response;

				if (loadPosition >= 0 && loadPosition <= 5) { // 前六天

					editor.putString("lastRefreshTime" + loadPosition,
							todayTimeStamp);
					editor.commit();
					refreshed[loadPosition] = true;
					if(progressDialog!=null){
						progressDialog.dismiss();
					}
					

					if (mClassifyByName) {
						SharedPreferenceUtil.setString(
								Constant.SHAREDREFERENCES_CONFIG,
								Constant.SHAREDREFERENCES_WELCOME_LASTTS,
								getFormatDateString(data.getTs()));
					}
					if (null == this.data || null == data.getMessages()) {
						break;
					}
 
					this.data.getMessages().addAll(data.getMessages());
				} else if (loadPosition == -1) { // 今天(只替换今天的消息)
					this.data = data;
				}
				DatabaseUtil.getInstance().insert(data.getMessages());

				// 从数据库取出来的数据
				getDataFromDatabase();

				initRequestConfig();
			}
			break;

		case API_UPGRADE:
			if (updagradeHandler == handler) {
				if (this == null) {
					return;
				}
				final DataUpgrade data = (DataUpgrade) response;
				if (data.updatable()) {
					builder = new AlertDialog.Builder(this)
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

	/**
	 * 从数据库取数据，收藏的优先
	 */
	public void getDataFromDatabase() {
		ArrayList<SmsInfo> dataList = new ArrayList<SmsInfo>();

		long timeFloat = 0;
		if (!TextUtils.isEmpty(todayTimeStamp)) {
			timeFloat = getFloorSevenDateFloat(todayTimeStamp);
		}

		ArrayList<String> collectedList = XmlUtil.getCollectedList();
		StringBuffer groupIdList = new StringBuffer();
		for (int i = 0; i < collectedList.size(); i++) {

			groupIdList.append("'");
			groupIdList.append(collectedList.get(i));
			if (i < collectedList.size() - 1) {
				groupIdList.append("',");
			} else {
				groupIdList.append("'");
			}

		}
		// 先取出已收藏的咨询
		String where = " WHERE "
				+ String.format(
						"%s >= '%s' and %s not in (%s) and %s in (%s)",
						OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS,
						timeFloat,
						OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_GROUPID,
						shangJiId,
						OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_GROUPID,
						groupIdList);
		if (!isLeft) {
			where = " WHERE "
					+ String.format(
							"%s >= '%s' and %s in  (%s) and %s in (%s)",
							OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS,
							timeFloat,
							OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_GROUPID,
							shangJiId,
							OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_GROUPID,
							groupIdList);
		}

		dataList = DatabaseUtil.getInstance().query(where);

		for (int i = 0; i < dataList.size(); i++) {
			dataList.get(i).setCollected(true);
		}
		// 再取出未收藏的
		String notColectedId;
		if (collectedList.size() > 0) {
			notColectedId = groupIdList.toString() + "," + shangJiId;
		} else {
			notColectedId = shangJiId;
		}

		String where2 = " WHERE "
				+ String.format(
						"%s >= '%s' and %s not in (%s)",
						OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS,
						timeFloat,
						OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_GROUPID,
						notColectedId);
		if (!isLeft) {
			where2 = " WHERE "
					+ String.format(
							"%s >= '%s' and %s in  (%s) and %s not in (%s)",
							OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS,
							timeFloat,
							OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_GROUPID,
							shangJiId,
							OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_GROUPID,
							groupIdList);
		}

		ArrayList<SmsInfo> dataListNotCollected = new ArrayList<SmsInfo>();
		dataListNotCollected = DatabaseUtil.getInstance().query(where2);

		for (int i = 0; i < dataListNotCollected.size(); i++) {
			dataListNotCollected.get(i).setCollected(false);
		}
		dataList.addAll(dataListNotCollected);
		data.setMessages(dataList);

		initData(data);

	}

	private String getFormatDateString(String ts) {
		if (!TextUtils.isEmpty(ts) && ts.length() == 13) {
			return ts;
		} else if (!TextUtils.isEmpty(ts) && ts.length() == 10) {
			return String.valueOf(Float.valueOf(ts) * 1000);
		}
		return ts;
	}

	@SuppressWarnings("finally")
	@SuppressLint("SimpleDateFormat")
	private long getFloorSevenDateFloat(String ts) {
		long time = 0;
		if (!TextUtils.isEmpty(ts) && ts.length() == 13) {
			time = Long.parseLong(ts);
		} else if (!TextUtils.isEmpty(ts) && ts.length() == 10) {
			time = Long.parseLong(ts) * 1000;
		}
		DateFormat dayFormater = new SimpleDateFormat("yyyy-MM-dd");
		String formatString = dayFormater.format(new Timestamp(
				(long) (time - 6 * 3600 * 24 * 1000)));
		try {
			time = dayFormater.parse(formatString).getTime();
		} catch (ParseException e) {
		} finally {
			return time;
		}
	}

	private void initData(DataSmsList data) {

		if (!isLeft) {//隆众商机都按时间分类
			mAdapter = new SmsListExpandableAdapter(this, data);
		} else if (TextUtils
				.equals(SharedPreferenceUtil
						.getString(
								Constant.SHAREDREFERENCES_CONFIG,
								Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_ADAPTERMODE,
								OilchemApplication
										.getResourceString(R.string.taber_setting_adptermode_time)),
						OilchemApplication
								.getResourceString(R.string.taber_setting_adptermode_time))) {
			//按时间分类
			mAdapter = new SmsListExpandableAdapter(this, data);
		} else if (TextUtils
				.equals(SharedPreferenceUtil
						.getString(
								Constant.SHAREDREFERENCES_CONFIG,
								Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_ADAPTERMODE,
								OilchemApplication
										.getResourceString(R.string.taber_setting_adptermode_time)),
						OilchemApplication
								.getResourceString(R.string.taber_setting_adptermode_category))) {
			// 按组名分类
			mAdapter = new SmsListCategorierExpandableAdapter(this, data);
		}
		mExpandableListView.getRefreshableView().setAdapter(mAdapter);

		if (expandGroup >= 0) {
			mExpandableListView.getRefreshableView().expandGroup(expandGroup);
		}

	}

	@Override
	public void onRequestError(RequestMethod method, API api,
			HandlerBase handler) {

		switch (api) {
		// case API_WELCOME:
		// case API_WELCOME_GET_BY_DAY:
		// if (loadPosition >= 0) { // 前六天
		// progressDialog.dismiss();
		// Toast.makeText(getApplicationContext(), "获取失败!", 1).show();
		// }
		// break;
		case API_ACCESSTOKAN_ERROR:
			// accesstToken验证失败
			if (OilchemApplication.isLogined()) {
				Intent intent = new Intent(OilchemWelcomeActivity.this,
						ErrorDialog.class);
				startActivity(intent);
			}

			break;
		default:
			break;
		}

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.view_searchbar_image_btn:
			Intent smsListIntent = new Intent(view.getContext(),
					OilchemSmsListActivity.class);
			smsListIntent.putExtra(OilchemSmsListActivity.PARAMS_QUERY,
					searchBar.getQuery());
			smsListIntent.putExtra(OilchemSmsListActivity.PARAMS_LOCAL, true);
			startActivity(smsListIntent);
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
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}
