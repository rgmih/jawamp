package com.github.rgmih.jawamp;

import java.util.List;

import com.github.rgmih.jawamp.Server.CallContext;
import com.github.rgmih.jawamp.Server.CallError;
import com.github.rgmih.jawamp.Server.CallHandler;
import com.github.rgmih.jawamp.Server.CallResult;
import com.google.gson.JsonElement;

public class EmptyCallHandler implements CallHandler {

	@Override
	public CallResult invoke(String procURI, List<JsonElement> arguments, CallContext context) throws CallError {
		return new CallResult(null);
	}

}
