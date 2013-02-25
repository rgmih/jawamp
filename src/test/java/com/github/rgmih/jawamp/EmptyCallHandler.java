package com.github.rgmih.jawamp;

import java.util.List;

import com.github.rgmih.jawamp.Server.CallHandler;
import com.google.gson.JsonElement;

public class EmptyCallHandler implements CallHandler {

	@Override
	public CallResult invoke(String procURI, List<JsonElement> arguments, ServerConnection connection) throws CallError {
		return new CallResult(null);
	}

}
