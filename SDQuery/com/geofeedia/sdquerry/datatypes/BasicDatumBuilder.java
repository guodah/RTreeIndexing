package com.geofeedia.sdquerry.datatypes;

import java.util.HashMap;
import java.util.Map;

import com.geofeedia.rtree.util.TimeUtil;
import com.geofeedia.sdquerry.rtree.Platform;

public class BasicDatumBuilder {
	private long longitude;
	private long latitude;
	private long time;
	private long id;
	private Map<String, Object> properties = new HashMap<String, Object>();
	

	
	public BasicDatumBuilder createNewDatum(long [] coordinates, String time, long id){
		check(coordinates, time, id);
		longitude = coordinates[0];
		latitude = coordinates[1];
		this.time = TimeUtil.parse(time).getTime();
		this.id = id;
		return this;
	}
	
	public BasicDatumBuilder createNewDatum(){
		longitude = Long.MIN_VALUE;
		latitude = Long.MIN_VALUE;
		time = Long.MIN_VALUE;
		id = Long.MIN_VALUE;
		return this;
	}		
	
	private static void check(long[] coordinates, String time, long id) {
		if(coordinates==null || coordinates.length!=2){
			throw new RuntimeException("Invalid coordinates provided in BasicDatumBuilder");
		}else{
			check(coordinates[0], coordinates[1]);
		}
		if(TimeUtil.parse(time)==null){
			throw new RuntimeException("Invalid time string provided in BasicDatumBuilder");
		}
	}

	public BasicDatumBuilder setLongitude(long longitude){
		this.longitude = longitude;
		return this;
	}
	
	public BasicDatumBuilder setLatitude(long latitude){
		this.latitude = latitude;
		return this;
	}
	
	public BasicDatumBuilder setTime(long time){
		this.time = time;
		return this;
	}
	
	public BasicDatumBuilder setId(long id){
		this.id = id;
		return this;
	}
	
	public BasicDatumBuilder setProperty(String name, Object value){
		properties.put(name, value);
		return this;
	}
	
	public BasicDatum build(){
		check(longitude, latitude);
		return new BasicDatum(longitude, latitude, time, id);
	}

	private static void check(long longitude,long latitude) {
		if(longitude/Platform.LONGITUDE_SCALE_FACTOR>180 || longitude/Platform.LONGITUDE_SCALE_FACTOR<-180){
			throw new RuntimeException("Invalid longitude provided in BasicDatumBuilder");
		}else if(latitude/Platform.LATITUDE_SCALE_FACTOR>90 || latitude/Platform.LATITUDE_SCALE_FACTOR<-90){
			throw new RuntimeException("Invalid latitude provided in BasicDatumBuilder");
		}
	}
}
