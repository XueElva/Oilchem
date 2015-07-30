package net.oilchem.communication.sms.activity;

import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.data.model.DataLogout;
import net.oilchem.communication.sms.data.model.OilResponseData;
import net.oilchem.communication.sms.handler.HandlerBase;
import net.oilchem.communication.sms.handler.HandlerFactory;
import net.oilchem.communication.sms.handler.HandlerParams;
import net.oilchem.communication.sms.handler.IRequestListener;
import net.oilchem.communication.sms.util.ApiUtil;
import net.oilchem.communication.sms.util.OilUtil;
import net.oilchem.communication.sms.util.IApi.API;
import net.oilchem.communication.sms.util.IApi.RequestMethod;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class ErrorDialog extends Activity implements IRequestListener{
Button confirm;
private HandlerBase logoutHandler;
@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.accesstoken_error_dialog);
		
		confirm=(Button) findViewById(R.id.confirm);
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HandlerParams params = ApiUtil.getInstance().initLogout();
				logoutHandler = HandlerFactory.getHandler(params, ErrorDialog.this);
				ApiUtil.getInstance().sendRequest(logoutHandler);
			
			}
		});
	}
@Override
public void onRequestSuccess(RequestMethod method, API api,
		OilResponseData response, HandlerBase handler) {
	
	DataLogout logoutData = (DataLogout) response;
	if (logoutData.logoutSuccessful()) {
		OilchemApplication.exitUser();
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		this.finish();
	}
	
	
}
@Override
public void onRequestError(RequestMethod method, API api, HandlerBase handler) {
	Toast.makeText(getApplicationContext(), "注销失败！", 1).show();
	
}
}
