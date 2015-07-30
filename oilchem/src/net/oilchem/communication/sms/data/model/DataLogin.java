package net.oilchem.communication.sms.data.model;

public class DataLogin extends OilResponseData {
	private static final long serialVersionUID = -1047206915230233689L;
	
	private String login;
	private String message;
	private String username;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public boolean isLogined() {
		return "1".equals(login);
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
