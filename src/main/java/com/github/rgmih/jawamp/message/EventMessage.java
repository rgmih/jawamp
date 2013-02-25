package com.github.rgmih.jawamp.message;

import com.google.gson.JsonElement;

public class EventMessage extends Message {
	
	private String topicURI;
	private JsonElement event;
	
	public EventMessage(String topicURI, JsonElement event) {
		super(MessageType.EVENT);
		this.topicURI = topicURI;
		this.event = event;
	}

	public String getTopicURI() {
		return topicURI;
	}

	public JsonElement getEvent() {
		return event;
	}
}
