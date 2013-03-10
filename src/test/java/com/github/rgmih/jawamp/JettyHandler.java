package com.github.rgmih.jawamp;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.OnTextMessage;
import org.eclipse.jetty.websocket.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyHandler extends WebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(JettyHandler.class);
	
	private Server server = new Server();
	
	public JettyHandler() {
		server.registerHandler("http://example.com/empty", new EmptyCallHandler());
		server.registerHandler("http://example.com/add", new AddCallHandler());
		server.registerHandler("http://example.com/error", new ErrorCallHandler());
	}
	
	private class WampWebSocket extends ServerConnection implements OnTextMessage {

		private Connection jettyConnection;
		
		public WampWebSocket() {
			super(server);
		}
		
		@Override
		public void onOpen(Connection connection) {
			this.jettyConnection = connection;
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
		public void sendMessage(String data) throws IOException {
			jettyConnection.sendMessage(data);
		}
	}
	
	@Override
	public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
		logger.debug(protocol);
		return new WampWebSocket();
	}

}
