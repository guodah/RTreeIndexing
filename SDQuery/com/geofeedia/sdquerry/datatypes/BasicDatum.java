package com.geofeedia.sdquerry.datatypes;

import java.io.Serializable;

public class BasicDatum extends Datum implements Serializable{

	private long longitude;
	private long latitude;
	private long time;
	private long id;

	protected BasicDatum(long longitude, long latitude, long time, long id) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.time = time;
		this.id = id;
	}

	public long getLongitude() {
		return longitude;
	}

	public long getLatitude() {
		return latitude;
	}

	public long getTime() {
		return time;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public long[]  getCoordinates() {
		return new long[]{getTime(), getLongitude(), getLatitude()};
	}

	@Override
	public int getDimensions() {
		return 3;
	}
	
	public String toString(){
		return String.format("id=%d, time=%d, latitude=%d, longitude=%d",
				id, time, getLatitude(), getLongitude());
	}
	
	@Override
	public boolean equals(Object obj){
	//	System.out.println("In BasicDatum.equals()");
		if(obj instanceof BasicDatum){
			BasicDatum d = (BasicDatum)obj;
			return d.id==this.id && d.time==this.time &&
					d.latitude==this.latitude && d.longitude==this.longitude;
		}
	
		return false;
	}
	
	@Override
	public int hashCode(){
	//	System.out.println("In BasicDatum.hashCode");
		return (int)(id/Integer.MAX_VALUE);
	}
}
