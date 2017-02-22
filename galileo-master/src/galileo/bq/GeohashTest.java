package galileo.bq;

import java.util.ArrayList;
import java.util.Arrays;

import galileo.dataset.Coordinates;
import galileo.util.GeoHash;

public class GeohashTest {
	public static void main(String[] args) {
		/*ArrayList<Coordinates> polygon = new ArrayList<>();
		polygon.add(new Coordinates(40.0780714274501f, -109.9951171875f));
		polygon.add(new Coordinates(42.68243539838623f, -104.9853515625f));
		polygon.add(new Coordinates(43.73935207915473f, -109.3359375f));
		System.out.println(Arrays.toString(GeoHash.getIntersectingGeohashes(polygon, 2)));*/
		
		ArrayList<Coordinates> polygon = new ArrayList<>();
		polygon.add(new Coordinates(41.11246878918085f, -118.8720703125f));
		polygon.add(new Coordinates(46.07323062540838f, -106.083984375f));
		polygon.add(new Coordinates(47.30903424774781f, -119.2236328125f));
		System.out.println(Arrays.toString(GeoHash.getIntersectingGeohashes(polygon, 2)));
	}
}
