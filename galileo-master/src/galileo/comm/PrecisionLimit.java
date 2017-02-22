package galileo.comm;

import java.io.IOException;

import galileo.dataset.feature.FloatIntervalFeatureData;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.ByteSerializable.Deserialize;

public class PrecisionLimit extends FloatIntervalFeatureData{
	
	/* data is spatial precision in integer
	 * data1 is temporal precision in hours*/
	public PrecisionLimit(float data1, float data2) {
		super(data1, data2);
		// TODO Auto-generated constructor stub
	}
	
	@Deserialize
    public PrecisionLimit(SerializationInputStream in)
    throws IOException {
        super(in);
    }
	

}
