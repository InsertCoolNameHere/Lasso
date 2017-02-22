package galileo.comm;

import java.io.IOException;

import galileo.event.Event;
import galileo.graph.GraphException;
import galileo.serialization.SerializationException;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class JoinResponse implements Event{
	private String text;

	public String getText() {
		return text;
	}
	
	public JoinResponse(String text) {
		this.text = text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@Deserialize
	public JoinResponse(SerializationInputStream in) throws IOException, SerializationException, GraphException {
		text = in.readString();
		
	}

	@Override
	public void serialize(SerializationOutputStream out) throws IOException {
		out.writeString(text);

	}

}
