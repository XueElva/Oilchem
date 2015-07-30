package net.oilchem.communication.sms.data.model;

import java.io.Serializable;

public class OilResponse implements Serializable {
	private static final long serialVersionUID = 3777780862460229362L;
	
	private String state;
	private String errorMessage;
	private OilResponseData data;
	
	public OilResponse(String state, String errorMessage, OilResponseData data) {
		this.state = state;
		this.errorMessage = errorMessage;
		this.data = data;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public OilResponseData getData() {
		return data;
	}
	public void setData(OilResponseData data) {
		this.data = data;
	}
	
	
}
