package net.oilchem.communication.sms.view;

import net.oilchem.communication.sms.R;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TaberView extends LinearLayout{

	private Context context;
	private LinearLayout.LayoutParams params;
	private OnClickListener taberClickListener;
	private TaberSelectedListener listener;
	
	public TaberView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_taber, TaberView.this);
		this.context = context;
		params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
		params.gravity = Gravity.CENTER;
		taberClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i=0; i<TaberView.this.getChildCount(); i++) {
					TaberView.this.getChildAt(i).setSelected(false);
				}
				v.setSelected(true);
				if (listener != null && v instanceof TextView) {
					listener.onTaberSelected((TextView)v, ((TextView)v).getText().toString());
				}
			}
		};
	}
	
	public String getCurrentTab() {
		for (int i=0; i<this.getChildCount(); i++) {
			View view = getChildAt(i); 
			if (view.isSelected() && view instanceof TextView) {
				return ((TextView) view).getText().toString();
			}
		}
		return "";
	}
	
	public void setListener(TaberSelectedListener listener) {
		this.listener = listener;
	}


	/**
	 * taberContent: a string like "xx_xx"
	 * */
	public void initTaber(String taberContent, String selectedContent) {
		if (taberContent.indexOf(selectedContent) < 0) {
			return;
		} else {
			this.removeAllViews();
		}
		String[] tabs = taberContent.split("_");
		for (int i=0; i<tabs.length; i++) {
			String tabName = tabs[i];
			TextView text = new TextView(context, null);
			text.setLayoutParams(params);
			text.setGravity(Gravity.CENTER);
			text.setBackgroundResource(R.drawable.selector_taber);
			text.setText(tabName);
			text.setTextColor(Color.WHITE);
			this.addView(text);
			if (TextUtils.equals(tabName, selectedContent)) {
				text.setSelected(true);
			} else {
				text.setSelected(false);
			}
			text.setOnClickListener(taberClickListener);
		}
	}
	
	public boolean inThisTaber(TextView v) {
		for (int i=0; i<this.getChildCount(); i++) {
			if (this.getChildAt(i) == v) {
				return true;
			}
		}
		return false;
	}
	
	public interface TaberSelectedListener {
		public void onTaberSelected(TextView v, String selectedTaberName);
	}
}
