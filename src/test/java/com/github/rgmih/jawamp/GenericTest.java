package com.github.rgmih.jawamp;

import static org.junit.Assert.*;

import java.net.URI;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

	@Test
	public void testWelcome() throws Exception {
		WebSocketClient client = createClient();
		WebSocket.Connection connection = client.open(new URI("ws://localhost:8081/"), new WebSocket.OnTextMessage() {
			private Connection connection;
			
			@Override
			public void onOpen(Connection connection) {
				this.connection = connection;
			}
			
			@Override
			public void onClose(int closeCode, String message) {}
			
			@Override
			public void onMessage(String json) {
				Message message = MessageAdapter.parse(json);
				assertTrue(message instanceof WelcomeMessage);
				
				try {
					connection.sendMessage(MessageAdapter.toJSON(new PrefixMessage("abc", "123456")));
				} catch (Exception e) {
					assertTrue("I/O exception occurred", false);
				}
			}
		}).get();
		Thread.sleep(1000);
		connection.close();
	}
}
