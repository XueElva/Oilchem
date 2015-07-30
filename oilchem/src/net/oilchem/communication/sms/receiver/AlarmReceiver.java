/***
  Copyright (c) 2011 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package net.oilchem.communication.sms.receiver;

import net.oilchem.communication.sms.service.WakefulIntentService;
import net.oilchem.communication.sms.service.WakefulIntentService.AlarmListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context ctxt, Intent intent) {
		AlarmListener listener = null;
		try {
			listener = getListener(ctxt);
		} catch(Exception e) {
		}
		if (listener != null) {
			if (intent.getAction() == null) {
				SharedPreferences prefs = ctxt.getSharedPreferences(WakefulIntentService.NAME, 0);
				prefs.edit().putLong(WakefulIntentService.LAST_ALARM, System.currentTimeMillis()).commit();
				listener.sendWakefulWork(ctxt);
			} else {
				WakefulIntentService.scheduleAlarms(listener, ctxt, true);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private WakefulIntentService.AlarmListener getListener(Context ctxt) {
		Class<AlarmListener> cls = null;
		try {
			cls = (Class<AlarmListener>) Class.forName("net.oilchem.communication.sms.service.WakefulListener");
			return (cls.newInstance());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Listener class not found", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Listener is not public or lacks public constructor", e);
		} catch (InstantiationException e) {
			throw new RuntimeException("Could not create instance of listener", e);
		} 
	}
}