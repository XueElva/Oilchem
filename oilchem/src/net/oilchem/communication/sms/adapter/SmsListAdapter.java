package net.oilchem.communication.sms.adapter;

import java.util.ArrayList;

import net.oilchem.communication.sms.data.model.SmsInfo;
import net.oilchem.communication.sms.util.XmlUtil;
import net.oilchem.communication.sms.view.SmsView;
import net.oilchem.communication.sms.view.SmsWelcomeView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SmsListAdapter extends BaseAdapter {
	private ArrayList<SmsInfo> smsList;
	private Context context;
	private boolean isSmsWelcomeView;
	public SmsListAdapter(Context context) {
		this.context = context;
		this.isSmsWelcomeView = false;
		this.smsList = new ArrayList<SmsInfo>();
	}
	
	public void clearData() {
		if (smsList != null) {
			this.smsList.clear();
		}
	}
	
	public void setData(ArrayList<SmsInfo> smsList, boolean isNextPage) {
		if (isNextPage) {
			this.smsList.addAll(smsList);
		} else {
			this.smsList = smsList;
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return smsList.size();
	}

	@Override
	public Object getItem(int position) {
		return smsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SmsHolder holder = null;
		SmsInfo info = smsList.get(position);
		if (convertView == null) {
			holder = new SmsHolder();
			if (isSmsWelcomeView) { //推送
				convertView = new SmsWelcomeView(context, null);
			} else {
				convertView = new SmsView(context, null);
			}
			holder.setSms(convertView);
			convertView.setTag(holder);
		} else {
			holder = (SmsHolder) convertView.getTag();
		}
		if (holder.getSms() instanceof SmsView) {
			((SmsView)holder.getSms()).initSms(info);
		} else if (holder.getSms() instanceof SmsWelcomeView) {
			((SmsWelcomeView)holder.getSms()).initSms(info);
		}
		return convertView;
	}
	
	public static class SmsHolder {
		View sms;

		public View getSms() {
			return sms;
		}

		public void setSms(View sms) {
			this.sms = sms;
		}
	}

	public void setSmsWelcomeView(boolean b) {
		this.isSmsWelcomeView = b;
	}
}
