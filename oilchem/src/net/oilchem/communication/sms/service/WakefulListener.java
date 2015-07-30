/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Advanced Android Development_
    http://commonsware.com/AdvAndroid
 */

package net.oilchem.communication.sms.service;

import net.oilchem.communication.sms.OilchemApplication;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

public class WakefulListener implements WakefulIntentService.AlarmListener {
	public static final String ACTION = "net.oilchem.communication.sms.service.KEEP_CONNECTION_ALIVE";
	public static final int PEROID = 60 * 1000;

	@Override
	public void scheduleAlarms(AlarmManager mgr, PendingIntent pi, Context ctxt) {
		int peroidTime = PEROID;
		try {
			peroidTime = OilchemApplication.getConfig().getConfig().getGetPushTimeInterval();
		} catch(Exception e) {}
		
		//根据不同手机修改Repeat定义
		String brand = android.os.Build.BRAND;
		String model = android.os.Build.MODEL;
		if("Xiaomi".equals(brand)){
			if(null != model && model.contains("MI 3")){
				mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
		                SystemClock.elapsedRealtime() + peroidTime,
		                peroidTime * 2, pi);
			}else{
				mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
		                SystemClock.elapsedRealtime() + peroidTime,
		                peroidTime, pi);
			}
		}else if("Huawei".equals(brand) && null != model && model.contains("HUAWEI P6-C00")){
			mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
	                SystemClock.elapsedRealtime() + peroidTime,
	                peroidTime * 2, pi);
		}else{
			mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
	                SystemClock.elapsedRealtime() + peroidTime,
	                peroidTime, pi);
		}
	    //Log.i("Repeating", "" + peroidTime*2);
	}

	@Override
	public void sendWakefulWork(Context ctxt) {
	    WakefulIntentService.sendWakefulWork(ctxt, PullNotificationService.class);
	}

	@Override
	public long getMaxAge() {
		return PEROID;
	}
}