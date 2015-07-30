package net.oilchem.communication.sms.view;

import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.util.OilUtil;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class SmsListGroupView extends FrameLayout {

	private TextView mTextWeekday, mTextDay, mTextSmsCount;
	private ImageView mImageIndicator;
	
	public SmsListGroupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_smslist_group, SmsListGroupView.this);
		mTextWeekday = (TextView) findViewById(R.id.view_smslist_group_weekday);
		mTextSmsCount = (TextView) findViewById(R.id.view_smslist_group_infocount);
		mTextDay = (TextView) findViewById(R.id.view_smslist_group_day);
		mImageIndicator = (ImageView) findViewById(R.id.view_smslist_group_indicator);
	}

	public ImageView getmImageIndicator() {
		return mImageIndicator;
	}

	public void setWeekDay(int groupPosition) {
		mTextWeekday.setText(OilUtil.getWeekDayByIndex(groupPosition));
	}
	
	public void setSmsCount(int count) {
		mTextSmsCount.setText(String.valueOf(count));
	}

	public void setWeekDay(String str) {
		mTextWeekday.setText(str);
	}

	public TextView getmTextWeekday() {
		return mTextWeekday;
	}

	public void setDay(String dayString) {
		if (TextUtils.isEmpty(dayString)) {
			mTextDay.setVisibility(View.GONE);
		} else {
			mTextDay.setVisibility(View.VISIBLE);
			mTextDay.setText(dayString);
		}
	}
}
