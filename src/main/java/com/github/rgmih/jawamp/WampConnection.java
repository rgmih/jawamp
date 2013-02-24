package com.github.rgmih.jawamp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WampConnection {

	private static final Logger logger = LoggerFactory.getLogger(WampConnection.class);
	
	public void onOpen() {
		sendMessage(new WelcomeMessage("123", 1, "jawamp/0.1.0"));
	}
	
	public void onClose() {
		
	}
	
	protected void onMessage(Message message) {
		logger.info("message received; type={}", message.getType());
	}
	
	protected void onMessage(String message) {
		onMessage(MessageAdapter.parse(message));
	}
	
	protected void sendMessage(Message message) {
		try {
			sendMessage(MessageAdapter.toJSON(message));
		} catch (IOException e) {
			// TODO
		}
	}
	
	protected abstract void sendMessage(String message) throws IOException;
}
