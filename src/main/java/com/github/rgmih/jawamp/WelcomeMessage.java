package com.github.rgmih.jawamp;


public class WelcomeMessage extends Message {
	private String sessionID;
	private Integer protocolVersion;
	private String serverIdent;
	
	public WelcomeMessage(String sessionID, int protocolVersion, String serverIdent) {
		super(MessageType.WELCOME);
		this.sessionID = sessionID;
		this.protocolVersion = protocolVersion;
		this.serverIdent = serverIdent;
	}
	
	public int getProtocolVersion() {
		return protocolVersion;
	}

	public String getServerIdent() {
		return serverIdent;
	}

	public String getSessionID() {
		return sessionID;
	}
}
