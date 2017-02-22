package galileo.comm;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import galileo.dataset.Coordinates;
import galileo.event.Event;
import galileo.serialization.SerializationException;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class JoinRequest implements Event{
	
	private List<Coordinates> polygon;
	private String specialText;
	private String stDate;
	private String endDate;
	
	public String getSpecialText() {
		return specialText;
	}
	public void setSpecialText(String specialText) {
		this.specialText = specialText;
	}
	
	public List<Coordinates> getPolygon() {
		return polygon;
	}
	public void setPolygon(List<Coordinates> polygon) {
		this.polygon = polygon;
	}
	public String getStDate() {
		return stDate;
	}
	public void setStDate(String stDate) {
		this.stDate = stDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public JoinRequest(List<Coordinates> polygon, String specialText, String stDate, String endDate) {
		this.polygon = polygon;
		this.specialText = specialText;
		this.stDate = stDate;
		this.endDate = endDate;
	}
	
	@Deserialize
	public JoinRequest(SerializationInputStream in) throws IOException, SerializationException {
		this.specialText = in.readString();
		this.stDate = in.readString();
		this.endDate = in.readString();
		List<Coordinates> poly = new ArrayList<Coordinates>();
		in.readSerializableCollection(Coordinates.class, poly);
		this.polygon = poly;
	}

	@Override
	public void serialize(SerializationOutputStream out) throws IOException {
		out.writeString(this.specialText);
		out.writeString(this.stDate);
		out.writeString(this.endDate);
		out.writeSerializableCollection(polygon);
	}

}
