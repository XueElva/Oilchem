package net.oilchem.communication.sms.handler;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import net.oilchem.communication.sms.Constant;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.activity.MainActivity;
import net.oilchem.communication.sms.data.model.DataLogin;
import net.oilchem.communication.sms.data.model.DataRegister;
import net.oilchem.communication.sms.data.model.OilResponseData;
import net.oilchem.communication.sms.util.ApiUtil;
import net.oilchem.communication.sms.util.IApi.API;
import net.oilchem.communication.sms.util.JsonUtil;
import net.oilchem.communication.sms.util.OilUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class HandlerBase extends JsonHttpResponseHandler {

	private IRequestListener listener;
	private HandlerParams handlerParams;
	boolean processing;
	private Header[] headers;

	public Header[] getHeaders() {
		return headers;
	}

	public void setHeaders(Header[] headers) {
		this.headers = headers;
	}

	public boolean isProcessing() {
		return processing;
	}

	public void setProcessing(boolean processing) {
		this.processing = processing;
	}

	public HandlerParams getHandlerParams() {
		return handlerParams;
	}

	public void setHandlerParams(HandlerParams params) {
		this.handlerParams = params;
	}

	public void setIRequestListener(IRequestListener listener) {
		this.listener = listener;
	}

	public IRequestListener getIRequestListener() {
		return listener;
	}

	@Override
	public void onSuccess(JSONObject json) {
		JSONObject data = null;
		try {
			data = json.getJSONObject(Constant.API_PARAMS_DATA);
//			Log.d("value","data=="+data.toString());
			String message=data.getString("message");
			if(message.equals("accessToken验证失败")){
			//验证失败
				listener.onRequestError(handlerParams.getMethod(),
						API.API_ACCESSTOKAN_ERROR, this);
				return;
			}
			handleErrorMessage(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
			

		OilResponseData oilData = JsonUtil.getResponse(data.toString(),
				handlerParams.getApi());
		handleUpdateAccessToken(oilData);
	
		if (null != listener && data != null && oilData != null) {
			try {
				if (json.has(Constant.API_PARAMS_STATUS)
						&& "0".equals(json
								.getString(Constant.API_PARAMS_STATUS))) {
					listener.onRequestError(handlerParams.getMethod(),
							handlerParams.getApi(), this);
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			boolean hasLogin=data.has(Constant.API_RESPONSE_LOGIN);
			boolean instanceofDataLogin=oilData instanceof DataLogin;
			if (hasLogin && !instanceofDataLogin) {
				if (oilData instanceof DataRegister) {
					listener.onRequestSuccess(handlerParams.getMethod(),
							handlerParams.getApi(), oilData, this);
				} else {
					listener.onRequestError(handlerParams.getMethod(),
							handlerParams.getApi(), this);
					// OilUtil.showToast("accessToken验证失败");
					// if((OilchemApplication.getContextFromApplication()
					// instanceof MainActivity)){
					OilchemApplication.exitUser();
					Intent intent = new Intent(
							OilchemApplication.getContextFromApplication(),
							MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					OilchemApplication.getContextFromApplication()
							.startActivity(intent);
					// }
				}
			} else {
				listener.onRequestSuccess(handlerParams.getMethod(),
						handlerParams.getApi(), oilData, this);
			}
		}
	}

	private void handleUpdateAccessToken(OilResponseData oilData) {
		if (null != oilData
				&& !TextUtils.isEmpty(oilData.getAccessToken())
				&& OilchemApplication.isLogined()
				&& !TextUtils.equals(oilData.getAccessToken(),
						OilchemApplication.getUser().getAccessToken())) {
			OilchemApplication.getUser().setAccessToken(
					oilData.getAccessToken());
			OilchemApplication.setUser(OilchemApplication.getUser());
			ApiUtil.getInstance().updateInstance();
		} else {

		}
	}

	private void handleErrorMessage(JSONObject json) throws JSONException {
		if (json.has(Constant.API_PARAMS_ERROR_MESSAGE)
				&& json.has(Constant.API_PARAMS_STATUS)
				&& ("0".equals(json.getString(Constant.API_PARAMS_STATUS)) || "1"
						.equals(json.getString(Constant.API_PARAMS_STATUS)))
				&& !TextUtils.isEmpty(json
						.getString(Constant.API_PARAMS_ERROR_MESSAGE))) {
			OilUtil.showToast(json.getString(Constant.API_PARAMS_ERROR_MESSAGE));
		}
	}

	@Override
	public void onFailure(Throwable error, String content) {
		super.onFailure(error, content);
		if (null != listener) {
			listener.onRequestError(handlerParams.getMethod(),
					handlerParams.getApi(), this);
		}
	}

	@Override
	public void onFinish() {
		super.onFinish();
		processing = false;
		HandlerFactory.clearHandler(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		processing = true;
	}

}
