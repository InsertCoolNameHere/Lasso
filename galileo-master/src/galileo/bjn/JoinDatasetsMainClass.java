package galileo.bjn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import galileo.dataset.Coordinates;


public class JoinDatasetsMainClass {

	public static void main(String[] args1) {
		String args[] = new String[3];
		args[0] = "annapolis";
		args[1] = "5555";
		
		
		if (args.length != 3) {
			System.out.println(
					"Usage: ConvertCSVFileToGalileo [galileo-hostname] [galileo-port-number] [path-to-csv-file]");
			System.exit(0);
		} else {
			try {
				GalileoJoinConnector gc = new GalileoJoinConnector(args[0], Integer.parseInt(args[1]));
				System.out.println("Connector Created: "+args[0] + "," + Integer.parseInt(args[1]));
				
				Coordinates c1 = new Coordinates(1.0f, 2.0f);
				Coordinates c2 = new Coordinates(2.0f, 3.0f);
				Coordinates c3 = new Coordinates(3.0f, 4.0f);
				Coordinates c4 = new Coordinates(4.0f, 5.0f);
				
				List<Coordinates> polygon = new ArrayList<Coordinates>();
				polygon.add(c1);
				polygon.add(c2);
				polygon.add(c3);
				polygon.add(c4);
				
				
				gc.join(polygon, "AmarSpecial", "1234", "5678");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Data successfully inserted into galileo");
		System.exit(0);
	}

}
