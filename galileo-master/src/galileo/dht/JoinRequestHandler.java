package galileo.dht;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import galileo.comm.GalileoEventMap;
import galileo.comm.JoinInitiatorResponse;
import galileo.comm.JoinResponse;
import galileo.comm.MetaResponse;
import galileo.comm.QueryResponse;
import galileo.dataset.feature.Feature;
import galileo.event.BasicEventWrapper;
import galileo.event.Event;
import galileo.event.EventContext;
import galileo.graph.Path;
import galileo.net.ClientMessageRouter;
import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;
import galileo.net.RequestListener;
import galileo.serialization.SerializationException;

/**
 * This class will collect the responses from all the nodes of galileo and then
 * transfer the result to the listener. Used by the {@link StorageNode} class.
 * 
 * @author sapmitra
 */
public class JoinRequestHandler implements MessageListener {

	private static final Logger logger = Logger.getLogger("galileo");
	private static final int DELAY = 600000; // 600 seconds = 10 minutes
	private GalileoEventMap eventMap;
	private BasicEventWrapper eventWrapper;
	private ClientMessageRouter router;
	private AtomicInteger expectedResponses;
	private Collection<NetworkDestination> nodes;
	private EventContext clientContext;
	private List<GalileoMessage> responses;
	private RequestListener requestListener;
	private AtomicLong timeout;
	private Event response;
	private Thread timerThread;
	private long elapsedTime;

	public JoinRequestHandler(Collection<NetworkDestination> nodes, EventContext clientContext,
			RequestListener listener) throws IOException {
		this.nodes = nodes;
		this.clientContext = clientContext;
		this.requestListener = listener;

		this.router = new ClientMessageRouter(true);
		this.router.addListener(this);
		this.responses = new ArrayList<GalileoMessage>();
		this.eventMap = new GalileoEventMap();
		this.eventWrapper = new BasicEventWrapper(this.eventMap);
		this.expectedResponses = new AtomicInteger(this.nodes.size());
		this.timeout = new AtomicLong();
		this.timerThread = new Thread("Client Request Handler - Timer Thread") {
			public void run() {
				try {
					elapsedTime = System.currentTimeMillis();
					while (System.currentTimeMillis() < timeout.get()) {
						Thread.sleep(500);
						if (isInterrupted())
							throw new InterruptedException("Deliberate interruption");
					}
				} catch (InterruptedException e) {
					logger.log(Level.INFO, "Timeout thread interrupted.");
				} finally {
					logger.log(Level.INFO, "Timeout: Closing the request and sending back the response.");
					elapsedTime = System.currentTimeMillis() - elapsedTime;
					JoinRequestHandler.this.closeRequest();
				}
			};
		};
	}

	public void closeRequest() {
		logger.log(Level.INFO,"=========================CALLED======================");
		silentClose(); // closing the router to make sure that no new responses
						// are added.
		
		String clubbedText="";
		for (GalileoMessage gresponse : this.responses) {
			Event event;
			try {
				event = this.eventWrapper.unwrap(gresponse);
				if (event instanceof JoinResponse && this.response instanceof JoinResponse) {
					/* ULTIMATE COMPILED RESPONSE */
					JoinResponse actualResponse = (JoinResponse) this.response;
					/* WHAT WE GET IN EACH RESPONSE */
					JoinResponse eventResponse = (JoinResponse) event;
					String temp="";
					temp = actualResponse.getText()+"\n"+eventResponse.getText();
					actualResponse.setText(temp);
					clubbedText = actualResponse.getText();
				} 
				
			} catch (IOException | SerializationException e) {
				logger.log(Level.INFO, "An exception occurred while processing the response message. Details follow:"
						+ e.getMessage());
			} catch (Exception e) {
				logger.log(Level.SEVERE, "An unknown exception occurred while processing the response message. Details follow:"
						+ e.getMessage());
			}
		}
		
		logger.log(Level.INFO,"CLUBBED TEXT:::::\n"+clubbedText);
		/* WHEN WE ARE SATISFIED WITH ALL RESPONSE */
		this.requestListener.onRequestCompleted(this.response, clientContext, this);
	}

	@Override
	public void onMessage(GalileoMessage message) {
		if (null != message)
			this.responses.add(message);
		int awaitedResponses = this.expectedResponses.decrementAndGet();
		logger.log(Level.INFO, "Awaiting " + awaitedResponses + " more message(s)");
		if (awaitedResponses > 0) // extend timer when awaiting more responses
			this.timeout.set(System.currentTimeMillis() + DELAY);
		else
			this.timeout.set(System.currentTimeMillis()); // send response
															// immediately
	}

	/**
	 * Handles the client request on behalf of the node that received the
	 * request
	 * 
	 * @param request
	 *            - This must be a server side event: Generic Event or
	 *            QueryEvent
	 * @param response
	 */
	public void handleRequest(Event request, Event response) {
		try {
			this.response = response;
			GalileoMessage mrequest = this.eventWrapper.wrap(request);
			for (NetworkDestination node : nodes) {
				this.router.sendMessage(node, mrequest);
				logger.info("Request sent to " + node.toString());
			}

			// Timeout thread which sets expectedResponses to zero after the
			// specified time elapses.
			this.timeout.set(System.currentTimeMillis() + DELAY);
			this.timerThread.start();
		} catch (IOException e) {
			logger.log(Level.INFO,
					"Failed to send request to other nodes in the network. Details follow: " + e.getMessage());
		}
	}

	public void silentClose() {
		try {
			this.router.forceShutdown();
		} catch (Exception e) {
			logger.log(Level.INFO, "Failed to shutdown the completed client request handler: ", e);
		}
	}

	@Override
	public void onConnect(NetworkDestination endpoint) {

	}

	@Override
	public void onDisconnect(NetworkDestination endpoint) {

	}
}
