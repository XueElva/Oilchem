package net.oilchem.communication.sms.handler;

import java.util.HashMap;
import java.util.Map.Entry;

public class HandlerFactory {
	private static HashMap<String, HandlerBase> handlerMaper;
	
	public static HandlerBase createHandler(IRequestListener listener) {
		HandlerBase handler = new HandlerBase();
		handler.setIRequestListener(listener);
		return handler;
	}
	
	public static HandlerBase getHandler(HandlerParams params, IRequestListener listener) {
		if (null == handlerMaper) {
			handlerMaper = new HashMap<String, HandlerBase>();
		}
		if (!handlerMaper.containsKey(params.toString()) ) {
			HandlerBase handler = createHandler(listener);
			handler.setHandlerParams(params);
			handlerMaper.put(params.toString(), handler);
		}else if( null == handlerMaper.get(params.toString()) ){
			HandlerBase handler = createHandler(listener);
			handler.setHandlerParams(params);
			handlerMaper.put(params.toString(), handler);
		}else if(null == handlerMaper.get(params.toString()).getIRequestListener()){
			HandlerBase handler = createHandler(listener);
			handler.setHandlerParams(params);
			handlerMaper.put(params.toString(), handler);
		}
		return handlerMaper.get(params.toString());
	}
	
	public static HandlerBase getHandler(HandlerParams params, IRequestListener listener, boolean canMulti) {
		if (!canMulti) {
			return getHandler(params, listener);
		}
		HandlerBase handler = createHandler(listener);
		handler.setHandlerParams(params);
		return handler;
	}

	public static void clearHandler(HandlerBase handlerBase) {
		if (handlerMaper == null) {
			return;
		}
		String currentKey = null;
		for (Entry<String, HandlerBase> entry: handlerMaper.entrySet()) {
			if (entry.getValue() == handlerBase) {
				currentKey = entry.getKey();
				break;
			}
		}
		if (handlerMaper.containsKey(currentKey)) {
			handlerMaper.remove(currentKey);
		}
	}
}
