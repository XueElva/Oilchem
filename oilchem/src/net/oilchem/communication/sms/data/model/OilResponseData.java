package net.oilchem.communication.sms.data.model;

import java.io.Serializable;

public abstract class OilResponseData implements Serializable {
	private static final long serialVersionUID = 8549749060677512123L;
	private String accessToken;
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
