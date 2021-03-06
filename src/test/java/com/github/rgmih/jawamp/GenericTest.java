package com.github.rgmih.jawamp;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.rgmih.jawamp.message.Message;
import com.github.rgmih.jawamp.message.WelcomeMessage;
import com.github.rgmih.jawamp.util.MessageAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


public class GenericTest {
	
	// private static final Logger logger = LoggerFactory.getLogger(GenericTest.class);
	
	private Server jetty;
	private static WebSocketClientFactory factory;
	
	@BeforeClass
	public static void onTimeSetUp() throws Exception {
		factory = new WebSocketClientFactory();
		factory.start();
	}
	
	@AfterClass
	public static void onTimeTearDown() throws Exception {
		factory.stop();
	}
	
	private int port;
	
	@Before
    public void setUp() throws Exception {
		jetty = new Server(0);
		jetty.setHandler(new JettyHandler());
		jetty.start();
		port = jetty.getConnectors()[0].getLocalPort();
    }

    @After
    public void tearDown() throws Exception {
    	jetty.stop();
    	jetty = null;
    }

    private static WebSocketClient createClient() throws Exception {
    	
		WebSocketClient client = factory.newWebSocketClient();
		
		client.setProtocol("wamp");
		return client;
    }

    boolean welcomeReceived;
    
    @Test
	public void testWelcome() throws Exception {
		WebSocketClient client = createClient();
		
		welcomeReceived = false;
		WebSocket.Connection connection = client.open(new URI("ws://localhost:" + port), new WebSocket.OnTextMessage() {
			@Override
			public void onOpen(Connection connection) {}
			
			@Override
			public void onClose(int closeCode, String message) {}
			
			@Override
			public void onMessage(String json) {
				Message message = MessageAdapter.parse(json);
				assertTrue(message instanceof WelcomeMessage);
				welcomeReceived = true;
			}
		}).get();
		Thread.sleep(1000);
		assertTrue("no WELCOME message received", welcomeReceived);
		connection.close();
	}
    
    @Test
	public void testCall() throws Exception {
		WebSocketClient wsClient = createClient();
		
		Client client = new JettyClient();
		WebSocket.Connection connection = wsClient.open(new URI("ws://localhost:" + port), (JettyClient) client).get();
		Future<CallResult> future = client.call("http://example.com/add", new JsonPrimitive(2), new JsonPrimitive(3));
		try {
			CallResult result = future.get();
			int z = result.getPayload().getAsInt();
			assertTrue("2 + 3 != 5", z == 5);
		} catch (ExecutionException e) {
			
		}
		Thread.sleep(100);
		connection.close();
	}   
    
    @Test
	public void testCallError() throws Exception {
		WebSocketClient wsClient = createClient();
		
		Client client = new JettyClient();
		WebSocket.Connection connection = wsClient.open(new URI("ws://localhost:" + port), (JettyClient) client).get();
		Future<CallResult> future = client.call("http://example.com/error");
		try {
			future.get();
			fail("call error expected");
		} catch (ExecutionException e) {
			Throwable t = e.getCause();
			assertTrue("error type != CallError", t instanceof CallError);
			CallError callError = (CallError) t;
			assertTrue("call error URI not passed", "http://example.com/error".equals(callError.getErrorURI()));
			assertTrue("call error description not passed", "error description".equals(callError.getErrorDesc()));
		}
		Thread.sleep(100);
		connection.close();
	}
    
    @Test
	public void testPrefix() throws Exception {
		WebSocketClient wsClient = createClient();
		
		Client client = new JettyClient();
		WebSocket.Connection connection = wsClient.open(new URI("ws://localhost:" + port), (JettyClient) client).get();
		client.bindPrefix("p", "http://example.com/");
		Future<CallResult> future = client.call("p:add", new JsonPrimitive(4), new JsonPrimitive(-2));
		try {
			future.get();
		} catch (ExecutionException e) {
			fail("call to procURI with prefix failed");
		}
		
		future = client.call("p:error");
		try {
			future.get();
		} catch (ExecutionException e) {
			CallError error = (CallError) e.getCause();
			assertEquals("error URI not using prefix;", error.getErrorURI(), "p:error");
		}
		connection.close();
	}
    
    @Test
    public void testErrorDetails() throws Exception {
    	WebSocketClient wsClient = createClient();
		
		Client client = new JettyClient();
		WebSocket.Connection connection = wsClient.open(new URI("ws://localhost:" + port), (JettyClient) client).get();
		Future<CallResult> future = client.call("http://example.com/error");
		try {
			future.get();
		} catch (ExecutionException e) {
			CallError error = (CallError) e.getCause();
			assertNotNull("error details not set", error.getErrorDetails());
			assertEquals("error details invalid;", "details", error.getErrorDetails().getAsString());
		}
		connection.close();
    }
    
