package net.oilchem.communication.sms.receiver;

import net.oilchem.communication.sms.util.NetworkUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		NetworkUtil.updateNetInfo();
	}
}
