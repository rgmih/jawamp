package com.github.rgmih.jawamp.message;

import java.util.List;

import com.google.gson.JsonElement;

public class CallMessage extends Message {

	private String callID;
	
	private String procURI;
	
	private List<JsonElement> arguments;
	
	public CallMessage(String callID, String procURI, List<JsonElement> arguments) {
		super(MessageType.CALL);
		this.callID = callID;
		this.procURI = procURI;
		this.arguments = arguments;
	}
	
	public String getCallID() {
		return callID;
	}

	public String getProcURI() {
		return procURI;
	}

	public List<JsonElement> getArguments() {
		return arguments;
	}
}
