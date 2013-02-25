package com.github.rgmih.jawamp;

public class CallError extends Exception {

	private static final long serialVersionUID = 551481029545370660L;
	
	public CallError(String procURI, String message) {
		super(message);
	}
}