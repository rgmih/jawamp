package com.github.rgmih.jawamp.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rgmih.jawamp.message.CallErrorMessage;
import com.github.rgmih.jawamp.message.CallMessage;
import com.github.rgmih.jawamp.message.CallResultMessage;
import com.github.rgmih.jawamp.message.Message;
import com.github.rgmih.jawamp.message.MessageType;
import com.github.rgmih.jawamp.message.PrefixMessage;
import com.github.rgmih.jawamp.message.PublishMessage;
import com.github.rgmih.jawamp.message.SubscribeMessage;
import com.github.rgmih.jawamp.message.WelcomeMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MessageAdapter implements JsonDeserializer<Message>, JsonSerializer<Message> {

	private static final Logger logger = LoggerFactory.getLogger(MessageAdapter.class);
	
	public static abstract class JsonProcessor {
		public static JsonArray serialize(JsonSerializationContext context, Object... items) {
			JsonArray array = new JsonArray();
			for (Object item : items) {
				if (item instanceof JsonElement) {
					array.add((JsonElement) item);
				} else {
					array.add(context.serialize(item));
				}
			}
			return array;
		}
		
		public abstract JsonArray serialize(Message message, JsonSerializationContext context);

		public abstract Message deserialize(JsonArray json, JsonDeserializationContext context) throws JsonParseException;
	};
	
	protected Map<MessageType, JsonProcessor> adapters = new HashMap<MessageType, JsonProcessor>();
	
	public MessageAdapter() {
		adapters.put(MessageType.WELCOME, new JsonProcessor() {
			@Override
			public JsonArray serialize(Message message, JsonSerializationContext context) {
				WelcomeMessage welcome = (WelcomeMessage) message;
				return serialize(context, message.getType(), welcome.getSessionID(), welcome.getProtocolVersion(), welcome.getServerIdent());
			}
			@Override
			public Message deserialize(JsonArray json, JsonDeserializationContext context) throws JsonParseException {
				return new WelcomeMessage(json.get(1).getAsString(), json.get(2).getAsInt(), json.get(3).getAsString());
			}
		});
		adapters.put(MessageType.PREFIX, new JsonProcessor() {
			@Override
			public JsonArray serialize(Message m, JsonSerializationContext context) {
				PrefixMessage message = (PrefixMessage) m;
				return serialize(context, message.getType(), message.getPrefix(), message.getURI());
			}
			@Override
			public Message deserialize(JsonArray json, JsonDeserializationContext context) throws JsonParseException {
				return new PrefixMessage(json.get(1).getAsString(), json.get(2).getAsString());
			}
		});
		adapters.put(MessageType.CALL, new JsonProcessor() {
			@Override
			public JsonArray serialize(Message message, JsonSerializationContext context) {
				CallMessage call = (CallMessage) message;
				JsonArray array = serialize(context, message.getType(), call.getCallID(), call.getProcURI());
				for (JsonElement element : call.getArguments()) {
					array.add(element);
				}
				return array;
			}
			@Override
			public Message deserialize(JsonArray json, JsonDeserializationContext context) throws JsonParseException {
				List<JsonElement> arguments = new ArrayList<JsonElement>();
				for (int i = 3; i < json.size(); ++i) {
					arguments.add(json.get(i));
				}
				return new CallMessage(json.get(1).getAsString(), json.get(2).getAsString(), arguments);
			}
		});
		adapters.put(MessageType.CALLRESULT, new JsonProcessor() {
			@Override
			public JsonArray serialize(Message message, JsonSerializationContext context) {
				CallResultMessage callResult = (CallResultMessage) message;
				return serialize(context, message.getType(), callResult.getCallID(), callResult.getPayload());
			}
			@Override
			public Message deserialize(JsonArray json, JsonDeserializationContext context) throws JsonParseException {
				return new CallResultMessage(json.get(1).getAsString(), json.get(2));
			}
		});
		adapters.put(MessageType.CALLERROR, new JsonProcessor() {
			@Override
			public JsonArray serialize(Message message, JsonSerializationContext context) {
				CallErrorMessage callError = (CallErrorMessage) message;
				JsonArray array = serialize(context, message.getType(), callError.getCallID(), callError.getErrorURI(), callError.getErrorDesc());
				if (callError.getErrorDetails() != null) {
					array.add(callError.getErrorDetails());
				}
				return array;
			}
			@Override
			public Message deserialize(JsonArray json, JsonDeserializationContext context) throws JsonParseException {
				if (json.size() > 4) {
					return new CallErrorMessage(json.get(1).getAsString(), json.get(2).getAsString(), json.get(3).getAsString(), json.get(4));
				} else {
					return new CallErrorMessage(json.get(1).getAsString(), json.get(2).getAsString(), json.get(3).getAsString());
				}
			}
		});
		adapters.put(MessageType.SUBSCRIBE, new JsonProcessor() {
			@Override
			public JsonArray serialize(Message message, JsonSerializationContext context) {
				return serialize(context, message.getType(), ((SubscribeMessage) message).getTopicURI());
			}
			@Override
			public Message deserialize(JsonArray json, JsonDeserializationContext context) throws JsonParseException {
				return new SubscribeMessage(json.get(1).getAsString());
			}
		});
		adapters.put(MessageType.PUBLISH, new JsonProcessor() {
			@Override
			public JsonArray serialize(Message message, JsonSerializationContext context) {
				PublishMessage publish = (PublishMessage) message;
				JsonArray array = serialize(context, message.getType(), publish.getTopicURI(), publish.getEvent());
				// TODO extra fields
				return array;
			}
			@Override
			public Message deserialize(JsonArray json, JsonDeserializationContext context) throws JsonParseException {
				return new PublishMessage(json.get(1).getAsString(), json.get(2));
			}
		});
	}
	
	@Override
	public Message deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonArray array = json.getAsJsonArray();
		MessageType messageType = context.deserialize(array.get(0), MessageType.class);
		JsonProcessor adapter = adapters.get(messageType);
		if (adapter != null) {
			return adapter.deserialize(array, context);
		}
		return null;
	};
	
	private static Gson gson;
	static {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeHierarchyAdapter(Message.class, new MessageAdapter());
		builder.registerTypeAdapter(MessageType.class, new MessageTypeAdapter());
		gson = builder.create();
	}
	
	public static Message parse(String json) {
		return gson.fromJson(json, Message.class);
	}
	
	public static String toJSON(Message message) {
		String json = gson.toJson(message);
		logger.debug(json);
		return json;
	}

	@Override
	public JsonElement serialize(Message message, Type type, JsonSerializationContext context) {
		JsonProcessor adapter = adapters.get(message.getType());
		if (adapter != null) {
			return adapter.serialize(message, context);
		}
		throw new RuntimeException("no adapter for message type=" + message.getType());
	}
}
