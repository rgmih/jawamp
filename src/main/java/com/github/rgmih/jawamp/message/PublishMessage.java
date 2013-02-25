package com.github.rgmih.jawamp.message;

import java.util.List;

import com.google.gson.JsonElement;

public class PublishMessage extends Message {
	private String topicURI;
	private JsonElement event;
	
	private boolean excludeMe = false;
	private List<String> exclude;
	private List<String> eligible;
	
	private final EventMessage eventMessage;
	
	public PublishMessage(String topicURI, JsonElement event) {
		this(topicURI, event, false);
	}
	
	public PublishMessage(String topicURI, JsonElement event, List<String> exclude) {
		this(topicURI, event, false);
		this.exclude = exclude;
	}
	
	public PublishMessage(String topicURI, JsonElement event, boolean excludeMe) {
		super(MessageType.PUBLISH);
		this.topicURI = topicURI;
		this.event = event;
		this.excludeMe = excludeMe;
		eventMessage = new EventMessage(topicURI, event);
	}
	
	public PublishMessage(String topicURI, JsonElement event, List<String> exclude, List<String> eligible) {
		this(topicURI, event, false);
		this.exclude = exclude;
		this.eligible = eligible;
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
