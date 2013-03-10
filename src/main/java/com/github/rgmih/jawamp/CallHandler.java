package com.github.rgmih.jawamp;

import java.util.List;

import com.google.gson.JsonElement;

/**
 * Implement {@link CallHandler} interface and register 
 * in {@link Server} instance to provide single procedure
 * handling.
 */
public interface CallHandler {
	/**
	 * This method is invoked when call request is received by {@link Server}.
	 * 
	 * @param procURI Call procedure URI. It may be useful if one handler was registered for many URIs
	 * @param arguments Call arguments as a list of JSON elements
	 * @param connection Server connection processing current call
	 * @return Return {@link CallResult} instance or throw {@link CallError} if unable to process call
	 * @throws CallError This exception is thrown when handler is unable to process request
	 */
	CallResult invoke(String procURI, List<JsonElement> arguments, ServerConnection connection) throws CallError;
}