package com.geofeedia.sdquerry.rtree.leaf;

import java.io.Serializable;

public class GeoMBB extends MBB implements Cloneable, Serializable{
	
	private static int TIME_INDEX = 0;
	private static int LONGITUDE_INDEX = 1;
	private static int LATITUDE_INDEX = 2;
	
	public GeoMBB(MBB mbb){
		super(mbb.highs,mbb.lows);	
	}
	
	public long getTimeHigh(){
		return (long)this.highs[TIME_INDEX];
	}
	public long getTimeLow(){
		return (long)this.lows[TIME_INDEX];
	}
	public long getLongitudeHigh(){
		return (long)this.highs[LONGITUDE_INDEX];
	}
	public long getLongitudeLow(){
		return (long)this.lows[LONGITUDE_INDEX];
	}
	public long getLatitudeHigh(){
		return (long)this.highs[LATITUDE_INDEX];
	}
	public long getLatitudeLow(){
		return (long)this.lows[LATITUDE_INDEX];
	}
}
