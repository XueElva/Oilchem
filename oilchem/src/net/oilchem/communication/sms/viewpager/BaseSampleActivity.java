package net.oilchem.communication.sms.viewpager;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.viewpagerindicator.PageIndicator;

public abstract class BaseSampleActivity extends FragmentActivity {

    TestFragmentAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
