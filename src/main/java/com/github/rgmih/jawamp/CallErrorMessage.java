package com.github.rgmih.jawamp;

import com.google.gson.JsonElement;

public class CallErrorMessage extends Message {

	private String callID;
	private String errorURI;
	private String errorDesc;
	private JsonElement errorDetails;
	
	public CallErrorMessage(String callID, CallError error) {
		super(MessageType.CALLERROR);
		this.callID = callID;
		this.errorURI = error.getErrorURI();
		this.errorDesc = error.getErrorDesc();
	}
	
	public CallErrorMessage(String callID, String errorURI, String errorDesc) {
		super(MessageType.CALLERROR);
		this.callID = callID;
		this.errorURI = errorURI;
		this.errorDesc = errorDesc;
	}
	
	public CallErrorMessage(String callID, String errorURI, String errorDesc, JsonElement errorDetails) {
		super(MessageType.CALLERROR);
		this.callID = callID;
		this.errorURI = errorURI;
		this.errorDesc = errorDesc;
		this.errorDetails = errorDetails;
	}

	public String getCallID() {
		return callID;
	}

	public String getErrorURI() {
		return errorURI;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public JsonElement getErrorDetails() {
		return errorDetails;
	}
}
