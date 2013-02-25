package com.github.rgmih.jawamp;

import com.google.gson.JsonElement;

public class CallError extends Exception {

	private static final long serialVersionUID = 551481029545370660L;
	
	private String errorURI;
	private String errorDesc;
	private JsonElement errorDetails;
	
	public CallError(String errorURI, String errorDesc) {
		super(errorURI);
		this.errorURI = errorURI;
		this.errorDesc = errorDesc;
	}
	
	public CallError(String errorURI, String errorDesc, JsonElement errorDetails) {
		this(errorURI, errorDesc);
		this.errorDetails = errorDetails;
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