package com.github.rgmih.jawamp.message;


public class SubscribeMessage extends Message {
	private String topicURI;
	
	public SubscribeMessage(String topicURI) {
		super(MessageType.SUBSCRIBE);
		this.topicURI = topicURI;
	}
	
	public String getTopicURI() {
		return topicURI;
	}
}
