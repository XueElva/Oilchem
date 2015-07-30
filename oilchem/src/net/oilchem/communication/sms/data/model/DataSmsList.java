package net.oilchem.communication.sms.data.model;

import java.util.ArrayList;

public class DataSmsList extends OilResponseData {
	private static final long serialVersionUID = -3648291618402334577L;
	
	private String key;
	private String ts;
	private ArrayList<SmsInfo> messages;
	
	public DataSmsList(){
		messages=new ArrayList<SmsInfo>();
	}
	public DataSmsList(DataSmsList data, int index) {
		setKey(data.getKey());
		setTs(data.getTs());
		ArrayList<SmsInfo> cs = new ArrayList<SmsInfo>();
		cs.add(data.getMessages().get(index));
		setMessages(cs);
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getTs() {
		return ts;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}
	public ArrayList<SmsInfo> getMessages() {
		if(null==messages){
			messages=new ArrayList<SmsInfo>();
		}
		return messages;
	}
	public void setMessages(ArrayList<SmsInfo> messages) {
		this.messages = messages;
	}
	
	
}
