package galileo.bq;
/* 
 * Copyright (c) 2015, Colorado State University. Written by Duck Keun Yang 2015-08-02
 * 
 * All rights reserved.
 * 
 * CSU EDF Project
 * 
 * This program read a csv-formatted file and send each line to the galileo server
 */

import galileo.dataset.Block;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

public class ConvertCSVFileToGalileo {

	// [START processFile]
	/**
	 * read each line from the csv file and send it to galileo server
	 * 
	 * @param pathtothefile
	 *            path to the csv file
	 * @param galileoconnector
	 *            GalileoConnector instance
	 * @throws Exception
	 */
	private static void processFile(String filepath, GalileoConnector gc) throws Exception {
		
		//Create a filesystem first.
		gc.createFS("airview", 4.0f, 1.0f);
		
		FileInputStream inputStream = null;
		Scanner sc = null;
		
		/* START---- THIS PART JUST VERIFIES IF THE DATA FORMAT IS CORRECT */
		try {
			inputStream = new FileInputStream(filepath);
			sc = new Scanner(inputStream);
			StringBuffer data = new StringBuffer();
			System.out.println("Start Reading CSV File");
			String previousDay = null;
			int rowCount = 0;
			Calendar c = Calendar.getInstance();
			c.setTimeZone(TimeZone.getTimeZone("GMT"));
			String lastLine = "";
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String tmpvalues[] = line.split(",");
				if (line.contains("platform_id,date,")) {
					continue;
				}
				if (Float.parseFloat(tmpvalues[24]) == 0.0f && Float.parseFloat(tmpvalues[25]) == 0.0f) {
					continue;
				}
				if (line.contains("NaN") || line.contains("null")) {
					line.replace("NaN", "0.0");
					line.replace("null", "0.0");
				}
				long epoch = GalileoConnector.reformatDatetime(tmpvalues[7]);
				c.setTimeInMillis(epoch);
				String currentDay = String.format("%d-%d-%d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
						c.get(Calendar.DAY_OF_MONTH));
				
				// DOING FOR ONE DAY AT A TIME
				if (previousDay != null && !currentDay.equals(previousDay)) {
					String allLines = data.toString();
					System.out.println("Creating a block for " + previousDay + " GMT having " + rowCount + " rows");
					System.out.println(lastLine);
					Block tmp = GalileoConnector.createBlock(lastLine, allLines.substring(0, allLines.length() - 1));
					if (tmp != null) {
						gc.store(tmp);
					}
					data = new StringBuffer();
					rowCount = 0;
				}
				previousDay = currentDay;
				data.append(line + "\n");
				lastLine = line;
				rowCount++;
			}
			/* END---- THIS PART JUST VARIFIES IF THE DATA FORMAT IS CORRECT */
			
			String allLines = data.toString();
			System.out.println("Creating a block for " + previousDay + " GMT having " + rowCount + " rows");
			System.out.println(lastLine);
			Block tmp = GalileoConnector.createBlock(lastLine, allLines.substring(0, allLines.length() - 1));
			if (tmp != null) {
				gc.store(tmp);
			}
			// note that Scanner suppresses exceptions
			if (sc.ioException() != null) {
				throw sc.ioException();
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (sc != null) {
				sc.close();
			}
			gc.disconnect();
		}
	}
	// [END processFile]

	// [START Main]
	/**
	 * Based on command line argument, call processFile method to store the data
	 * at galileo server
	 * 
	 * @param args
	 */
	public static void main(String[] args1) {
		String args[] = new String[3];
		args[0] = "annapolis";
		args[1] = "5555";
		args[2] = "/s/chopin/b/grad/sapmitra/GalileoT/galileo-master/eg.csv";
		
		
		if (args.length != 3) {
			System.out.println(
					"Usage: ConvertCSVFileToGalileo [galileo-hostname] [galileo-port-number] [path-to-csv-file]");
			System.exit(0);
		} else {
			try {
				GalileoConnector gc = new GalileoConnector(args[0], Integer.parseInt(args[1]));
				System.out.println(args[0] + "," + Integer.parseInt(args[1]));
				File file = new File(args[2]);
				if (file.isFile()) {
					System.out.println("processing - " + args[2]);
					processFile(args[2], gc);
				} else {
					if (file.isDirectory()) {
						File[] files = file.listFiles();
						for (File f : files) {
							if (f.isFile())
								System.out.println("processing - " + f);
							processFile(f.getAbsolutePath(), gc);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Data successfully inserted into galileo");
		System.exit(0);
	}
	// [END Main]
}
