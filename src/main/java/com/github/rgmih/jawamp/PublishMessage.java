package com.github.rgmih.jawamp;

import java.util.List;

import com.google.gson.JsonElement;

public class PublishMessage extends Message {
	private String topicURI;
	private JsonElement event;
	
	private boolean excludeMe;
	private List<String> exclude;
	private List<String> eligible;
	
	public PublishMessage(String topicURI, JsonElement event) {
		super(MessageType.PUBLISH);
		this.topicURI = topicURI;
		this.event = event;
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
}
