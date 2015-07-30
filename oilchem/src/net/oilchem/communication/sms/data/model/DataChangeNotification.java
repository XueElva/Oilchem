package net.oilchem.communication.sms.data.model;

import java.util.ArrayList;

import net.oilchem.communication.sms.data.model.DataConfig.OilSMSCategory;

public class DataChangeNotification extends OilResponseData {
	private static final long serialVersionUID = -7263768142644029889L;

	private ArrayList<OilSMSCategory> categories;

	public ArrayList<OilSMSCategory> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<OilSMSCategory> categories) {
		this.categories = categories;
	}
}
