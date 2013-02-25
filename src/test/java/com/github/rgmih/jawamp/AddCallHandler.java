package com.github.rgmih.jawamp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rgmih.jawamp.Server.CallContext;
import com.github.rgmih.jawamp.Server.CallError;
import com.github.rgmih.jawamp.Server.CallHandler;
import com.github.rgmih.jawamp.Server.CallResult;
import com.google.gson.JsonElement;

public class AddCallHandler implements CallHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(AddCallHandler.class);
	
	@Override
	public CallResult invoke(String procURI, List<JsonElement> arguments, CallContext context) throws CallError {
		logger.info("add-call-handler invoked");
		return new CallResult(5);
	}
}
