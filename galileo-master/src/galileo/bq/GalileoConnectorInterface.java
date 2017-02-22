package galileo.bq;
import java.io.IOException;

import galileo.client.EventPublisher;
import galileo.comm.FileSystemAction;
import galileo.comm.FileSystemRequest;
import galileo.comm.PrecisionLimit;
import galileo.comm.StorageRequest;
import galileo.dataset.Block;
import galileo.net.ClientMessageRouter;
import galileo.net.NetworkDestination;

abstract class GalileoConnectorInterface {
	private ClientMessageRouter messageRouter;
	private EventPublisher publisher;
	private NetworkDestination server;
	
	public GalileoConnectorInterface(String serverHostName, int serverPort) throws IOException {
		messageRouter = new ClientMessageRouter();
		publisher = new EventPublisher(messageRouter);
		server = new NetworkDestination(serverHostName, serverPort);
	}
	
	public void store(Block fb) throws Exception {
		StorageRequest store = new StorageRequest(fb);
		publisher.publish(server, store);
	}
	
	public void createFS(String name) throws IOException {
		PrecisionLimit pr = new PrecisionLimit(4.0f, 1.0f);
		FileSystemRequest fsr = new FileSystemRequest(name, FileSystemAction.CREATE, pr);
		publisher.publish(server, fsr);
	}
	public void createFS(String name, float a, float b) throws IOException {
		PrecisionLimit pr = new PrecisionLimit(a, b);
		FileSystemRequest fsr = new FileSystemRequest(name, FileSystemAction.CREATE, pr);
		publisher.publish(server, fsr);
	}
	
	public void disconnect() {
		messageRouter.shutdown();
	}

}
