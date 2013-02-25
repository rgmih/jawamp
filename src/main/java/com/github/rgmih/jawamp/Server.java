package com.github.rgmih.jawamp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rgmih.jawamp.message.PublishMessage;
import com.google.gson.JsonElement;

public class Server {
	public static final String IDENT = "jawamp/0.1.0";
	
	private static final Logger logger = LoggerFactory.getLogger(Server.class);
	
	public static class CallContext {
		
	}
	
	public static interface CallHandler {
		CallResult invoke(String procURI, List<JsonElement> arguments, CallContext context) throws CallError;
	}

	protected final Map<String, CallHandler> callHandlers = new HashMap<String, CallHandler>();
	
	public void addHandler(String procURI, CallHandler handler) {
		callHandlers.put(procURI, handler);
		logger.info("call handler '{}' registered for procURI='{}'", handler, procURI);
	}
	
	public void removeHandler(CallHandler handler) {
		
	}
	
	public CallResult call(String procURI, List<JsonElement> arguments, CallContext context) throws CallError {
		CallHandler handler = callHandlers.get(procURI);
		if (handler != null) {
			logger.debug("handler found for procURI={}; processing call", procURI);
			return handler.invoke(procURI, arguments, context);
		} else {
			logger.warn("no handler registered for procURI='{}'; error", procURI);
			throw new CallError(procURI, "procURI not supported");
		}
	}
	
	public static abstract class Listener {
		public void onPublish(PublishMessage message) {}
	}
	
	private final Set<Listener> listeners = new HashSet<Listener>();
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void publish(PublishMessage message) {
		for (Listener listener : listeners) {
			listener.onPublish(message);
		}
	}
}
