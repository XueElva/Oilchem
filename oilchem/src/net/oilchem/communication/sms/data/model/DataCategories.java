package net.oilchem.communication.sms.data.model;

import java.util.ArrayList;

import net.oilchem.communication.sms.data.model.DataConfig.OilSMSCategory;

public class DataCategories extends OilResponseData {
	private static final long serialVersionUID = 6215868082178844255L;
	
	private ArrayList<OilSMSCategory> categories;

	public ArrayList<OilSMSCategory> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<OilSMSCategory> categories) {
		this.categories = categories;
	}
	
	
}
