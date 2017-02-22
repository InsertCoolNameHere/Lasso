package galileo.dataset;

import java.io.IOException;

import galileo.serialization.ByteSerializable;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;
import galileo.serialization.ByteSerializable.Deserialize;

public class SearchIndex implements ByteSerializable{
	
	private String latitudePosn;
	private String longitudePosn;
	private String temporalPosn;
	
	public SearchIndex(String latHint, String longHint, String tempHint) {
		this.latitudePosn = latHint;
		this.longitudePosn = longHint;
		this.temporalPosn = tempHint;
	}
	
	public String getLatitudeHint(){
		return this.latitudePosn;
	}
	
	public String getLongitudeHint() {
		return this.longitudePosn;
	}
	
	@Deserialize
    public SearchIndex(SerializationInputStream in)
    throws IOException {
        this.latitudePosn = in.readString();
        this.longitudePosn = in.readString();
        this.temporalPosn = in.readString();
    }

	@Override
	public void serialize(SerializationOutputStream out) throws IOException {
		out.writeString(latitudePosn);
		out.writeString(longitudePosn);
		out.writeString(temporalPosn);
	}

}
