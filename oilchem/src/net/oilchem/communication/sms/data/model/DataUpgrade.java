package net.oilchem.communication.sms.data.model;

public class DataUpgrade extends OilResponseData {
	private static final long serialVersionUID = 4828978133302253351L;

	private String update;
	private String downloadUrl;
	public boolean updatable() {
		return "1".equals(update);
	}
	public void setUpdate(String update) {
		this.update = update;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
}
