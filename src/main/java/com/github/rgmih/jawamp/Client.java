package com.github.rgmih.jawamp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rgmih.jawamp.message.CallErrorMessage;
import com.github.rgmih.jawamp.message.CallMessage;
import com.github.rgmih.jawamp.message.CallResultMessage;
import com.github.rgmih.jawamp.message.EventMessage;
import com.github.rgmih.jawamp.message.Message;
import com.github.rgmih.jawamp.message.PrefixMessage;
import com.github.rgmih.jawamp.message.PublishMessage;
import com.github.rgmih.jawamp.message.SubscribeMessage;
import com.github.rgmih.jawamp.message.UnsubscribeMessage;
import com.github.rgmih.jawamp.message.WelcomeMessage;
import com.google.gson.JsonElement;

/**
 * Client connection base class. Provides high-level interface for
 * making asynchronous RPC calls, see {@link #call(String, JsonElement...)}.
 * It also provides PubSub API for subscribing ({@link #subscribe(String, Subscriber)},
 * unsubscribing ({@link #unsubscribe(String)} and listening for events
 * (see {@link Subscriber} interface).
 * 
 *  If you are new to <code>jawamp</code> take a look at
 * <a href="https://github.com/rgmih/jawamp/wiki/Tutorial">https://github.com/rgmih/jawamp/wiki/Tutorial</a>
 * for a short tutorial on how to use {@link Client}.
 */
public abstract class Client extends Connection {

	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	
	private ExecutorService threadpool = Executors.newCachedThreadPool();
	
	private class Call implements Callable<CallResult> {
		final Lock lock = new ReentrantLock();
		final Condition done = lock.newCondition();
		
		private CallResult payload = null;
		private CallError  error   = null;
		
		private CallMessage message;
		
		public Call(CallMessage message) {
			this.message = message;
		}
		
		@Override
		public CallResult call() throws Exception {
			lock.lock();
			sendMessage(message);
			
			try {
				done.await();
			} finally {
				lock.unlock();
			}
			if (payload != null) {
				return payload;
			} else if (error != null) {
				throw error;
			} else {
				// TODO can not be
				throw new RuntimeException("can not be");
			}
		}
		
		public void setResult(CallResult payload) {
			this.payload = payload;
			lock.lock();
			try {
				done.signal();
			} finally {
				lock.unlock();
			}
		}

		public void setError(CallError error) {
			this.error = error;
			lock.lock();
			try {
				done.signal();
			} finally {
				lock.unlock();
			}
		}
	}

	private Map<String, Call> calls = new HashMap<String, Call>();

	@Override
	protected void onMessage(Message message) {
		logger.debug("message received; type={}", message.getType());
		
		switch (message.getType()) {
		case WELCOME:
			WelcomeMessage welcome = (WelcomeMessage) message;
			sessionID = welcome.getSessionID();
			break;
		case CALLRESULT:
		{
			CallResultMessage callResultMessage = (CallResultMessage) message;
			Call call = calls.get(callResultMessage.getCallID());
			if (call != null) {
				call.setResult(new CallResult(callResultMessage.getPayload()));
			}
		}
			break;
		case CALLERROR:
		{
			CallErrorMessage callErrorMessage = (CallErrorMessage) message;
			Call call = calls.get(callErrorMessage.getCallID());
			if (call != null) {
				call.setError(callErrorMessage.toError());
			}
		}
			break;
		case EVENT:
			EventMessage eventMessage = (EventMessage) message;
			notifySubscribers(eventMessage.getTopicURI(), eventMessage.getEvent());
			break;
		default:
			break;
		}
	}

	/**
	 * Sends <code>PREFIX</code> command to server to register
	 * <code>prefix</code> for the given <code>URI</code>. 
	 * @param prefix Prefix to be bound
	 * @param URI URI string to register prefix for
	 */
	public void bindPrefix(String prefix, String URI) {
		sendMessage(new PrefixMessage(prefix, URI));
	}

