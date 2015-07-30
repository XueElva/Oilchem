package net.oilchem.communication.sms.handler;

import com.loopj.android.http.BinaryHttpResponseHandler;

public class BinaryHandlerBase extends BinaryHttpResponseHandler {
	private boolean processing = false;
	private IBinaryRequestListener listener;

	public void setListener(IBinaryRequestListener listener) {
		this.listener = listener;
	}

	@Override
	public void onSuccess(byte[] bytes) {
		if (null != listener) {
			listener.onBinaryRequestSuccess(bytes, this);
		}
	}
	
	@Override
	public void onFinish() {
		processing = false;
	}

	@Override
	public void onStart() {
		processing = true;
	}
	
	@Override
	public void onFailure(Throwable error, String content) {
	}

	public boolean isProcessing() {
		return processing;
	}
}
