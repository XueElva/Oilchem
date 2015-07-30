package net.oilchem.communication.sms.viewpager;

import net.oilchem.communication.sms.Constant;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.activity.MainActivity;
import net.oilchem.communication.sms.util.SharedPreferenceUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

public class SampleCirclesDefault extends BaseSampleActivity implements OnClickListener {
	
	TextView enter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_circles);
        
        //判断是否新装或刚升级，如果不是直接跳到main
        if(OilchemApplication.getVersionName()
        		.equals(SharedPreferenceUtil.getString(Constant.SHAREDREFERENCES_CONFIG,
						Constant.SHAREDREFERENCES_LATEST_VERSION))){
        	Intent i = new Intent(SampleCirclesDefault.this, MainActivity.class);
			startActivity(i);
			finish();
			return;
        }
        
        enter = (TextView) findViewById(R.id.enter);
        enter.setOnClickListener(this);

        mAdapter = new TestFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        
        //We set this on the indicator, NOT the pager
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //Toast.makeText(SampleCirclesDefault.this, "Changed to page " + position, Toast.LENGTH_SHORT).show();
            	if(position == mAdapter.getCount()-1){
            		enter.setVisibility(View.VISIBLE);
            	}else{
            		enter.setVisibility(View.GONE);
            	}
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.enter:
			//将sp中记录更新
			SharedPreferenceUtil.setString(Constant.SHAREDREFERENCES_CONFIG,
					Constant.SHAREDREFERENCES_LATEST_VERSION, OilchemApplication.getVersionName());
			Intent i = new Intent(SampleCirclesDefault.this, MainActivity.class);
			startActivity(i);
			finish();
			break;
		default:
			break;
		}
	}
}