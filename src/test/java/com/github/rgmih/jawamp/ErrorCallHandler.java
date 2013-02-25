package com.github.rgmih.jawamp;

import java.util.List;

import com.github.rgmih.jawamp.Server.CallContext;
import com.github.rgmih.jawamp.Server.CallHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ErrorCallHandler implements CallHandler {

	@Override
	public CallResult invoke(String procURI, List<JsonElement> arguments, CallContext context) throws CallError {
		throw new CallError("http://example.com/error", "error description", new JsonPrimitive("details"));
	}

}