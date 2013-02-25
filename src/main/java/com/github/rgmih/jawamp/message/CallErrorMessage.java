package com.github.rgmih.jawamp.message;

import com.github.rgmih.jawamp.CallError;
import com.google.gson.JsonElement;

public class CallErrorMessage extends Message {

	private String callID;
	private String errorURI;
	private String errorDesc;
	private JsonElement errorDetails;
	
	public CallErrorMessage(String callID, CallError error) {
		this(callID, error.getErrorURI(), error.getErrorDesc(), error.getErrorDetails());
	}
	
	public CallErrorMessage(String callID, String errorURI, CallError error) {
		this(callID, error);
		this.errorURI = errorURI;
	}
	
	public CallErrorMessage(String callID, String errorURI, String errorDesc) {
		super(MessageType.CALLERROR);
		this.callID = callID;
		this.errorURI = errorURI;
		this.errorDesc = errorDesc;
	}
	
	public CallErrorMessage(String callID, String errorURI, String errorDesc, JsonElement errorDetails) {
		this(callID, errorURI, errorDesc);
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
	
	public CallError toError() {
		return new CallError(errorURI, errorDesc, errorDetails);
	}
}
