package net.oilchem.communication.sms.activity;

import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.adapter.NotificationCategoriesAdapter;
import net.oilchem.communication.sms.data.model.DataCategories;
import net.oilchem.communication.sms.data.model.DataConfig.OilSMSCategory;
import net.oilchem.communication.sms.data.model.OilResponseData;
import net.oilchem.communication.sms.handler.HandlerBase;
import net.oilchem.communication.sms.handler.HandlerFactory;
import net.oilchem.communication.sms.handler.HandlerParams;
import net.oilchem.communication.sms.handler.IRequestListener;
import net.oilchem.communication.sms.service.PullNotificationService;
import net.oilchem.communication.sms.util.ApiUtil;
import net.oilchem.communication.sms.util.IApi.API;
import net.oilchem.communication.sms.util.IApi.RequestMethod;
import net.oilchem.communication.sms.view.TitleBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NotificationManageActivity extends OilActivityBase implements IRequestListener, OnClickListener {
	private ListView categoriesList;
	private NotificationCategoriesAdapter mAdapter;
	private RelativeLayout mFooter;
	private TitleBar titlebar;
	private TextView mContacter, mBatchOpen, mBatchClose;
	private HandlerBase categoriesHandler, notiChangeHandler;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_categories_manage);
		categoriesList = (ListView) findViewById(R.id.activity_setting_list_categories);
		titlebar = (TitleBar) findViewById(R.id.activity_categoriesmanage_titlebar);
		mFooter = (RelativeLayout) findViewById(R.id.activity_setting_footer);
		mContacter = (TextView) findViewById(R.id.activity_setting_footer_contacter);
		mBatchOpen = (TextView) findViewById(R.id.activity_setting_batch_open);
		mBatchClose = (TextView) findViewById(R.id.activity_setting_batch_close);
		mBatchOpen.setOnClickListener(this);
		mBatchClose.setOnClickListener(this);
		if (null != OilchemApplication.getConfig().getConfig()) {
			mContacter.setText(String.format("%s %s", OilchemApplication.getConfig().getConfig().getCustomerServiceName(), OilchemApplication.getConfig().getConfig().getCustomerServiceNumber()));
		}
		mFooter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					String number = OilchemApplication.getConfig().getConfig().getCustomerServiceNumber().replaceAll("-", "");
					Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number));
					NotificationManageActivity.this.startActivity(intent);
				} catch(Exception e) {
				}
			}
		});
		mAdapter = new NotificationCategoriesAdapter(this, OilchemApplication.getConfig());
		categoriesList.setAdapter(mAdapter);
		categoriesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				//添加分类列表指向信息列表页跳转，查询参数为分类名，本地查询，添加from_category参数
				OilSMSCategory o = (OilSMSCategory) parent.getItemAtPosition(position);
				String cat = o.getName();
				Intent smsListIntent = new Intent(v.getContext(), OilchemSmsListActivity.class);
				smsListIntent.putExtra(OilchemSmsListActivity.PARAMS_QUERY, cat);
				smsListIntent.putExtra(OilchemSmsListActivity.PARAMS_LOCAL, true);
				smsListIntent.putExtra(OilchemSmsListActivity.PARAMS_FROM_CATEGORY, true);
				startActivity(smsListIntent);
			}
		});
		initTitlebar();
		doRefresh();
	}
	
	private void initTitlebar() {
		titlebar.setTitle(this, OilchemApplication.getResourceString(R.string.title_welcome));
//        titlebar.setTwoTitle(this, OilchemApplication.getResourceString(R.string.title_welcome), OilchemApplication.getResourceString(R.string.title_shangji));
		titlebar.setLeft(this, TitleBar.ACTION_BACK);
		titlebar.setRightListener(this, R.drawable.refresh, new OnClickListener() {
			@Override
			public void onClick(View view) {
				doRefresh();
			}
		});
	}

	protected void doRefresh() {
		HandlerParams params = ApiUtil.getInstance().initGetCategories();
		categoriesHandler = HandlerFactory.getHandler(params, NotificationManageActivity.this);
		ApiUtil.getInstance().sendRequest(categoriesHandler);		
	}

	@Override
	public void onRequestSuccess(RequestMethod method, API api,
			OilResponseData response, HandlerBase handler) {
		switch(api) {
		case API_GET_CATEGORIES:
			if (categoriesHandler == handler) {
				DataCategories data = (DataCategories) response;
				if (null != OilchemApplication.getConfig().getConfig()) {
					OilchemApplication.getConfig().getConfig().setCategories(data.getCategories());
					OilchemApplication.setConfig(OilchemApplication.getConfig());
				}
				mAdapter.setData(data.getCategories());
			}
			break;
		case API_NOTIFICATION_CHANGE:
			if (notiChangeHandler == handler) {
				doRefresh();
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
			Intent intent=new Intent(NotificationManageActivity.this,ErrorDialog.class);
			startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {
		String pushableString = "";
		switch(v.getId()){
		case R.id.activity_setting_batch_open:
			pushableString = "1";
			break;
		case R.id.activity_setting_batch_close:
			pushableString = "0";
			break;
		default:
			return;
		}
		//批量开关只需要发送分类ID为0即可
		HandlerParams params = ApiUtil.getInstance().initNotificationChange("0", pushableString);
		notiChangeHandler = HandlerFactory.getHandler(params, this, true);
		ApiUtil.getInstance().sendRequest(notiChangeHandler);
	}
}
