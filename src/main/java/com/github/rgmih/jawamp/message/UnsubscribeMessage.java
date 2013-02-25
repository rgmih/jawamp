package com.github.rgmih.jawamp.message;

public class UnsubscribeMessage extends Message {
	
	private String topicURI;
	
	public UnsubscribeMessage(String topicURI) {
		super(MessageType.UNSUBSCRIBE);
		
		this.topicURI = topicURI;
	}
	
	public String getTopicURI() {
		return topicURI;
	}
}
