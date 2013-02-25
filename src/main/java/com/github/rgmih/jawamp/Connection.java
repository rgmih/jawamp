package com.github.rgmih.jawamp;

import java.io.IOException;

public abstract class Connection {

	// private static final Logger logger = LoggerFactory.getLogger(Connection.class);
	
	public void onOpen() {}
	
	public void onClose() {}
	
	protected abstract void onMessage(Message message);
	
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
