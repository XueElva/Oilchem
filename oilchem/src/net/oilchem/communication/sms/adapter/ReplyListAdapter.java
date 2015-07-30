package net.oilchem.communication.sms.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import net.oilchem.communication.sms.data.model.DataReply;
import net.oilchem.communication.sms.data.model.SmsInfo;
import net.oilchem.communication.sms.view.ReplyView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luowei on 2014/5/5.
 */
public class ReplyListAdapter extends BaseAdapter {
    private SmsInfo smsInfo;
    private List<DataReply.Reply> replies = new ArrayList<DataReply.Reply>();
    private Context context;

    public ReplyListAdapter(Context context) {
        this.context = context;
        this.replies = new ArrayList<DataReply.Reply>();
    }

    public void clearData() {
        if (replies != null) {
            this.replies.clear();
        }
    }

    public void setData(List<DataReply.Reply> replies,SmsInfo smsInfo) {
        this.smsInfo = smsInfo;
        this.replies=replies;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 1+replies.size();
    }

    @Override
    public Object getItem(int i) {
        return i>0?replies.get(i-1):smsInfo;
    }

    @Override
    public long getItemId(int i) {
        return i;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view==null){
            view = new ReplyView(context, null);
            viewHolder = new ViewHolder(view,i);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }
        ReplyView replyView = ((ReplyView)viewHolder.getView());
        int idx = viewHolder.getIdx();
        if(idx==0){
            replyView.initReply(smsInfo,null,idx);
        }else if(idx > 0){
            replyView.initReply(null,replies,idx);
        }

        return view;
    }

    public static class ViewHolder{

        public ViewHolder(View view,int idx) {
            this.view = view;
            this.idx = idx;
        }

        private int idx;
        private View view;

        public int getIdx() {
            return idx;
        }

        public void setIdx(int idx) {
            this.idx = idx;
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }
    }
}
