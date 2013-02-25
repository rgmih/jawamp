package com.github.rgmih.jawamp;

public class CallError extends Exception {

	private static final long serialVersionUID = 551481029545370660L;
	
	private String errorURI;
	private String errorDesc;
	
	public CallError(String errorURI, String errorDesc) {
		super(errorURI);
		this.errorURI = errorURI;
		this.errorDesc = errorDesc;
	}

	public String getErrorURI() {
		return errorURI;
	}

	public String getErrorDesc() {
		return errorDesc;
	}
}