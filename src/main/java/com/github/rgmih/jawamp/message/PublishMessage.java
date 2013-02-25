package com.github.rgmih.jawamp.message;

import java.util.List;

import com.google.gson.JsonElement;

public class PublishMessage extends Message {
	private String topicURI;
	private JsonElement event;
	
	private boolean excludeMe;
	private List<String> exclude;
	private List<String> eligible;
	
	private final EventMessage eventMessage;
	
	public PublishMessage(String topicURI, JsonElement event) {
		super(MessageType.PUBLISH);
		this.topicURI = topicURI;
		this.event = event;
		eventMessage = new EventMessage(topicURI, event);
	}

	public String getTopicURI() {
		return topicURI;
	}

	public JsonElement getEvent() {
		return event;
	}

	public boolean isExcludeMe() {
		return excludeMe;
	}

	public List<String> getExclude() {
		return exclude;
	}

	public List<String> getEligible() {
		return eligible;
	}
	
	public EventMessage toEventMessage() {
		return eventMessage;
	}
}
