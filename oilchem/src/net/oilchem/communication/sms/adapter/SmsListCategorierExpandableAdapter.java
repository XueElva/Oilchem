package net.oilchem.communication.sms.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.adapter.SmsListExpandableAdapter.SmsWelcomeHolder;
import net.oilchem.communication.sms.adapter.SmsListExpandableAdapter.WeekDayHolder;
import net.oilchem.communication.sms.data.model.DataConfig.OilSMSCategory;
import net.oilchem.communication.sms.data.model.DataSmsList;
import net.oilchem.communication.sms.data.model.SmsInfo;
import net.oilchem.communication.sms.util.UIUtil;
import net.oilchem.communication.sms.util.XmlUtil;
import net.oilchem.communication.sms.view.SmsListGroupView;
import net.oilchem.communication.sms.view.SmsWelcomeView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class SmsListCategorierExpandableAdapter extends BaseExpandableListAdapter {

	private DataSmsList data;
	private Context context;
	private SparseArray<ArrayList<SmsInfo>> smsListMapper;
	private HashMap<String, Integer> titleMapper;
	private ArrayList<String> collectedList; //已收藏groupId
	@SuppressLint("SimpleDateFormat")
	public SmsListCategorierExpandableAdapter(Context context, DataSmsList data) {
		if (null == context) {
			return;
		}
		this.context = context;
		collectedList=XmlUtil.getCollectedList();
		setData(data);
	}
	
	private void setData(DataSmsList data) {
		this.data = data;
		initSmsList();
	}

	@SuppressLint("UseSparseArrays")
	private void initSmsList() {
		if (null == smsListMapper) {
			smsListMapper = new SparseArray<ArrayList<SmsInfo>>();
			titleMapper = new HashMap<String, Integer>();
		}
		int whileIndex = 0;
		while (whileIndex < OilchemApplication.getCategoriesCount()) {
			OilSMSCategory category = OilchemApplication.getConfig().getConfig().getCategories().get(whileIndex);
			smsListMapper.put(whileIndex, new ArrayList<SmsInfo>());
			titleMapper.put(category.getName(), whileIndex);
			whileIndex ++;
		}
//		int index = 0;
		if(null!=data){
			for (int i=0; i<data.getMessages().size(); i++) {
				SmsInfo smsInfo = data.getMessages().get(i);
//				if (!titleMapper.containsKey(OilchemApplication.getGroupNameByGroupId(smsInfo.getGroupId()))) {
//					titleMapper.put(OilchemApplication.getGroupNameByGroupId(smsInfo.getGroupId()), index);
//					smsListMapper.put(index, new ArrayList<SmsInfo>());
//					index++;
//				}
				if (titleMapper.containsKey(OilchemApplication.getGroupNameByGroupId(smsInfo.getGroupId()))) {
					smsListMapper.get(titleMapper.get(OilchemApplication.getGroupNameByGroupId(smsInfo.getGroupId()))).add(smsInfo);
				}
			}
		}
		
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if (null == smsListMapper) {
			return null;
		}
		return smsListMapper.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (null == smsListMapper || null == smsListMapper.get(groupPosition)) {
			return 0;
		}
		return smsListMapper.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		if (null == smsListMapper) {
			return null;
		}
		return smsListMapper.get(groupPosition);
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		WeekDayHolder holder = null;
		if (convertView == null) {
			holder = new WeekDayHolder();
			convertView = new SmsListGroupView(context, null);
			holder.setGroupView((SmsListGroupView) convertView);
			convertView.setTag(holder);
		} else {
			holder = (WeekDayHolder) convertView.getTag();
		}
		if (isExpanded) {
			holder.getGroupView().getmImageIndicator().setImageResource(R.drawable.unpack);
		} else {
			holder.getGroupView().getmImageIndicator().setImageResource(R.drawable.pack);
		}
		holder.getGroupView().setSmsCount(smsListMapper.get(groupPosition).size());
		for (Entry<String, Integer> entry : titleMapper.entrySet()) {
			if (entry.getValue() == groupPosition) {
				holder.getGroupView().setWeekDay(entry.getKey());
				holder.getGroupView().setDay("");
				break;
			}
		}
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		SmsWelcomeHolder holder = null;
		if (convertView == null) {
			holder = new SmsWelcomeHolder();
			convertView = new SmsWelcomeView(context, null);
			holder.setSmsWelcome((SmsWelcomeView) convertView);
			convertView.setTag(holder);
		} else {
			holder = (SmsWelcomeHolder) convertView.getTag();
		}
		holder.getSmsWelcome().initSms(smsListMapper.get(groupPosition).get(childPosition));
		return convertView;
	}
	
	@Override
	public int getGroupCount() {
		if (null == smsListMapper) {
			return 0;
		}
		return smsListMapper.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	@Override
	public int getChildType(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public int getChildTypeCount() {
		return 1;
	}

	@Override
	public int getGroupType(int groupPosition) {
		return 0;
	}

	@Override
	public int getGroupTypeCount() {
		return 1;
	}
}
