package net.oilchem.communication.sms.adapter;

import java.util.ArrayList;

import net.oilchem.communication.sms.data.model.DataConfig;
import net.oilchem.communication.sms.data.model.DataConfig.OilSMSCategory;
import net.oilchem.communication.sms.view.SettingCellView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class NotificationCategoriesAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<OilSMSCategory> categories;
	
	public NotificationCategoriesAdapter(Context context, DataConfig data) {
		this.context = context;
		setData(data);
	}
	
	public void setData(DataConfig data) {
		if (data.getConfig() != null && data.getConfig().getCategories() != null) {
			categories = data.getConfig().getCategories();
		} else {
			categories = new ArrayList<DataConfig.OilSMSCategory>();
		}
		this.notifyDataSetChanged();
	}
	
	public void setData(ArrayList<OilSMSCategory> data) {
		if (data != null ) {
			categories = data;
		} else {
			categories = new ArrayList<DataConfig.OilSMSCategory>();
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return categories.size();
	}

	@Override
	public Object getItem(int position) {
		return categories.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CategoryHolder holder = null;
		if (convertView == null) {
			holder = new CategoryHolder();
			convertView = new SettingCellView(context, null);
			holder.setCategoryCell((SettingCellView) convertView);
			convertView.setTag(holder);
		} else {
			holder = (CategoryHolder) convertView.getTag();
		}
		holder.getCategoryCell().initView(categories.get(position));
		return convertView;
	}
	
	public static class CategoryHolder {
		private SettingCellView categoryCell;

		public SettingCellView getCategoryCell() {
			return categoryCell;
		}

		public void setCategoryCell(SettingCellView categoryCell) {
			this.categoryCell = categoryCell;
		}
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}
	
	
}
