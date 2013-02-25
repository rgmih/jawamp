package com.github.rgmih.jawamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

import com.google.gson.JsonElement;

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
		case CALLRESULT:
			CallResultMessage callResultMessage = (CallResultMessage) message;
			Call call = calls.get(callResultMessage.getCallID());
			if (call != null) {
				call.setResult(new CallResult(callResultMessage.getPayload()));
			}
			break;
		case CALLERROR:
//			CallErrorMessage callError = (CallErrorMessage) message;
//			Call call = calls.get(callResult.getCallID());
//			if (call != null) {
//				// TODO parse payload
//				call.setResult(new CallResult(null));
//			}
			break;
		default:
			break;
		}
	}

	public void bindPrefix(String prefix, String URI) {
		sendMessage(new PrefixMessage(prefix, URI));
	}

	// private static class CallTask extends FutureTask<CallResult> implements
	// Callable<CallResult> {
	//
	// public CallTask() {
	// super()
	// }
	// }

	public Future<CallResult> call(String procURI, JsonElement... arguments) {
		UUID callID = UUID.randomUUID();
		Call callable = new Call(new CallMessage(callID.toString(), procURI, Arrays.asList(arguments)));
		calls.put(callID.toString(), callable);
		FutureTask<CallResult> task = new FutureTask<CallResult>(callable);
		threadpool.execute(task);
		return task;
	}
}