    private boolean eventReceived = false;
    
    @Test
    public void testPubSub() throws Exception {
    	WebSocketClient wsClient = createClient();
		Client client = new JettyClient();
		WebSocket.Connection connection = wsClient.open(new URI("ws://localhost:" + port), (JettyClient) client).get();
		
		client.subscribe("http://example.com/topic", new Client.Subscriber() {
			@Override
			public void onEvent(String topicURI, JsonElement event) {
				eventReceived = true;
				assertEquals("event topic is invalid;", topicURI, "http://example.com/topic");
				assertEquals("event payload is invalid;", "event", event.getAsString());
			}
		});
		eventReceived = false;
		client.publish("http://example.com/topic", new JsonPrimitive("event"));
		Thread.sleep(1000);
		assertTrue("event not published", eventReceived);
		
		connection.close();
    }
    
    @Test
    public void testPublishExcludeMe() throws Exception {
    	WebSocketClient wsClient = createClient();
		Client client = new JettyClient();
		WebSocket.Connection connection = wsClient.open(new URI("ws://localhost:" + port), (JettyClient) client).get();
		
		client.subscribe("http://example.com/topic", new Client.Subscriber() {
			@Override
			public void onEvent(String topicURI, JsonElement event) {
				eventReceived = true;
			}
		});
		eventReceived = false;
		client.publish("http://example.com/topic", new JsonPrimitive("event"), true);
		Thread.sleep(1000);
		assertFalse("event was published, no exclusion", eventReceived);
		
		connection.close();
    }
    
    @Test
    public void testPublishExclude() throws Exception {
    	WebSocketClient wsClient = createClient();
		Client client = new JettyClient();
		WebSocket.Connection connection = wsClient.open(new URI("ws://localhost:" + port), (JettyClient) client).get();
		
		client.subscribe("http://example.com/topic", new Client.Subscriber() {
			@Override
			public void onEvent(String topicURI, JsonElement event) {
				eventReceived = true;
			}
		});
		eventReceived = false;
		Thread.sleep(1000); // wait for WELCOME message
		List<String> exclude = new ArrayList<String>();
		exclude.add(client.getSessionID());
		client.publish("http://example.com/topic", new JsonPrimitive("event"), exclude);
		Thread.sleep(1000);
		assertFalse("event was published, no exclusion", eventReceived);
		
		connection.close();
    }
    
    private boolean receivedA, receivedB;
    
    @Test
    public void testPublishEligible() throws Exception {
    	WebSocketClient wsClient = createClient();
		Client clientA = new JettyClient();
		WebSocket.Connection connectionA = wsClient.open(new URI("ws://localhost:" + port), (JettyClient) clientA).get();
		Client clientB = new JettyClient();
		WebSocket.Connection connectionB = wsClient.open(new URI("ws://localhost:" + port), (JettyClient) clientB).get();
		receivedA = false;
		receivedB = false;
		clientA.subscribe("http://example.com/topic", new Client.Subscriber() {
			@Override
			public void onEvent(String topicURI, JsonElement event) {
				receivedA = true;
			}
		});
		clientB.subscribe("http://example.com/topic", new Client.Subscriber() {
			@Override
			public void onEvent(String topicURI, JsonElement event) {
				receivedB = true;
			}
		});
		
		Thread.sleep(1000); // wait for WELCOME message
		List<String> eligible = new ArrayList<String>();
		eligible.add(clientB.getSessionID());
		
		clientA.publish("http://example.com/topic", new JsonPrimitive("event"), new ArrayList<String>(), eligible);
		Thread.sleep(1000);
		
		assertFalse("client A must not receive event", receivedA);
		assertTrue("client B must have received event", receivedB);
		
		connectionA.close();
		connectionB.close();
    }
    
    private int eventsReceived;
    @Test
    public void testUnsubscribe() throws Exception {
    	WebSocketClient wsClient = createClient();
		Client client = new JettyClient();
		WebSocket.Connection connection = wsClient.open(new URI("ws://localhost:" + port), (JettyClient) client).get();
		
		client.subscribe("http://example.com/topic", new Client.Subscriber() {
			@Override
			public void onEvent(String topicURI, JsonElement event) {
				eventsReceived++;
			}
		});
		eventsReceived = 0;
		client.publish("http://example.com/topic", new JsonPrimitive("event"));
		Thread.sleep(1000);
		
		client.unsubscribe("http://example.com/topic");
		client.publish("http://example.com/topic", new JsonPrimitive("event"));
		Thread.sleep(1000);
		
		assertEquals("too many events;", 1, eventsReceived);
		
		connection.close();
    }
}
