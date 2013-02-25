package com.github.rgmih.jawamp;

import com.google.gson.JsonElement;

public class CallResult {
	private JsonElement payload;
	
	public CallResult(JsonElement json) {
		this.payload = json;
	}

	public JsonElement getPayload() {
		return payload;
	}
}