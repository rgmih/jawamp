package com.github.rgmih.jawamp;

import java.util.UUID;

public abstract class ServerConnection extends Connection {
	
	public ServerConnection(Server server) {
		
	}
	
	UUID id = UUID.randomUUID();
	
	@Override
	public void onOpen() {
		super.onOpen();
		
		// send WELCOME message
		sendMessage(new WelcomeMessage(id.toString(), 1, Server.IDENT));
	}


	@Override
	protected void onMessage(Message message) {
		
	}

}