	/**
	 * Performs RPC call. Returns {@link Future} instance that may used to
	 * synchronize on call success or failure. Use {@link Future#get()} or
	 * {@link Future#get(long, java.util.concurrent.TimeUnit)} to get call
	 * result or catch {@link CallError} if call fails.
	 * 
	 * @see Future
	 * 
	 * @param procURI URI of procedure to be called
	 * @param arguments Arguments to be passed to procedure
	 * @return {@link Future} instance representing future result of operation
	 */
	public Future<CallResult> call(String procURI, JsonElement... arguments) {
		UUID callID = UUID.randomUUID();
		Call callable = new Call(new CallMessage(callID.toString(), procURI, Arrays.asList(arguments)));
		calls.put(callID.toString(), callable);
		FutureTask<CallResult> task = new FutureTask<CallResult>(callable);
		threadpool.execute(task);
		return task;
	}

	/**
	 * Implement this interface to be notified on new events when being subscribed.
	 * To subscribe use {@link Client#subscribe(String, Subscriber)}.
	 */
	public static interface Subscriber {
		/**
		 * This method is invoked when event this subscriber was
		 * subscribed to is received by {@link Client}.
		 * @param topicURI URI of event. May be useful if single subscriber is subscribed to multiple event types
		 * @param event Event instance in form of JSON element
		 */
		void onEvent(String topicURI, JsonElement event);
	}
	
	private final Map<String, Subscriber> subscribers = new HashMap<String, Subscriber>();
	
	/**
	 * Subscribes for a given event type.
	 * @param topicURI Event topic URI to subscribe to
	 * @param subsciber Subscriber instance to be registered
	 */
	public void subscribe(String topicURI, Subscriber subsciber) {
		subscribers.put(topicURI, subsciber);
		sendMessage(new SubscribeMessage(topicURI));
	}
	
	
	/**
	 * Publishes event without any additional parameters.
	 * @param topicURI Event topic URI
	 * @param event Event payload to be published
	 */
	public void publish(String topicURI, JsonElement event) {
		sendMessage(new PublishMessage(topicURI, event));
	}
	
	/**
	 * Publishes event providing <code>excludeMe</code> parameter.
	 * @param topicURI Event topic URI
	 * @param event Event payload to be published
	 * @param excludeMe See <a href="http://wamp.ws/spec#publish_message">WAMP specification</a> for more details
	 */
	public void publish(String topicURI, JsonElement event, boolean excludeMe) {
		sendMessage(new PublishMessage(topicURI, event, excludeMe));
	}
	
	/**
	 * Publishes event providing list of excluded subscribers.
	 * @param topicURI Event topic URI
	 * @param event Event payload to be published
	 * @param exclude See <a href="http://wamp.ws/spec#publish_message">WAMP specification</a> for more details
	 */
	public void publish(String topicURI, JsonElement event, List<String> exclude) {
		sendMessage(new PublishMessage(topicURI, event, exclude));
	}
	
	/**
	 * Publishes event providing lists of excluded and eligible subscribers.
	 * @param topicURI Event topic URI
	 * @param event Event payload to be published
	 * @param exclude See <a href="http://wamp.ws/spec#publish_message">WAMP specification</a> for more details
	 * @param eligible See <a href="http://wamp.ws/spec#publish_message">WAMP specification</a> for more details
	 */
	public void publish(String topicURI, JsonElement event, List<String> exclude, List<String> eligible) {
		sendMessage(new PublishMessage(topicURI, event, exclude, eligible));
	}
	
	/**
	 * Stops listening for given topic URI.
	 * @param topicURI Event topic URI to stop listening to
	 */
	public void unsubscribe(String topicURI) {
		sendMessage(new UnsubscribeMessage(topicURI));
		subscribers.remove(topicURI);
	}
	
	private void notifySubscribers(String topicURI, JsonElement event) {
		Subscriber subscriber = subscribers.get(topicURI);
		if (subscriber != null) {
			subscriber.onEvent(topicURI, event);
		} else {
			logger.warn("no subscriber found for topicURI={}", topicURI);
		}
	}

	private String sessionID = null;
	
	/**
	 * Returns the session identifier provided by WAMP server in
	 * <code>WELCOME</code> message when client was first connected.
	 * @return String session identifier
	 */
	public String getSessionID() {
		return sessionID;
	}
}
