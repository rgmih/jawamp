package com.github.rgmih.jawamp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rgmih.jawamp.message.Message;
import com.google.gson.JsonElement;

/**
 * {@link Server} connects {@link ServerConnection} to {@link CallHandler}.
 * To add procedure handler use {@link #registerHandler(String, CallHandler)}.
 * It also works as a {@link Message} and {@link ServerConnection} observer
 * providing {@link #addListener(Listener)} and {@link #removeListener(Listener)}
 * methods for registering server listeners. See {@link Listener} interface for
 * more details.
 * 
 * If you are new to <code>jawamp</code> take a look at
 * <a href="https://github.com/rgmih/jawamp/wiki/Tutorial">https://github.com/rgmih/jawamp/wiki/Tutorial</a>
 * for a short tutorial.
 */
public class Server {
	public static final String IDENT = "jawamp/0.1.0";
	
	private static final Logger logger = LoggerFactory.getLogger(Server.class);
	
	protected final Map<String, CallHandler> callHandlers = new HashMap<String, CallHandler>();
	
	/**
	 * Registers new handler for given procedure URI. If any other handler
	 * was already registered for this <code>procURI</code> than it is automatically removed.
	 * @param procURI Procedure URI to register handler for
	 * @param handler Procedure handler to be registered
	 * @return Previously registered handler for this <code>procURI</code> if any exists
	 */
	public CallHandler registerHandler(String procURI, CallHandler handler) {
		CallHandler previousHandler = callHandlers.put(procURI, handler);
		logger.info("call handler '{}' registered for procURI='{}'", handler, procURI);
		return previousHandler;
	}
	
	/**
	 * Removes handler for given <code>procURI</code> from server.
	 * @param procURI URI of procedure handler was registered for
	 * @return Removed handler instance or <code>null</code> if no handler registered
	 */
	public CallHandler removeHandler(String procURI) {
		CallHandler handler = callHandlers.remove(procURI);
		if (handler == null) {
			logger.warn("no handler registered for procURI={}; unable to remove handler", procURI);
			return null;
		} else {
			logger.info("handler {} for procURI={} removed; ok", handler, procURI);
			return handler;
		}
	}
	
	CallResult call(String procURI, List<JsonElement> arguments, ServerConnection connection) throws CallError {
		CallHandler handler = callHandlers.get(procURI);
		if (handler != null) {
			logger.debug("handler found for procURI={}; processing call", procURI);
			return handler.invoke(procURI, arguments, connection);
		} else {
			logger.warn("no handler registered for procURI='{}'; error", procURI);
			throw new CallError(procURI, "procURI not supported");
		}
	}
	
	private final Set<Listener> listeners = new HashSet<Listener>();
	
	/**
	 * Implement {@link Listener} if you need handling low-level server events.
	 */
	public static interface Listener {
		/**
		 * {@link #onMessage(Message)} handler is invoked when new WAMP message received by server.
		 * @param message Message instance
		 */
		public void onMessage(Message message);
		/**
		 * This callback is invoked when {@link ServerConnection} is closed.
		 * @param connection Server connection that is closed
		 */
		public void onConnectionClosed(ServerConnection connection);
	}
	
	/**
	 * Adds listener to the server.
	 * @param listener Listener instance
	 */
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes listener from server.
	 * @param listener Listener instance to be removed
	 */
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	void onMessage(Message message) {
		for (Listener listener : listeners) {
			listener.onMessage(message);
		}
	}
	
	void onConnectionClosed(ServerConnection connection) {
		for (Listener listener : listeners) {
			listener.onConnectionClosed(connection);
		}
	}
}
