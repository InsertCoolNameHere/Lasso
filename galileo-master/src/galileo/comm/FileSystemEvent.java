package galileo.comm;

import java.io.IOException;

import galileo.event.Event;
import galileo.serialization.SerializationException;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

/**
 * Internal use only. To create or delete file systems in galileo
 * @author jkachika
 *
 */
public class FileSystemEvent implements Event{
	
	private String name;
	private FileSystemAction action;
	private PrecisionLimit prec;
	
	public FileSystemEvent(String name, FileSystemAction action) {
		if(name == null || name.trim().length() == 0 || !name.matches("[a-z0-9-]{5,50}"))
			throw new IllegalArgumentException("name is required and must be lowercase having length at least 5 and at most 50 characters. alphabets, numbers and hyphens are allowed.");
		if(action == null)
			throw new IllegalArgumentException("action cannot be null. must be one of the actions specified by galileo.comm.FileSystemAction");
		this.name = name;
		this.action = action;
	}
	
	public String getName(){
		return this.name;
	}
	
	public FileSystemAction getAction(){
		return this.action;
	}
	
	@Deserialize
    public FileSystemEvent(SerializationInputStream in)
    throws IOException, SerializationException {
         this.name = in.readString();
         this.action = FileSystemAction.fromAction(in.readString());
         this.prec = new PrecisionLimit(in);
        
    }

	@Override
	public void serialize(SerializationOutputStream out) throws IOException {
		out.writeString(this.name);
		out.writeString(this.action.getAction());
		out.writeSerializable(prec);
	}

	public PrecisionLimit getPrec() {
		return prec;
	}

	public void setPrec(PrecisionLimit prec) {
		this.prec = prec;
	}

}