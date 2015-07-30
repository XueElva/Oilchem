package net.oilchem.communication.sms.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.oilchem.communication.sms.Constant;
import net.oilchem.communication.sms.util.IApi.API;
import net.oilchem.communication.sms.util.IApi.RequestMethod;

import android.text.TextUtils;

public class HandlerParams {
	private RequestMethod method = RequestMethod.INVALID;
	private API api;
	private HashMap<String, String> params;

	private String universalToken;
	
	public HandlerParams(RequestMethod method, API api, HashMap<String, String> params) {
		this.method = method;
		this.api = api;
		this.params = new HashMap<String, String>(params);
//		this.params.put(Constant.API_COMMON_PARAMS_API, api.toString());
	}

	public API getApi() {
		return api;
	}
	
	public void put(String key, String value) {
		params.put(key, value);
	}
	
	public void put(String key, int value) {
		params.put(key, String.valueOf(value));
	}
	
	public boolean containsKey(String key) {
		return params.containsKey(key);
	}
	
	public String get(String key) {
		return params.get(key);
	}
	
	public RequestMethod getMethod() {
		return method;
	}
	
	public HashMap<String, String> getParams() {
		return params;
	}
	
	@Override
	public String toString() {
		if (universalToken == null) {
			ArrayList<String> ps = new ArrayList<String>();
			for (Entry<String, String> entry : params.entrySet()) {
				ps.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
			}
			universalToken = String.format("%s&%s&%s", getMethod(), getApi(), TextUtils.join("&", ps));
		}
		return universalToken;
	}
	
	
}
