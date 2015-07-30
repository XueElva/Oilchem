package net.oilchem.communication.sms.data.model;

public class DataRegister extends OilResponseData {
	private static final long serialVersionUID = 6650461369865119707L;
	
	private String login;
	private String message;
	private String register;

	public boolean registerSuccessful() {
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
	public String getRegister() {
		return register;
	}
	public void setRegister(String register) {
		this.register = register;
	}
}
