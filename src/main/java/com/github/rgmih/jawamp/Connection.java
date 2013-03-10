package com.github.rgmih.jawamp;

import java.io.IOException;

import com.github.rgmih.jawamp.message.Message;
import com.github.rgmih.jawamp.util.MessageAdapter;

/**
 * Base abstract class for both server and client WAMP connections.
 * It shouldn't be used as is, inherit {@link ServerConnection} or
 * {@link Client} instead.
 */
public abstract class Connection {

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
