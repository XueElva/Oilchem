package net.oilchem.communication.sms.activity;

import net.oilchem.communication.sms.Constant;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.data.OilchemContract;
import net.oilchem.communication.sms.data.OilchemDbHelper;
import net.oilchem.communication.sms.data.model.DataLogin;
import net.oilchem.communication.sms.data.model.DataUpgrade;
import net.oilchem.communication.sms.data.model.OilResponseData;
import net.oilchem.communication.sms.handler.HandlerBase;
import net.oilchem.communication.sms.handler.HandlerFactory;
import net.oilchem.communication.sms.handler.HandlerParams;
import net.oilchem.communication.sms.handler.IRequestListener;
import net.oilchem.communication.sms.util.ApiUtil;
import net.oilchem.communication.sms.util.IApi.API;
import net.oilchem.communication.sms.util.IApi.RequestMethod;
import net.oilchem.communication.sms.util.DatabaseUtil;
import net.oilchem.communication.sms.util.OilUtil;
import net.oilchem.communication.sms.util.SharedPreferenceUtil;
import net.oilchem.communication.sms.view.TitleBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends OilActivityBase implements IRequestListener, OnClickListener {

	private TitleBar titlebar;
	private TextView mTextDoLogin, mRegister, mTextFooter;
	private EditText mEditUsername, mEditPassword;
	private HandlerBase loginHandler;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		//测试用异常生成
		/*String str = null;
		str.equals("abc");*/
		
		setContentView(R.layout.activity_login);
		mTextDoLogin = (TextView) findViewById(R.id.activity_main_text_login);
		mRegister = (TextView) findViewById(R.id.activity_login_text_register);
		mTextFooter = (TextView) findViewById(R.id.activity_login_text_footer);
		mEditUsername = (EditText) findViewById(R.id.activity_login_text_username);
		//填写保存的用户名
		mEditUsername.setText(SharedPreferenceUtil.getString(Constant.SHAREDREFERENCES_CONFIG, Constant.SHAREDREFERENCES_LOGIN_SAVEUSER));
		mEditPassword = (EditText) findViewById(R.id.activity_login_text_password);
		titlebar = (TitleBar) findViewById(R.id.activity_login_titlebar);
		mTextDoLogin.setOnClickListener(this);
		OilchemApplication.initFooter(this, mTextFooter);
		mRegister.setOnClickListener(this);
		initTitlebar();
	}

	private void initTitlebar() {
		titlebar.setTitle(this, OilchemApplication.getResourceString(R.string.title_login));
		titlebar.setLeft(this, TitleBar.ACTION_BACK);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.activity_main_text_login:
			String username = mEditUsername.getText().toString();
			String password = OilUtil.getMD5(mEditPassword.getText().toString());
			if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
				OilUtil.showToast(R.string.toast_username_password_should_not_empty);
				break;
			}
			HandlerParams loginParams = ApiUtil.getInstance().initLogin(username, password);
			loginHandler = HandlerFactory.getHandler(loginParams, this);
			ApiUtil.getInstance().sendRequest(loginHandler);
			break;
		case R.id.activity_login_text_register:
			Intent intent = new Intent(v.getContext(), RegisterActivity.class);
			startActivity(intent);
			this.finish();
			break;
		}
	}

	@Override
	public void onRequestSuccess(RequestMethod method, API api, OilResponseData response, HandlerBase handler) {
		switch(api) {
		case API_LOGIN:
			if (loginHandler == handler) {
				DataLogin data = (DataLogin) response;
				if (data.isLogined()) {
					//保存登录的用户名
					SharedPreferenceUtil.setString(Constant.SHAREDREFERENCES_CONFIG, Constant.SHAREDREFERENCES_LOGIN_SAVEUSER, mEditUsername.getText().toString());
				
					OilchemApplication.setUser(data);
					ApiUtil.getInstance().updateInstance();
					
					//初始化数据库
					OilchemDbHelper.DATABASE_NAME="oilchem_"+OilchemApplication.getUser().getUsername();
					DatabaseUtil.getInstance();
					
					Intent intent = new Intent(this, OilchemWelcomeActivity.class);
					intent.putExtra(OilchemWelcomeActivity.PARAMS_LOGINED, true);
					startActivity(intent);
					this.finish();
				}
				OilUtil.showToast(data.getMessage());
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
