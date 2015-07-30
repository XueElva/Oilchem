package net.oilchem.communication.sms.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import net.oilchem.communication.sms.Constant;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.activity.OilchemSmsListActivity;
import net.oilchem.communication.sms.activity.ReplyListActivity;
import net.oilchem.communication.sms.data.model.SmsInfo;
import net.oilchem.communication.sms.util.OilUtil;
import net.oilchem.communication.sms.util.SharedPreferenceUtil;
import net.oilchem.communication.sms.util.XmlUtil;

public class SmsView extends LinearLayout {
	private Button historyBtn;
	private Button replyBtn;
	private TextView content;
	private TextView date;
	private Button collection;
    private  Drawable collectedDra,notCollectedDra;
    private Context context;
	public SmsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_sms_info, SmsView.this);
		historyBtn = (Button) this.findViewById(R.id.layout_sms_history_btn);
		replyBtn = (Button) this.findViewById(R.id.layout_sms_reply_btn);
		content = (TextView) this.findViewById(R.id.layout_sms_content);
		date = (TextView) this.findViewById(R.id.layout_sms_date);
		collection = (Button) this.findViewById(R.id.collection);
		content.setTextColor(OilchemApplication.getGlobalTextColor());
		setBackgroundColor(OilchemApplication.getGlobalBackground());
		
		if(!OilchemApplication.isLogined()){
			collection.setVisibility(View.GONE);
		}
		 collectedDra=getResources().getDrawable(R.drawable.collected);
         notCollectedDra=getResources().getDrawable(R.drawable.notcollected);
         collectedDra.setBounds(0, 0, collectedDra.getMinimumWidth(), collectedDra.getMinimumHeight()); 
         notCollectedDra.setBounds(0, 0, collectedDra.getMinimumWidth(), collectedDra.getMinimumHeight()); 
	}

	public TextView getContent() {
		return content;
	}

	public TextView getDate() {
		return date;
	}

	public void initSms(final SmsInfo smsInfo) {
		// this.content.setText(Html.fromHtml(smsInfo.getContent()+" <font color='red'>[回复]</font>"));
		// this.content.setText(Html.fromHtml(smsInfo.getContent()+" <font color='red' font-size='12'>[回复]</font>"));
		
		if(smsInfo.isCollected()){
			collection.setText("已收藏");
			collection.setCompoundDrawables(null, null, collectedDra, null);  
		}else{
			collection.setText("收藏");
			collection.setCompoundDrawables(null, null, notCollectedDra, null);  
		}
		
		this.content.setText(smsInfo.getContent());
		Linkify.addLinks(this.content, Linkify.WEB_URLS | Linkify.PHONE_NUMBERS
				| Linkify.EMAIL_ADDRESSES);

		// this.content.setText(Html.fromHtml(smsInfo.getContent()));
		this.date.setText(OilUtil.getFormatString(smsInfo.getTs()));

		String fontSize = SharedPreferenceUtil
				.getString(
						Constant.SHAREDREFERENCES_CONFIG,
						Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_FONTSIZE,
						OilchemApplication
								.getResourceString(R.string.taber_setting_fontsize_big));
		if (TextUtils.equals(OilchemApplication
				.getResourceString(R.string.taber_setting_fontsize_big),
				fontSize)) {
			content.setTextSize(20);
		} else if (TextUtils.equals(OilchemApplication
				.getResourceString(R.string.taber_setting_fontsize_normal),
				fontSize)) {
			content.setTextSize(18);
		} else if (TextUtils.equals(OilchemApplication
				.getResourceString(R.string.taber_setting_fontsize_small),
				fontSize)) {
			// content.setTextSize(OilchemApplication.getContextFromApplication().getResources().getDimension(R.dimen.fontsize_sms_small));
			content.setTextSize(16);
		}

		   
        //收藏
        this.collection.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String groupId=smsInfo.getGroupId();
				if(smsInfo.isCollected()){
					//取消收藏
					XmlUtil.cancelCollect(smsInfo.getGroupId());
					smsInfo.setCollected(false);
					collection.setText("收藏");
					collection.setCompoundDrawables(null, null, notCollectedDra, null); 
					Toast.makeText(context, smsInfo.getGroupName()+"已取消收藏", 1).show();
				}else{
					//收藏
					XmlUtil.collect(smsInfo.getGroupId());
					smsInfo.setCollected(true);
					collection.setText("已收藏");
					collection.setCompoundDrawables(null, null, collectedDra, null); 
					Toast.makeText(context, smsInfo.getGroupName()+"已收藏", 1).show();
				}
			}
		});
		final SmsInfo sms = smsInfo;
		// this.content.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View view) {
		// // Toast.makeText(getContext(), "aaaaa", Toast.LENGTH_SHORT).show();
		// Intent chatListIntent = new Intent(view.getContext(),
		// ReplyListActivity.class);
		// chatListIntent.putExtra(ReplyListActivity.CHATS, sms);
		// getContext().startActivity(chatListIntent);
		// }
		// });

		 if(getContext() instanceof OilchemSmsListActivity){
		 historyBtn.setVisibility(View.GONE);
		 }
		this.historyBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d("value","查看历史");
				Intent smsListIntent = new Intent(getContext(),
						OilchemSmsListActivity.class);
				smsListIntent.putExtra(OilchemSmsListActivity.PARAMS_TYPE,
						sms.getGroupName());
				smsListIntent.putExtra(OilchemSmsListActivity.PARAMS_QUERY,
						sms.getGroupId());
				smsListIntent.putExtra(OilchemSmsListActivity.PARAMS_LOCAL,
						true);
				smsListIntent.putExtra(
						OilchemSmsListActivity.PARAMS_FROM_CATEGORY, false);
				getContext().startActivity(smsListIntent);
			}
		});
		this.replyBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d("value","回复");
				Intent chatListIntent = new Intent(view.getContext(),
						ReplyListActivity.class);
				chatListIntent.putExtra(ReplyListActivity.CHATS, sms);
				getContext().startActivity(chatListIntent);
			}
		});
	}

}
