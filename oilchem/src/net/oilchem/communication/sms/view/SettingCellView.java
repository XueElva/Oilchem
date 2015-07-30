package net.oilchem.communication.sms.view;

import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.data.model.DataChangeNotification;
import net.oilchem.communication.sms.data.model.DataConfig.OilSMSCategory;
import net.oilchem.communication.sms.data.model.OilResponseData;
import net.oilchem.communication.sms.handler.HandlerBase;
import net.oilchem.communication.sms.handler.HandlerFactory;
import net.oilchem.communication.sms.handler.HandlerParams;
import net.oilchem.communication.sms.handler.IRequestListener;
import net.oilchem.communication.sms.util.ApiUtil;
import net.oilchem.communication.sms.util.IApi.API;
import net.oilchem.communication.sms.util.IApi.RequestMethod;
import net.oilchem.communication.sms.view.TaberView.TaberSelectedListener;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingCellView extends LinearLayout implements TaberSelectedListener, IRequestListener {
	private TextView categoryName;
	private TaberView taber;
	private HandlerBase handler;

	public SettingCellView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_setting_category, SettingCellView.this);
		categoryName = (TextView) findViewById(R.id.view_setting_text_category);
		taber = (TaberView) findViewById(R.id.view_setting_view_taber);
		taber.setListener(this);
	}

	public void initView(OilSMSCategory oilSMSCategory) {
		categoryName.setText(oilSMSCategory.getName());
		if (!oilSMSCategory.pushable()) {
			taber.initTaber(OilchemApplication.getResourceString(R.string.taber_setting_category), OilchemApplication.getResourceString(R.string.taber_setting_close));
		} else {
			taber.initTaber(OilchemApplication.getResourceString(R.string.taber_setting_category), OilchemApplication.getResourceString(R.string.taber_setting_open));
		}
	}

	@Override
	public void onTaberSelected(TextView v, String selectedTaberName) {
		boolean pushable = TextUtils.equals(selectedTaberName, OilchemApplication.getResourceString(R.string.taber_setting_open));
		String pushableString = OilSMSCategory.getPushableForApi(pushable);
		if (TextUtils.equals(taber.getCurrentTab(), selectedTaberName)) {
			HandlerParams params = ApiUtil.getInstance().initNotificationChange(OilchemApplication.getGroupIdByCategoryName(categoryName.getText().toString()), pushableString);
			handler = HandlerFactory.getHandler(params, this, true);
			ApiUtil.getInstance().sendRequest(handler);
		}
	}

	@Override
	public void onRequestSuccess(RequestMethod method, API api,
			OilResponseData response, HandlerBase handler) {
		switch(api) {
		case API_NOTIFICATION_CHANGE: 
			if (this.handler == handler) {
				DataChangeNotification data = (DataChangeNotification) response;
				if (null != data.getCategories() && data.getCategories().size() > 0) {
					OilchemApplication.updateConfig(data.getCategories().get(0));
				}
			}
			break;
		default :
			break;
		}
	}

	@Override
	public void onRequestError(RequestMethod method, API api,
			HandlerBase handler) {
		
	}
}
