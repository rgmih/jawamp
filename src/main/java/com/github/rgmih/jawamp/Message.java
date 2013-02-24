package com.github.rgmih.jawamp;

public abstract class Message {
	
	// private static final Logger logger = LoggerFactory.getLogger(Message.class);
	
	protected MessageType type;
	
	protected Message(MessageType type) {
		this.type = type;
	}

	public MessageType getType() {
		return type;
	}
}
