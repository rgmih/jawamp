package com.github.rgmih.jawamp;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyHandler extends WebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(JettyHandler.class);
	
	@Override
	public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
		logger.debug(protocol);
		return new JettyConnection();
	}

}
