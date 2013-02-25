package com.github.rgmih.jawamp.message;

public class PrefixMessage extends Message {
	private String prefix;
	private String uri;
	
	public PrefixMessage(String prefix, String uri) {
		super(MessageType.PREFIX);
		this.prefix = prefix;
		this.uri = uri;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getURI() {
		return uri;
	}
}
