package com.geofeedia.rtree.data;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.geofeedia.rtree.util.TimeUtil;

public class BasicDatumBuilder {
	private float longitude;
	private float latitude;
	private float time;
	private long id;
	private Map<String, Object> properties = new HashMap<String, Object>();
	private BasicDatum datum;
	
	private BasicDatumBuilder(float [] coordinates, String time, long id) throws ParseException{
		this();
		longitude = coordinates[0];
		latitude = coordinates[1];
		this.time = (float) TimeUtil.parse(time).getTime();
		this.id = id;
	}
	
	private BasicDatumBuilder(){
		datum = new BasicDatum();
	}
	
	public static BasicDatumBuilder newInstance(){
		return new BasicDatumBuilder();
	}
	
	public static BasicDatumBuilder newInstance(float [] coordinates, String time, long id) throws ParseException{
		check(coordinates, time, id);
		return new BasicDatumBuilder(coordinates, time, id);
	}
	
	private static void check(float[] coordinates, String time, long id) throws ParseException {
		if(coordinates==null || coordinates.length!=2){
			throw new RuntimeException("Invalid coordinates provided in BasicDatumBuilder");
		}else if(coordinates[0]>180 || coordinates[0]<-180){
			throw new RuntimeException("Invalid longitude provided in BasicDatumBuilder");
		}else if(coordinates[1]>90 || coordinates[1]<90){
			throw new RuntimeException("Invalid latitude provided in BasicDatumBuilder");
		}else if(TimeUtil.parse(time)==null){
			throw new RuntimeException("Invalid time string provided in BasicDatumBuilder");
		}
	}

	public BasicDatumBuilder setLongitude(float longitude){
		this.longitude = longitude;
		return this;
	}
	
	public BasicDatumBuilder setLatitude(float latitude){
		this.latitude = latitude;
		return this;
	}
	
	public BasicDatumBuilder setTime(float time){
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
	
}
