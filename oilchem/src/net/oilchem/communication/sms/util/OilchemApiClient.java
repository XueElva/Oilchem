package net.oilchem.communication.sms.util;

import net.oilchem.communication.sms.Constant;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.handler.BinaryHandlerBase;
import net.oilchem.communication.sms.handler.HandlerBase;
import net.oilchem.communication.sms.handler.HandlerParams;
import net.oilchem.communication.sms.util.IApi.API;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class OilchemApiClient {
//	public static final String BASE_URL = "http://test.aginfo.cn";
//public static final String BASE_URL = "http://192.168.0.76:8080";
	public static final String BASE_URL = "http://android.oilchem.net";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static String getAbsoluteUrl(String relativeUrl) {
		if(relativeUrl.startsWith("http://"))
			return relativeUrl;
		return BASE_URL + relativeUrl;
	}
	
	public static void sendRequest(String url, BinaryHandlerBase responseHandler) {
		if (null != url) {
			if (NetworkUtil.currentNetworkAvailable()) {
				if (null != responseHandler && !responseHandler.isProcessing()) {
					client.get(OilchemApplication.getContextFromApplication(), getAbsoluteUrl(url), responseHandler);
				}
			}
		}
	}
	
	public static void sendRequest(HandlerBase handler) {
		if (handler == null || handler.getHandlerParams() == null || (handler.getHandlerParams().containsKey(Constant.API_PARAMS_FLAG) && null == handler.getHandlerParams().get(Constant.API_PARAMS_FLAG))) {
			return;
		}
		HandlerParams params = handler.getHandlerParams();
		//由于footer, emptyView 需要有重试体验，故这里将判断网络注释
		if (!NetworkUtil.currentNetworkAvailable()) {
			OilUtil.showToast(R.string.toast_network_method_error);
			return;
		}
		if (!handler.isProcessing()) {
//			NetworkUtil.updateNetInfo();
			switch(params.getMethod()) {
			case GET:
				get(handler, false);
				break;
			case POST:
				post(handler, false);
				break;
			default:
				OilUtil.showToast(R.string.toast_network_method_error);
				break;
			}
		}
	}

	public static void get(HandlerBase responseHandler, boolean isAbsolutePath) {
		String uri = null;
		HandlerParams params = responseHandler.getHandlerParams();
		if (isAbsolutePath && params.getApi().toString().startsWith("http")) {
			uri = params.getApi().toString();
		} else {
			uri = getAbsoluteUrl(params.getApi().toString());
		}
		if (null != params) {
			client.get(OilchemApplication.getContextFromApplication(), uri, responseHandler.getHeaders(), new RequestParams(params.getParams()), responseHandler);
		}
	}
	
	public static void post(HandlerBase responseHandler, boolean isAbsolutePath) {
		String uri = null;
	
		HandlerParams params = responseHandler.getHandlerParams();
		if (isAbsolutePath && params.getApi().toString().startsWith("http")) {
			uri = params.getApi().toString();
		} else {
			uri = getAbsoluteUrl(params.getApi().toString());
		}
		
		if (null != params) {
			client.post(OilchemApplication.getContextFromApplication(), uri, new RequestParams(params.getParams()), responseHandler);
		}
	}
}
