package net.oilchem.communication.sms.handler;

import net.oilchem.communication.sms.data.model.OilResponse;
import net.oilchem.communication.sms.data.model.OilResponseData;
import net.oilchem.communication.sms.util.IApi.API;
import net.oilchem.communication.sms.util.IApi.RequestMethod;

public interface IRequestListener {
	public void onRequestSuccess(RequestMethod method, API api, OilResponseData response, HandlerBase handler);
	public void onRequestError(RequestMethod method, API api, HandlerBase handler);
}
