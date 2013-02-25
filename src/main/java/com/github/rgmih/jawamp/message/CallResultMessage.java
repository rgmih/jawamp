package com.github.rgmih.jawamp.message;

import com.google.gson.JsonElement;

public class CallResultMessage extends Message {
	
	private String callID;
	
	private JsonElement payload;
	
	public CallResultMessage(String callID, JsonElement payload) {
		super(MessageType.CALLRESULT);
		this.callID = callID;
		this.payload = payload;
	}

	public String getCallID() {
		return callID;
	}

	public JsonElement getPayload() {
		return payload;
	}
}
