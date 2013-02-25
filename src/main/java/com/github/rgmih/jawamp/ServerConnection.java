package com.github.rgmih.jawamp;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rgmih.jawamp.message.CallErrorMessage;
import com.github.rgmih.jawamp.message.CallMessage;
import com.github.rgmih.jawamp.message.CallResultMessage;
import com.github.rgmih.jawamp.message.Message;
import com.github.rgmih.jawamp.message.PrefixMessage;
import com.github.rgmih.jawamp.message.PublishMessage;
import com.github.rgmih.jawamp.message.SubscribeMessage;
import com.github.rgmih.jawamp.message.WelcomeMessage;


public abstract class ServerConnection extends Connection {
	
	private static final Logger logger = LoggerFactory.getLogger(ServerConnection.class);
	
	private final Server server;
	
	public ServerConnection(Server server) {
		this.server = server;
		this.server.addListener(new Server.Listener() {
			@Override
			public void onPublish(PublishMessage message) {
				sendMessage(message);
			}
		});
		logger.info("connection created; connection={}", id);
	}
	
	UUID id = UUID.randomUUID();
	
	@Override
	public void onOpen() {
		super.onOpen();
		
		logger.info("connection opened, sending WELCOME; connection={}", id);
		// send WELCOME message
		sendMessage(new WelcomeMessage(id.toString(), 1, Server.IDENT));
		logger.debug("WELCOME message sent; connection connection={}, protocolVersion=1, serverIdent={}", id, Server.IDENT);
	}


	@Override
	protected void onMessage(Message message) {
		logger.debug("message recieved; connection={}, type={}", id, message.getType());
		
		switch (message.getType()) {
		case PREFIX:
			PrefixMessage prefixMessage = (PrefixMessage) message;
			bindPrefix(prefixMessage.getPrefix(), prefixMessage.getURI());
			break;
		case CALL:
			CallMessage callMessage = (CallMessage) message;
			logger.info("call message id='{}' received for procURI='{}'; connection={}", callMessage.getCallID(), callMessage.getProcURI(), id);
			try {
				String procURI = tryParseCURIE(callMessage.getProcURI());
				CallResult result = server.call(procURI, callMessage.getArguments(), new Server.CallContext());
				logger.debug("call processed; sending CALLRESULT message; connection={}, call id={}", id, callMessage.getCallID());
				sendMessage(new CallResultMessage(callMessage.getCallID(), result.getPayload()));
			} catch (CallError e) {
				logger.warn("call error occurred, sending CALLERROR; procURI={}, call id={}, connection={}", callMessage.getProcURI(), callMessage.getCallID(), id);
				sendMessage(new CallErrorMessage(callMessage.getCallID(), toCURIE(e.getErrorURI()), e));
			}
			break;
		case SUBSCRIBE:
			SubscribeMessage subscribeMessage = (SubscribeMessage) message;
			String topicURI = tryParseCURIE(subscribeMessage.getTopicURI());
			logger.info("subscribing to topicURI='{}'; connection={}", topicURI, id);
			break;
		case PUBLISH:
			PublishMessage publishMessage = (PublishMessage) message;
			logger.info("event published for topicURI={}; connection={}", publishMessage.getTopicURI(), id);
			server.publish(publishMessage);
			break;
		default:
			logger.warn("unexpected message type={}; connection={}", message.getType());
			break;
		}
	}

	private final Map<String, String> prefixes = new HashMap<String, String>();
	
	private void bindPrefix(String prefix, String URI) {
		String oldURI = prefixes.put(prefix, URI);
		logger.info("prefix='{}' for URI='{}' bound; connection={}", prefix, URI, id);
		
		if (oldURI != null) {
			logger.warn("overriding prefix={} binding; old URI={}, new URI={}, connection={}", prefix, URI, oldURI, id);
		}
	}
	
	private String tryParseCURIE(String URI) {
		for (Entry<String, String> entry : prefixes.entrySet()) {
			if (URI.startsWith(entry.getKey() + ":")) {
				String s = entry.getValue() + URI.substring(entry.getKey().length() + 1);
				logger.debug("found prefix={} binding for URI={}; parsed into {}", entry.getKey(), URI, s);
				return s;
			}
		}
		return URI;
	}
	
	private String toCURIE(String URI) {
		for (Entry<String, String> entry : prefixes.entrySet()) {
			if (URI.startsWith(entry.getValue())) {
				String s = entry.getKey() + ":" + URI.substring(entry.getValue().length());
				logger.debug("found prefix={} binding for URI={}; using as {}", entry.getKey(), URI, s);
				return s;
			}
		}
		return URI;
	}
}
