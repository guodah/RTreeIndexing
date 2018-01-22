package com.geofeedia.rtree.util;

import java.util.ArrayList;
import java.util.List;

import com.geofeedia.sdquerry.datatypes.Datum;

public class CollectionUtils {
	
	public static <T> List<List<T>> intervalSample(List<T> source, int interval){
		if(source==null || interval<1){
			throw new IllegalStateException("unable to sample the list");
		}
		
		List<List<T>> result = new ArrayList<List<T>>();
		int numOfRows = (interval<source.size())?interval:source.size();
		for(int i=0;i<numOfRows;i++){
			int index = i;
			List<T> row = new ArrayList<T>();
			while(index<source.size()){
				row.add(source.get(index));
				index+=interval;
			}
			result.add(row);
		}
		return result;
	}
	
	public static <T> List<T> buildList(List<T> source, int start, int end){
		if(source==null || source.size()<(end-start+1) || 
				start>end || start<0 || end>=source.size()){
			throw new IllegalStateException("Unable to build a list");
		}
		
		List<T> result = new ArrayList<T>();
		for(int i=start;i<=end;i++){
			result.add(source.get(i));
		}
		return result;
	}
	
	public static long findMin(List<Datum> list, int dimension){
		long min = Long.MAX_VALUE;
		
		for(Datum datum:list){
			min = (datum.getCoordinates()[dimension]<min)
					?datum.getCoordinates()[dimension]:min;
		}
		return min;
	}
	
	public static long findMax(List<Datum> list, int dimension){
		long max = Long.MIN_VALUE;
		
		for(Datum datum:list){
			max = (datum.getCoordinates()[dimension]>max)
					?datum.getCoordinates()[dimension]:max;
		}
		return max;		
	}

}
