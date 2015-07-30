package net.oilchem.communication.sms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.adapter.SmsListAdapter;
import net.oilchem.communication.sms.data.model.DataSmsList;
import net.oilchem.communication.sms.data.model.OilResponseData;
import net.oilchem.communication.sms.data.model.SmsInfo;
import net.oilchem.communication.sms.handler.HandlerBase;
import net.oilchem.communication.sms.handler.HandlerFactory;
import net.oilchem.communication.sms.handler.HandlerParams;
import net.oilchem.communication.sms.handler.IRequestListener;
import net.oilchem.communication.sms.util.ApiUtil;
import net.oilchem.communication.sms.util.DatabaseUtil;
import net.oilchem.communication.sms.util.IApi.API;
import net.oilchem.communication.sms.util.IApi.RequestMethod;
import net.oilchem.communication.sms.util.XmlUtil;
import net.oilchem.communication.sms.view.SearchBar;
import net.oilchem.communication.sms.view.TitleBar;

import java.util.ArrayList;

import static net.oilchem.communication.sms.data.OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_GROUPID;

public class OilchemSmsListActivity extends OilActivityBase implements IRequestListener, OnScrollListener {
	public static final String PARAMS_QUERY = "query";
	public static final String PARAMS_LOCAL = "local";
	public static final String PARAMS_PUSH_DATA = "push_data_smslist";
	public static final String PARAMS_FROM_CATEGORY = "from_category";
    public static final String PARAMS_TYPE = "type";

    private TitleBar titlebar;
	private ListView mListSmsList;
	private SmsListAdapter mAdapter;
	private SearchBar searchBar;
	private ArrayList<SmsInfo> smsList;
    private String queryType;
	private String query;
	private String lastTs;
	private boolean searchLocal;
	private HandlerBase smsSearchHandler;

	private boolean isPush;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_smslist);
		isPush = false;
		titlebar = (TitleBar) findViewById(R.id.activity_smslist_titlebar);
		mListSmsList = (ListView) findViewById(R.id.acivity_smslist_listview);
		mListSmsList.setOnScrollListener(this);
