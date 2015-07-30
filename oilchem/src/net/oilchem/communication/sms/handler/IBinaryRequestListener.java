package net.oilchem.communication.sms.handler;

public interface IBinaryRequestListener {
	public void onBinaryRequestSuccess(byte[] bytes, BinaryHandlerBase handler);
}
