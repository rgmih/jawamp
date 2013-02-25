package com.github.rgmih.jawamp;

import java.io.IOException;

import com.github.rgmih.jawamp.message.Message;
import com.github.rgmih.jawamp.util.MessageAdapter;

public abstract class Connection {

	// private static final Logger logger = LoggerFactory.getLogger(Connection.class);
	
	protected void onOpen() {}
	
	protected void onClose() {}
	
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