//        mListSmsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<? > arg0, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(), "aaaaaaaa",Toast.LENGTH_SHORT).show();
//            }
//        });

		searchBar = (SearchBar) findViewById(R.id.activity_smslist_searchbar);
        queryType = getIntent().getStringExtra(PARAMS_TYPE);
		query = getIntent().getStringExtra(PARAMS_QUERY);
		searchLocal = getIntent().getBooleanExtra(PARAMS_LOCAL, true);
		smsList = new ArrayList<SmsInfo>();
		searchBar.getEdit().requestFocus();
		searchBar.getEdit().setText(query);
		if (!TextUtils.isEmpty(query)) {
			searchBar.getEdit().setSelection(query.length());
		}
		searchBar.getSearchBtn().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadData(0, true);
			}
		});
		lastTs = "";
		handlePush(getIntent());
		if (!isPush) {
			loadData(0);
		}
		initTitlebar();
		//新增来自分类页跳转的判断处理
		if(getIntent().getBooleanExtra(PARAMS_FROM_CATEGORY, false)){
			titlebar.setTitle(this,searchBar.getQuery());
			searchBar.setVisibility(View.GONE);
		}
	}



	private void handlePush(Intent intent) {
		if(intent != null) {
			DataSmsList pushData = (DataSmsList) intent.getSerializableExtra(PARAMS_PUSH_DATA);
			if (null != pushData && pushData.getMessages() != null && pushData.getMessages().size() > 0) {
				//推送的消息
				searchBar.setVisibility(View.GONE);
				titlebar.setTitle(this, R.string.title_push);
				mAdapter = new SmsListAdapter(this);
				mAdapter.setSmsWelcomeView(true);
			if(OilchemApplication.isLogined()){
				ArrayList<SmsInfo> messages=checkIsCollected(pushData.getMessages());
				mAdapter.setData(messages, false);
			}else{
				mAdapter.setData(pushData.getMessages(), false);
			}
				
				mListSmsList.setAdapter(mAdapter);
				isPush = true;
				setResult(RESULT_OK);
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handlePush(intent);
	}

	private void initTitlebar() {
        if(queryType!=null && !queryType.equals("type")) {
            titlebar.setTitle(this, queryType);
        }else if (searchLocal) {
			titlebar.setTitle(this, OilchemApplication.getResourceString(R.string.title_smslist_native));
		} else {
			titlebar.setTitle(this, OilchemApplication.getResourceString(R.string.title_smslist_online));
		}
		if (isPush) {
			titlebar.setTitle(this, OilchemApplication.getResourceString(R.string.title_smslist_push));
		}
		titlebar.setLeft(this, TitleBar.ACTION_BACK);
	}

	private void loadData(int offset, boolean clear) {
		if (clear && null!=mAdapter) {
			mAdapter.clearData();
			mAdapter.notifyDataSetChanged();
		}
		loadData(offset);
	}

	private void loadData(int offset) {
		query = searchBar.getQuery();
		if (searchLocal) {
			int limit = 0;
			try {
                if(queryType!=null && !queryType.equals("type")){
                    searchBar.setVisibility(View.GONE);
                    String whereClause =" WHERE "+ COLUMN_NAME_SMS_GROUPID+" = '"+query+"' ";
                    smsList = DatabaseUtil.getInstance().query(whereClause);
                }else{
                    limit = OilchemApplication.getConfig().getConfig().getPageSizeWhileSearchingLocalSMS();
                    smsList = DatabaseUtil.getInstance().query(query, lastTs, offset, limit);
                }
			} catch(Exception e) {
			}

//			if(0 == smsList.size()){
//                mAdapter = new SmsListAdapter(this);
//                mAdapter.setData(smsList, true);
//                mListSmsList.setAdapter(mAdapter);
//                return;//这个返回仅仅是为了减少处理流程
//            }
			if (null == mAdapter) {
				mAdapter = new SmsListAdapter(this);
				if(OilchemApplication.isLogined()){
					mAdapter.setData(checkIsCollected(smsList), true);
				}else{
					mAdapter.setData(smsList, true);
				}
			
				mListSmsList.setAdapter(mAdapter);
			} else {
				//mAdapter.setData(smsList, false);//这个地方肯定是原作者笔误
				mAdapter.setData(checkIsCollected(smsList), true);
			}
			lastTs = getLastTs(smsList);
		} else {
			HandlerParams params = ApiUtil.getInstance().initSmsSearch(query, lastTs);
			smsSearchHandler = HandlerFactory.getHandler(params, this);
			ApiUtil.getInstance().sendRequest(smsSearchHandler);
		}
	}

	public static String getLastTs(ArrayList<SmsInfo> list) {
		if (list != null && list.size() > 0) {
			return list.get(list.size() - 1).getTs();
		}
		return "";
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onRequestSuccess(RequestMethod method, API api,
			OilResponseData response, HandlerBase handler) {
		switch(api) {
		case API_SMS_SEARCH:
			if (smsSearchHandler == handler) {
				if(OilchemApplication.isLogined()){
					smsList = checkIsCollected(((DataSmsList) response).getMessages());
				}else{
					smsList =((DataSmsList) response).getMessages();
				}
				
				
				if (mAdapter == null) {
					mAdapter = new SmsListAdapter(this);
					mAdapter.setData(((DataSmsList) response).getMessages(), false);
					mListSmsList.setAdapter(mAdapter);
				} else {
					mAdapter.setData(((DataSmsList) response).getMessages(), true);
				}
//				lastTs = getLastTs(smsList);
				lastTs = ((DataSmsList) response).getTs();
			}
			break;
		default:
			break;
		}
	}

	
	/**
	 * 检查是否是收藏的资讯组
	 */
	private ArrayList<SmsInfo> checkIsCollected(ArrayList<SmsInfo> messages){
		ArrayList<String> collectedList=XmlUtil.getCollectedList();
		for (int i = 0; i < messages.size(); i++) {
			if(collectedList.contains(messages.get(i).getGroupId())){
				messages.get(i).setCollected(true);
			}else{
				messages.get(i).setCollected(false);
			}
		}
		
		return messages;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onRequestError(RequestMethod method, API api,
			HandlerBase handler) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (visibleItemCount > 0 && firstVisibleItem + visibleItemCount == totalItemCount && !TextUtils.isEmpty(lastTs) && mAdapter != null) {
			if (!isPush) {
                if(queryType!=null && !queryType.equals("type")){
                }else{
                    loadData(mAdapter.getCount());
                }
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}
