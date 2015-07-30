package net.oilchem.communication.sms.activity;

import net.oilchem.communication.sms.Constant;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.data.model.DataPhoneCode;
import net.oilchem.communication.sms.data.model.DataRegister;
import net.oilchem.communication.sms.data.model.OilResponseData;
import net.oilchem.communication.sms.handler.BinaryHandlerBase;
import net.oilchem.communication.sms.handler.HandlerBase;
import net.oilchem.communication.sms.handler.HandlerFactory;
import net.oilchem.communication.sms.handler.HandlerParams;
import net.oilchem.communication.sms.handler.IBinaryRequestListener;
import net.oilchem.communication.sms.handler.IRequestListener;
import net.oilchem.communication.sms.util.ApiUtil;
import net.oilchem.communication.sms.util.SharedPreferenceUtil;
import net.oilchem.communication.sms.util.IApi.API;
import net.oilchem.communication.sms.util.IApi.RequestMethod;
import net.oilchem.communication.sms.util.OilUtil;
import net.oilchem.communication.sms.view.TitleBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends OilActivityBase implements OnClickListener, IRequestListener, IBinaryRequestListener {

	private TitleBar titlebar;
	private TextView mDoPhoneCode, mDoRegister, mTextFooter;
	private EditText mEditTel, mEditAuthCode, mEditPassword;
	private HandlerBase phonecodeHandler, registerHandler;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_register);
		mDoPhoneCode = (TextView) findViewById(R.id.activity_register_text_phonecode_get);
		mDoRegister = (TextView) findViewById(R.id.activity_register_text_register_submit);
		mTextFooter = (TextView) findViewById(R.id.activity_register_text_footer);
		titlebar = (TitleBar) findViewById(R.id.activity_register_titlebar);
		mEditTel = (EditText) findViewById(R.id.activity_register_edit_telnum);
		mEditAuthCode = (EditText) findViewById(R.id.activity_register_edit_authcode);
		mEditPassword = (EditText) findViewById(R.id.activity_register_edit_password);
		mDoPhoneCode.setOnClickListener(this);
		mDoRegister.setOnClickListener(this);
		OilchemApplication.initFooter(this, mTextFooter);
		initTitlebar();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void initTitlebar() {
		titlebar.setTitle(this, OilchemApplication.getResourceString(R.string.title_register));
		titlebar.setLeft(this, TitleBar.ACTION_BACK);
	}
	
	@Override
	public void onRequestSuccess(RequestMethod method, API api,
			OilResponseData response, HandlerBase handler) {
		switch(api) {
		case API_REGISTER:
			if (registerHandler == handler) {
				DataRegister data = (DataRegister) response;
				String regResult = data.getRegister();
				if (null != regResult && "1".equals(regResult)) {
					//保存注册成功的用户名
					SharedPreferenceUtil.setString(Constant.SHAREDREFERENCES_CONFIG, Constant.SHAREDREFERENCES_LOGIN_SAVEUSER, mEditTel.getText().toString());
					Intent intent = new Intent(this, LoginActivity.class);
					startActivity(intent);
					this.finish();
				}
				OilUtil.showToast(data.getMessage());
			}
			break;
		case API_PHONECODE:
			if (phonecodeHandler == handler) {
				DataPhoneCode data = (DataPhoneCode) response;
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

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.activity_register_text_register_submit:
			String tel = mEditTel.getText().toString();
			String authCode = mEditAuthCode.getText().toString();
			String password = mEditPassword.getText().toString();
			if (TextUtils.isEmpty(tel) || TextUtils.isEmpty(authCode) || TextUtils.isEmpty(password)) {
				OilUtil.showToast(R.string.toast_tel_authcode_should_not_empty);
				break;
			}
			HandlerParams regParams = ApiUtil.getInstance().initRegister(tel, authCode, password);
			registerHandler = HandlerFactory.getHandler(regParams, this);
			ApiUtil.getInstance().sendRequest(registerHandler);
			break;
		case R.id.activity_register_text_phonecode_get:
			String telnumber = mEditTel.getText().toString();
			if (TextUtils.isEmpty(telnumber)) {
				OilUtil.showToast(R.string.toast_tel_should_not_empty);
				break;
			}
			HandlerParams phoneParams = ApiUtil.getInstance().initPhoneCode(telnumber);
			phonecodeHandler = HandlerFactory.getHandler(phoneParams, this);
			ApiUtil.getInstance().sendRequest(phonecodeHandler);
			break;
		default :
			break;
		}
	}

	@Override
	public void onBinaryRequestSuccess(byte[] bytes, BinaryHandlerBase handler) {
	}
}
