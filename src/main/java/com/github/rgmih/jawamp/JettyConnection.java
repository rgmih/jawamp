package com.github.rgmih.jawamp;

import java.io.IOException;

import org.eclipse.jetty.websocket.WebSocket.OnTextMessage;

public class JettyConnection extends WampConnection implements OnTextMessage {

	// private static final Logger logger = LoggerFactory.getLogger(JettyConnection.class);
	
	private Connection connection;
	
	@Override
	public void onOpen(Connection connection) {
		this.connection = connection;
		onOpen();
	}

	@Override
	public void onClose(int closeCode, String message) {
		onClose();
	}

	@Override
	public void onMessage(String data) {
		super.onMessage(data);
	}

	@Override
	public void sendMessage(String data) throws IOException {
		connection.sendMessage(data);
	}

}
