package com.github.rgmih.jawamp;

import static org.junit.Assert.*;

import java.net.URI;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rgmih.jawamp.Server.CallError;
import com.github.rgmih.jawamp.Server.CallResult;

public class GenericTest {
	
	private static final Logger logger = LoggerFactory.getLogger(GenericTest.class);
	
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
	
	@Before
    public void setUp() throws Exception {
		jetty = new Server(8081);
		jetty.setHandler(new JettyHandler());
		jetty.start();
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
	public void testPrefix() throws Exception {
		WebSocketClient wsClient = createClient();
		
		Client client = new JettyClient();
		WebSocket.Connection connection = wsClient.open(new URI("ws://localhost:8081/"), (JettyClient) client).get();
		client.bindPrefix("p", "http://example.com/");
		client.call("p:add");
		Thread.sleep(100);
		connection.close();
	}
    
    @Test
	public void testCall() throws Exception {
		WebSocketClient wsClient = createClient();
		
		Client client = new JettyClient();
		WebSocket.Connection connection = wsClient.open(new URI("ws://localhost:8081/"), (JettyClient) client).get();
		Future<CallResult> future = client.call("http://example.com/empty");
		try {
			CallResult result = future.get();
			logger.debug("call result received");
		} catch (ExecutionException e) {
			
		}
		Thread.sleep(100);
		connection.close();
	}   
    
	@Test
	public void testWelcome() throws Exception {
		WebSocketClient client = createClient();
		
		welcomeReceived = false;
		WebSocket.Connection connection = client.open(new URI("ws://localhost:8081/"), new WebSocket.OnTextMessage() {
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
}
