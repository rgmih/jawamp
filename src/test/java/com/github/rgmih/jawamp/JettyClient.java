package com.github.rgmih.jawamp;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.eclipse.jetty.websocket.WebSocket;

public class JettyClient extends Client implements WebSocket.OnTextMessage {

	private Connection jettyConnection;
	
	public JettyClient() {
		super(Executors.newCachedThreadPool());
	}
	
	@Override
	public void onOpen(Connection connection) {
		jettyConnection = connection;
		super.onOpen();
	}

	@Override
	public void onClose(int closeCode, String message) {
		super.onClose();
	}
	
	@Override
	public void onMessage(String data) {
		super.onMessage(data);
	}

	@Override
	protected void sendMessage(String message) throws IOException {
		jettyConnection.sendMessage(message);
	}

}
