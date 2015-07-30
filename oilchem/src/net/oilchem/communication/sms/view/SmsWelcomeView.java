package net.oilchem.communication.sms.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import net.oilchem.communication.sms.Constant;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.activity.OilchemSmsListActivity;
import net.oilchem.communication.sms.activity.OilchemWelcomeActivity;
import net.oilchem.communication.sms.activity.ReplyListActivity;
import net.oilchem.communication.sms.data.model.SmsInfo;
import net.oilchem.communication.sms.util.OilUtil;
import net.oilchem.communication.sms.util.SharedPreferenceUtil;
import net.oilchem.communication.sms.util.XmlUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SmsWelcomeView extends LinearLayout {
    private Button historyBtn;
    private Button replyBtn;
    private Context context;
    private TextView content;
    private TextView date;
    private TextView title;
    private Button collection;
    private static DateFormat dateFormater = new SimpleDateFormat("HH:mm:ss");
    private  Drawable collectedDra,notCollectedDra;
    public SmsWelcomeView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_sms_info_welcome, SmsWelcomeView.this);
        historyBtn = (Button)this.findViewById(R.id.layout_sms_history_btn);
        replyBtn = (Button)this.findViewById(R.id.layout_sms_reply_btn);
        content = (TextView) this.findViewById(R.id.view_sms_welcome_content);
        date = (TextView) this.findViewById(R.id.view_sms_welcome_date);
        title = (TextView) this.findViewById(R.id.view_sms_welcome_title);
        collection=(Button) this.findViewById(R.id.collection);
      
        if(!OilchemApplication.isLogined()){
        	collection.setVisibility(View.GONE);
        }
         collectedDra=getResources().getDrawable(R.drawable.collected);
         notCollectedDra=getResources().getDrawable(R.drawable.notcollected);
         collectedDra.setBounds(0, 0, collectedDra.getMinimumWidth(), collectedDra.getMinimumHeight()); 
         notCollectedDra.setBounds(0, 0, collectedDra.getMinimumWidth(), collectedDra.getMinimumHeight()); 

        content.setTextColor(OilchemApplication.getGlobalTextColor());
        setBackgroundColor(OilchemApplication.getGlobalBackground());
    }

    public TextView getContent() {
        return content;
    }

    public TextView getDate() {
        return date;
    }

	public void initSms(final SmsInfo smsInfo) {
//		this.context=context;
		if(smsInfo.isCollected()){
			collection.setText("已收藏");
			collection.setCompoundDrawables(null, null, collectedDra, null);  
		}else{
			collection.setText("收藏");
			collection.setCompoundDrawables(null, null, notCollectedDra, null);  
		}
		
        this.title.setText(smsInfo.getGroupName());
//        this.content.setText(Html.fromHtml(smsInfo.getContent()));

        this.content.setText(smsInfo.getContent());
        Linkify.addLinks(this.content, Linkify.WEB_URLS |
                Linkify.PHONE_NUMBERS | Linkify.EMAIL_ADDRESSES);
//        this.content.setText(Html.fromHtml(smsInfo.getContent()+" <font color='red'>[回复]</font>"));
//        this.content.setText(Html.fromHtml(smsInfo.getContent()+" <font color='red' font-size='12'>[回复]</font>"));
        this.date.setText(OilUtil.getFormatString(smsInfo.getTs(), dateFormater));
        String fontSize = SharedPreferenceUtil.getString(Constant.SHAREDREFERENCES_CONFIG, Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_FONTSIZE, OilchemApplication.getResourceString(R.string.taber_setting_fontsize_big));
        if (TextUtils.equals(OilchemApplication.getResourceString(R.string.taber_setting_fontsize_big), fontSize)) {
            content.setTextSize(20);
        } else if (TextUtils.equals(OilchemApplication.getResourceString(R.string.taber_setting_fontsize_normal), fontSize)) {
            content.setTextSize(18);
        } else if (TextUtils.equals(OilchemApplication.getResourceString(R.string.taber_setting_fontsize_small), fontSize)) {
//			content.setTextSize(OilchemApplication.getContextFromApplication().getResources().getDimension(R.dimen.fontsize_sms_small));
            content.setTextSize(16);
        }
        this.content.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(String.format("%s %s", title.getText().toString().trim(),
                        content.getText().toString().trim()));
                OilUtil.showToast(R.string.toast_clipboard);
                return false;
            }
        });
        
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
        if(getContext() instanceof OilchemSmsListActivity){
               historyBtn.setVisibility(View.GONE);
        }
        this.historyBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent smsListIntent = new Intent(getContext(), OilchemSmsListActivity.class);
                smsListIntent.putExtra(OilchemSmsListActivity.PARAMS_TYPE, sms.getGroupName());
                smsListIntent.putExtra(OilchemSmsListActivity.PARAMS_QUERY, sms.getGroupId());
                smsListIntent.putExtra(OilchemSmsListActivity.PARAMS_LOCAL, true);
                smsListIntent.putExtra(OilchemSmsListActivity.PARAMS_FROM_CATEGORY, false);
                getContext().startActivity(smsListIntent);
            }
        });
        this.replyBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent chatListIntent = new Intent(view.getContext(), ReplyListActivity.class);
                chatListIntent.putExtra(ReplyListActivity.CHATS, sms);
                getContext().startActivity(chatListIntent);
            }
        });
    }
}
