package net.oilchem.communication.sms.data.model;

public class DataPhoneCode extends OilResponseData {
	private static final long serialVersionUID = -2271783878409845172L;
	private String message;
	private String getcode;

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getGetcode() {
		return getcode;
	}
	public void setGetcode(String getcode) {
		this.getcode = getcode;
	}
	
}
