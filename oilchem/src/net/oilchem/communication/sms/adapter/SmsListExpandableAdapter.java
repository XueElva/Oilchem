package net.oilchem.communication.sms.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Toast;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.data.model.DataSmsList;
import net.oilchem.communication.sms.data.model.SmsInfo;
import net.oilchem.communication.sms.util.OilUtil;
import net.oilchem.communication.sms.util.XmlUtil;
import net.oilchem.communication.sms.view.SmsListGroupView;
import net.oilchem.communication.sms.view.SmsWelcomeView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SmsListExpandableAdapter extends BaseExpandableListAdapter {

	private DataSmsList data;
	private Context context;
	private SparseArray<ArrayList<SmsInfo>> smsListMapper; //子列表
	private SparseArray<Integer> keyMapper; //分组
	private SparseArray<String> dayMapper;
	private DateFormat dayFormater;
	private int currentWeekIndex;
	private ArrayList<String> collectedList; //已收藏groupId
	@SuppressLint("SimpleDateFormat")
	public SmsListExpandableAdapter(Context context, DataSmsList data) {
		if (null == context) {
			return;
		}
		this.context = context;
		this.dayFormater = new SimpleDateFormat("yyyy-MM-dd");
		currentWeekIndex = getWeekIndex(String.valueOf(System.currentTimeMillis()));
		collectedList=XmlUtil.getCollectedList();
		setData(data);
	}
	
	public void setData(DataSmsList data) {
		this.data = data;
		initSmsList();
	}

	@SuppressLint("UseSparseArrays")
	private void initSmsList() {
		if (null == smsListMapper) {
			smsListMapper = new SparseArray<ArrayList<SmsInfo>>();
			dayMapper = new SparseArray<String>();
			keyMapper = new SparseArray<Integer>();
		}
		int whileIndex = 0; 
		while (whileIndex < 7) {
			int dayIndex = currentWeekIndex - whileIndex;
			if (dayIndex < 0) dayIndex += 7;
			smsListMapper.put(dayIndex, new ArrayList<SmsInfo>());
			dayMapper.put(dayIndex, OilUtil.getFormatString(String.valueOf(System.currentTimeMillis() - whileIndex *24*3600*1000), dayFormater));
			keyMapper.append(whileIndex, dayIndex);
			whileIndex ++;
		}
		for (int i=0; i<data.getMessages().size(); i++) {
			SmsInfo smsInfo = data.getMessages().get(i);
			int weekIndex = getWeekIndex(smsInfo.getTs());//星期几
			smsListMapper.get(weekIndex).add(smsInfo);
			
		}
		
//		rangeMapper();
		
	}
	
	

//	private void rangeMapper() {
//		sortedSmsListMapper = new SparseArray<Integer>();
//		for (int i=0; i<smsListMapper.size(); i++) {
//			sortedSmsListMapper.append(i, smsListMapper.);
//		}
//	}

	private int getWeekIndex(String ts) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(OilUtil.getLongTimemillis(ts)));
		int weekIndex = c.get(Calendar.DAY_OF_WEEK) - 2;
		if (weekIndex < 0) {
			weekIndex += 7;
		}
		return weekIndex;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if (null == smsListMapper) {
			return null;
		}
		return smsListMapper.get(keyMapper.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (null == smsListMapper || null == smsListMapper.get(keyMapper.get(groupPosition))) {
			return 0;
		}
		return smsListMapper.get(keyMapper.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		if (null == smsListMapper) {
			return null;
		}
		return smsListMapper.get(keyMapper.get(groupPosition));
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		WeekDayHolder holder = null;
		if (convertView == null) {
			holder = new WeekDayHolder();
			convertView = new SmsListGroupView(context, null);
//			convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
			holder.setGroupView((SmsListGroupView) convertView);
			convertView.setTag(holder);
		} else {
			holder = (WeekDayHolder) convertView.getTag();
		}
		if (keyMapper.get(groupPosition) == currentWeekIndex) {
			holder.getGroupView().setWeekDay(OilchemApplication.getResourceString(R.string.weekday_today));
		} else {
			holder.getGroupView().setWeekDay(keyMapper.get(groupPosition));
		}
		
		if (isExpanded) {
			holder.getGroupView().getmImageIndicator().setImageResource(R.drawable.unpack);
		} else {
			holder.getGroupView().getmImageIndicator().setImageResource(R.drawable.pack);
		}
		holder.getGroupView().setSmsCount(smsListMapper.get(keyMapper.get(groupPosition)).size());
 		holder.getGroupView().setDay(dayMapper.get(keyMapper.get(groupPosition)));
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
		holder.getSmsWelcome().initSms(smsListMapper.get(keyMapper.get(groupPosition)).get(childPosition));

		return convertView;
	}

	public static class SmsWelcomeHolder {
		private SmsWelcomeView smsWelcome;

		public SmsWelcomeView getSmsWelcome() {
			return smsWelcome;
		}

		public void setSmsWelcome(SmsWelcomeView smsWelcome) {
			this.smsWelcome = smsWelcome;
		}
	}
	
	public static class WeekDayHolder {
		private SmsListGroupView groupView;

		public SmsListGroupView getGroupView() {
			return groupView;
		}

		public void setGroupView(SmsListGroupView groupView) {
			this.groupView = groupView;
		}
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
		return keyMapper.get(groupPosition);
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

	public int getCurrentWeekIndex() {
		return currentWeekIndex;
	}
	
	/**
	 * 查看某组是否只剩收藏的资讯
	 */
	public boolean isOnlyCollectedMessages(String ts){
		
		int weekIndex = getWeekIndex(ts);//星期几
		for (int i = 0; i < smsListMapper.get(weekIndex).size(); i++) {
			if(!smsListMapper.get(weekIndex).get(i).isCollected()){
				 return false;
			}
		}
		return true;
	}
	/**
	 * 查询某天最晚刷新的消息的时间戳
	 * @param ts
	 * @return
	 */
	public String getLastRefreshTime(String ts){
		int weekIndex = getWeekIndex(ts);//星期几
		Long lastRefreshTime=0l,time; 
		for (int i = 0; i < smsListMapper.get(weekIndex).size(); i++) {
			time=Long.parseLong(smsListMapper.get(weekIndex).get(0).getTs());
			if(time>lastRefreshTime){
				lastRefreshTime=time;
			}
		}
		
		return String.valueOf(lastRefreshTime);
	}
}
