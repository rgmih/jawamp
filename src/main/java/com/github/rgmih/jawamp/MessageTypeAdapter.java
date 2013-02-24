package com.github.rgmih.jawamp;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MessageTypeAdapter implements JsonSerializer<MessageType>, JsonDeserializer<MessageType> {
	@Override
	public JsonElement serialize(MessageType msgType, Type type,JsonSerializationContext context) {
		return new JsonPrimitive(msgType.ordinal());
	}

	@Override
	public MessageType deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		return MessageType.values()[json.getAsInt()];
	}
};
