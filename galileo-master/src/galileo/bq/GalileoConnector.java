package galileo.bq;
/*
 * Copyright (c) 2013, Colorado State University. Modified by Duck Keun Yang for CSU EDF Project 2015-08-02
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *    
 * This software is provided by the copyright holders and contributors "as is" and
 * any express or implied warranties, including, but not limited to, the implied
 * warranties of merchantability and fitness for a particular purpose are
 * disclaimed. In no event shall the copyright holder or contributors be liable for
 * any direct, indirect, incidental, special, exemplary, or consequential damages
 * (including, but not limited to, procurement of substitute goods or services;
 * loss of use, data, or profits; or business interruption) however caused and on
 * any theory of liability, whether in contract, strict liability, or tort
 * (including negligence or otherwise) arising in any way out of the use of this
 * software, even if advised of the possibility of such damage.
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import galileo.dataset.Block;
import galileo.dataset.Metadata;
import galileo.dataset.SearchIndex;
import galileo.dataset.SpatialHint;
import galileo.dataset.SpatialProperties;
import galileo.dataset.TemporalProperties;
import galileo.dataset.feature.Feature;
import galileo.dataset.feature.FeatureSet;
import galileo.util.GeoHash;

public class GalileoConnector extends GalileoConnectorInterface {
	

	// [START Constructor]
	/**
	 * Constructor of GalileoConnector. Use Superclass Constructor.
	 * 
	 * @param serverHostName	Hostname of galileo server
	 * @param serverPort		Portnumber of galileo server
	 * @throws IOException
	 */
	public GalileoConnector(String serverHostName, int serverPort) throws IOException {
		super(serverHostName, serverPort);
	}
	// [END Constructor]

	// [START store]
	/**
	 * stores a block to galileo server. Use Superclass Method
	 * 
	 * @param block		a galileo block to be sent and stored at galileo server
	 * @throws Exception
	 */
	public void store(Block fb) throws Exception {
		super.store(fb);
	}
	// [END store]

	// [START disconnect]
	/**
	 * disconnects from galileo server
	 */
	public void disconnect() {
		super.disconnect();
	}
	// [END disconnect]

	// [START createBlock]
	/**
	 * returns a galileo block from csv formatted EDF data record.
	 * 
	 * @param EDFDataRecord		a single row of csv-formatted EDF data from BigQuery
	 * @throws UnsupportedEncodingException 
	 */
	public static Block createBlock(String edfRecord, String data) throws UnsupportedEncodingException {
		String[] values = edfRecord.split(",");
		TemporalProperties temporalProperties = new TemporalProperties(reformatDatetime(values[7]));
		SpatialProperties spatialProperties = new SpatialProperties(parseFloat(values[24]), parseFloat(values[25]));
		FeatureSet features = new FeatureSet();
		features.put(new Feature("platform_id", values[0]));		
		features.put(new Feature("date", values[1]));
		features.put(new Feature("time", values[2]));
		features.put(new Feature("datetime", values[3]));
		features.put(new Feature("frac_days_since_jan1", parseFloat(values[4])));
		features.put(new Feature("frac_hrs_since_jan1", parseFloat(values[5])));
		features.put(new Feature("julian_days", parseFloat(values[6])));
		features.put(new Feature("epoch_time", reformatDatetime(values[7])));
		features.put(new Feature("alarm_status", parseFloat(values[8])));
		features.put(new Feature("inst_status", parseFloat(values[9])));
		features.put(new Feature("cavity_pressure", parseFloat(values[10])));
		features.put(new Feature("cavity_temp", parseFloat(values[11])));
		features.put(new Feature("das_temp", parseFloat(values[12])));
		features.put(new Feature("etalon_temp", parseFloat(values[13])));
		features.put(new Feature("warm_box_temp", parseFloat(values[14])));
		features.put(new Feature("species", parseFloat(values[15])));
		features.put(new Feature("mvp_position", parseFloat(values[16])));
		features.put(new Feature("outlet_valve", parseFloat(values[17])));
		features.put(new Feature("solenoid_valves", parseFloat(values[18])));
		features.put(new Feature("co2", parseFloat(values[19])));
		features.put(new Feature("co2_dry", parseFloat(values[20])));
		features.put(new Feature("ch4", parseFloat(values[21])));
		features.put(new Feature("ch4_dry", parseFloat(values[22])));
		features.put(new Feature("h2o", parseFloat(values[23])));
		features.put(new Feature("gps_abs_lat", parseFloat(values[24])));
		features.put(new Feature("gps_abs_lon", parseFloat(values[25])));
		features.put(new Feature("gps_fit", parseFloat(values[26])));
		features.put(new Feature("gps_time", parseFloat(values[27])));
		features.put(new Feature("ws_wind_lon", parseFloat(values[28])));
		features.put(new Feature("ws_wind_lat", parseFloat(values[29])));
		features.put(new Feature("ws_cos_heading", parseFloat(values[30])));
		features.put(new Feature("ws_sin_heading", parseFloat(values[31])));
		features.put(new Feature("wind_n", parseFloat(values[32])));
		features.put(new Feature("wind_e", parseFloat(values[33])));
		features.put(new Feature("wind_dir_sdev", parseFloat(values[34])));
		features.put(new Feature("ws_rotation", parseFloat(values[35])));
		features.put(new Feature("car_speed", parseFloat(values[36])));
		features.put(new Feature("postal_code", values[37]));
		features.put(new Feature("locality", values[38]));
		features.put(new Feature("s2_30_int", parseLong(values[39])));
			
		Metadata metadata = new Metadata();
		metadata.setName(GeoHash.encode(parseFloat(values[24]), parseFloat(values[25]), 7));
		metadata.setTemporalProperties(temporalProperties);
		metadata.setsIndex(new SearchIndex("24","25","7"));
		metadata.setSpatialProperties(spatialProperties);
		metadata.setSpatialHint(new SpatialHint("gps_abs_lat", "gps_abs_lon"));
		metadata.setAttributes(features);
		
		return new Block("airview", metadata, data.getBytes("UTF-8"));
	}
	
	private static float parseFloat(String input){
		try {
			return Float.parseFloat(input);
		} catch(Exception e){
			return 0.0f;
		}
	}
	
	private static Long parseLong(String input){
		try {
			return Long.parseLong(input);
		} catch(Exception e){
			return 0l;
		}
	}
	
	/*private static double parseDouble(String input){
		try {
			return Double.parseDouble(input);
		} catch(Exception e){
			return 0.0;
		}
	}*/
	// [END createBlock]
	
	// [START reformatDatetime]
	/**
	 * reformat epoch_time from BigQuery table into typical UNIX epoch time
	 * FROM: 1.43699552323E9 TO: 143699552323
	 */
	
	public static long reformatDatetime(String data){
		String tmp = data.replace(".", "").replace("E9", "");
		while(tmp.length()<13){
			tmp+="0";
		}
		return Long.parseLong(tmp); 
	}
	// [END reformatDatetime]
}