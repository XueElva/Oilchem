package net.oilchem.communication.sms.activity;

import net.oilchem.communication.sms.Constant;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.util.SharedPreferenceUtil;
import net.oilchem.communication.sms.util.UIUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.test.UiThreadTest;
import android.text.TextUtils;
import android.util.Log;

public class OilActivityBase extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		if (TextUtils.equals(OilchemApplication.getResourceString(R.string.taber_setting_theme_open),
				SharedPreferenceUtil.getString(Constant.SHAREDREFERENCES_CONFIG, Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_THEME,
						OilchemApplication.getResourceString(R.string.taber_setting_theme_close)))) {
			this.setTheme(R.style.OilNighttime);
		} else {
			this.setTheme(R.style.OilDaytime);
		}
		if (null != UIUtil.getThemeMapper() && !UIUtil.getThemeMapper().containsKey(this.getClass().getSimpleName())) {
			UIUtil.setThemeMapper(this.getClass().getSimpleName());
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d("push", "onNewIntent" + this.getClass().getSimpleName());
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
		if (UIUtil.isChangedTheme(this.getClass().getSimpleName())) {
			this.finish();
			startActivity(new Intent(this, this.getClass()));
			UIUtil.setThemeMapper(this.getClass().getSimpleName());
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
}
