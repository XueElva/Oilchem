package net.oilchem.communication.sms.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.reflect.TypeToken;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.adapter.ReplyListAdapter;
import net.oilchem.communication.sms.data.OilchemContract;
import net.oilchem.communication.sms.data.model.DataReply;
import net.oilchem.communication.sms.data.model.OilResponseData;
import net.oilchem.communication.sms.data.model.SmsInfo;
import net.oilchem.communication.sms.handler.HandlerBase;
import net.oilchem.communication.sms.handler.HandlerFactory;
import net.oilchem.communication.sms.handler.HandlerParams;
import net.oilchem.communication.sms.handler.IRequestListener;
import net.oilchem.communication.sms.util.ApiUtil;
import net.oilchem.communication.sms.util.DatabaseUtil;
import net.oilchem.communication.sms.util.IApi;
import net.oilchem.communication.sms.util.JsonUtil;
import net.oilchem.communication.sms.view.SendBar;
import net.oilchem.communication.sms.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luowei on 2014/5/5.
 */
public class ReplyListActivity extends OilActivityBase implements IRequestListener, View.OnClickListener {

    public static final String CHATS = "chats";
    private TitleBar titlebar;
    private ListView mListReplyList;
    private ReplyListAdapter mAdapter;
    private SendBar sendBar;

    private DataReply.Reply reply;
    SmsInfo smsInfo;
    List<DataReply.Reply> replies;

    private HandlerBase pushReplyHandler;
    private HandlerBase configHandler;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_replylist);

        titlebar = (TitleBar) findViewById(R.id.activity_replylist_titlebar);
        titlebar.setTitle(this, OilchemApplication.getResourceString(R.string.title_chatlist_native));
        titlebar.setLeft(this, TitleBar.ACTION_BACK);

        mListReplyList = (ListView) findViewById(R.id.acivity_replylist_listview);
        sendBar = (SendBar) findViewById(R.id.activity_replylist_sendbar);
        sendBar.getEdit().requestFocus();

        //加载回复数据
        smsInfo = (SmsInfo) getIntent().getExtras().get(CHATS);
        List<SmsInfo> smsList = DatabaseUtil.getInstance()
                .query(" WHERE " + String.format(" %s='%s' ", OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_MSGID, smsInfo.getMsgId()));
        if (smsList!=null) {
            String repliesJson = smsList.isEmpty()?"[]":smsList.get(0).getReplies();
            smsInfo.setReplies(repliesJson);
            replies = JsonUtil.fromJson(repliesJson, new TypeToken<List<DataReply.Reply>>() {}.getType());
            //设置Adapter
            if (mAdapter == null) {
                mAdapter = new ReplyListAdapter(this);
            }
            smsInfo.setReplies(repliesJson);
            replies = replies==null?new ArrayList<DataReply.Reply>():replies;
            mAdapter.setData(replies, smsInfo);
            mListReplyList.setAdapter(mAdapter);
            mListReplyList.setCacheColorHint(Color.TRANSPARENT);
        }

        sendBar.getSendBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(ReplyListActivity.this, "sendBtn clicked", Toast.LENGTH_SHORT).show();
                if (!OilchemApplication.isLogined()) {
                    ReplyListActivity.this.finish();
                }
                loadNewReply();
                if (null!= mAdapter) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void loadNewReply() {
        String username = "";//OilchemApplication.getUser().getUsername();
        String replyText = sendBar.getEdit().getText().toString();
        HandlerParams handlerParams = ApiUtil.getInstance().initPushReply(smsInfo.getMsgId(),username,replyText);
        pushReplyHandler = HandlerFactory.getHandler(handlerParams, this);
        ApiUtil.getInstance().sendRequest(pushReplyHandler);
    }

    private void initRequestConfig() {
        if (null == OilchemApplication.getConfig().getConfig() || null == OilchemApplication.getConfig().getConfig().getCategories()) {
            HandlerParams params = ApiUtil.getInstance().initConfig();
            configHandler = HandlerFactory.getHandler(params, this);
            ApiUtil.getInstance().sendRequest(configHandler);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("push", "handle intent" + intent.getSerializableExtra(OilchemSmsListActivity.PARAMS_PUSH_DATA));
        if (intent != null && intent.hasExtra(OilchemSmsListActivity.PARAMS_PUSH_DATA)) {
            Intent intent1 = new Intent(this, OilchemSmsListActivity.class);
            intent1.putExtra(OilchemSmsListActivity.PARAMS_PUSH_DATA, intent.getSerializableExtra(OilchemSmsListActivity.PARAMS_PUSH_DATA));
            startActivityForResult(intent1, 1);
        }
    }

    @Override
    public void onRequestSuccess(IApi.RequestMethod method, IApi.API api, OilResponseData response, HandlerBase handler) {
        switch (api) {
            case API_GET_REPLIES:
                break;
            case API_PUSHREPLY:
                if (pushReplyHandler == handler) {
                    if (response == null) break;
                    replies = replies == null ? new ArrayList<DataReply.Reply>() : replies;
                    reply = ((DataReply) response).getReply();
                    replies.add(reply);
                    String repliesJson = JsonUtil.toJson(replies, new TypeToken<List<DataReply.Reply>>() {
                    }.getType());

                    //设置Adapter
                    if (mAdapter == null) {
                        mAdapter = new ReplyListAdapter(this);
                    }
                    smsInfo.setReplies(repliesJson);
                    mAdapter.setData(replies,smsInfo);

                    //保存到sqllite数据库中
                    long updateNum = DatabaseUtil.getInstance().update(smsInfo);
                    initRequestConfig();

                    InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(sendBar.getEdit().getWindowToken(),0);
                    Toast.makeText(ReplyListActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    sendBar.getEdit().setText("");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestError(IApi.RequestMethod method, IApi.API api, HandlerBase handler) {

    }

    @Override
    public void onClick(View view) {
    }
}
