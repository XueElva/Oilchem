package net.oilchem.communication.sms.data.model;

public class DataLogout extends OilResponseData {
	private static final long serialVersionUID = -2306092151233238731L;

	private String logout;

	public boolean logoutSuccessful() {
		return "1".equals(logout);
	}

	public void setLogout(String logout) {
		this.logout = logout;
	}
}
