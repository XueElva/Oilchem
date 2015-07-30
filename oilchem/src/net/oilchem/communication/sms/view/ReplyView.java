package net.oilchem.communication.sms.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.data.model.DataReply;
import net.oilchem.communication.sms.data.model.SmsInfo;
import net.oilchem.communication.sms.util.OilUtil;

import java.util.List;

/**
 * Created by luowei on 2014/5/5.
 */
public class ReplyView extends FrameLayout {
    private TextView reply;
    private TextView replyDate;


    public ReplyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_reply_info, ReplyView.this);
        reply = (TextView) this.findViewById(R.id.layout_reply);
        replyDate = (TextView) this.findViewById(R.id.layout_reply_date);

    }

    public void initReply(SmsInfo smsInfo, List<DataReply.Reply> replies, int i) {

        if (smsInfo != null && replies ==null &&  i==0) {
            this.reply.setText(smsInfo.getContent());
            this.replyDate.setText(OilUtil.getFormatString(smsInfo.getTs()));
            this.reply.setGravity(Gravity.LEFT);
            this.replyDate.setGravity(Gravity.LEFT);
            this.reply.setTextColor(Color.BLACK);
            reply.setTextSize(18);
            this.replyDate.setTextColor(OilchemApplication.getGlobalTextColor());

            RelativeLayout.LayoutParams replyLayoutParams = new RelativeLayout.LayoutParams(
                    android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
            replyLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            this.reply.setLayoutParams(replyLayoutParams);

            RelativeLayout.LayoutParams dateLayoutParams = new RelativeLayout.LayoutParams(
                    android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
            dateLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            dateLayoutParams.addRule(RelativeLayout.BELOW, R.id.layout_reply);
            this.replyDate.setLayoutParams(dateLayoutParams);

//            RelativeLayout replyDateLayout = (RelativeLayout)findViewById(R.id.relativeLayout_reply);
//            replyDateLayout.setBackgroundColor(Color.WHITE);

        } else if (replies != null && !replies.isEmpty() && smsInfo==null && i > 0) {
            int idx = i > 0 ? i - 1 : 0;
            String reply = replies.get(idx).getReply();
            String replyTime = replies.get(idx).getReplyTime();
            this.reply.setText(reply);
            this.reply.setTextSize(16);
            this.reply.setTextColor(Color.BLUE);
            this.replyDate.setText(OilUtil.getFormatString(replyTime));
        }
    }

    public TextView getReply() {
        return reply;
    }

    public void setReply(TextView reply) {
        this.reply = reply;
    }

    public TextView getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(TextView replyDate) {
        this.replyDate = replyDate;
    }

}
