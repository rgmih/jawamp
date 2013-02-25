package com.github.rgmih.jawamp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rgmih.jawamp.Server.CallContext;
import com.github.rgmih.jawamp.Server.CallHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class AddCallHandler implements CallHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(AddCallHandler.class);
	
	@Override
	public CallResult invoke(String procURI, List<JsonElement> arguments, CallContext context) throws CallError {
		logger.info("add-call-handler invoked");
		int x = arguments.get(0).getAsInt();
		int y = arguments.get(1).getAsInt();
		return new CallResult(new JsonPrimitive(x + y));
	}
}
