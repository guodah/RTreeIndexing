package com.geofeedia.rtree.data;

import java.util.Arrays;
import java.util.Collection;

public class MBB {
	private int dimension;
	private float [] lows;
	private float [] highs;
	public MBB(int dimension){
		this.dimension = dimension;
		lows = new float[dimension];
		highs = new float[dimension];
	}
	
	public float [] getLowCorner(){
		return Arrays.copyOf(lows, lows.length);
	}

	public float [] getHighCorner(){
		return Arrays.copyOf(highs, highs.length);
	}
	
	private boolean include(float[] coordinates){
		if(coordinates==null || coordinates.length!=dimension){
			return false;
		}
		
		for(int i=0;i<dimension;i++){
			if(coordinates[i]>highs[i] && coordinates[i]<lows[i]){
				return false;
			}
		}
		return true;
	}
	
	public boolean include(Datum point){
		return include(point.getCoordinates());
	}
	
	public boolean include (MBB mbb){
		return include(mbb.highs) && include(mbb.lows);
	}
	
	public boolean include (Collection<Datum> points){
		if(points==null){
			return false;
		}
		
		for(Datum datum: points){
			if(!include(datum)){
				return false;
			}
		}
		
		return true;
	}
}
