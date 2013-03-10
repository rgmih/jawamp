package com.github.rgmih.jawamp;

import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ErrorCallHandler implements CallHandler {

	@Override
	public CallResult invoke(String procURI, List<JsonElement> arguments, ServerConnection connection) throws CallError {
		throw new CallError("http://example.com/error", "error description", new JsonPrimitive("details"));
	}

}
