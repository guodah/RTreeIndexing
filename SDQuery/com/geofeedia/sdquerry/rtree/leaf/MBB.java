package com.geofeedia.sdquerry.rtree.leaf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.geofeedia.rtree.util.CollectionUtils;
import com.geofeedia.rtree.util.TimeUtil;
import com.geofeedia.sdquerry.datatypes.Datum;

public class MBB implements Cloneable, Serializable{
	private static final double DELTA = 0.5;
	private int dimension;
	protected long [] lows;
	protected long [] highs;
	public MBB(int dimension){
		if(dimension<1){
			throw new IllegalStateException("invalid arguments");
		}
		this.dimension = dimension;
		lows = new long[dimension];
		highs = new long[dimension];
		
		for(int i=0;i<dimension;i++){
			lows[i] = Long.MAX_VALUE;
			highs[i] = Long.MIN_VALUE;
		}
	}
	
	public MBB(long highs[], long lows[]){
		if(highs==null || lows==null ||
				highs.length!=lows.length){
			throw new IllegalStateException("invalid arguments");
		}
		
		this.dimension = highs.length;
		this.lows = new long[dimension];
		this.highs = new long[dimension];

		for(int i=0;i<dimension;i++){
			this.lows[i] = lows[i];
			this.highs[i] = highs[i];
		}
	}
	
	public MBB(){
		
	}
	
	public MBB(List<Datum> g1) {
		if(g1==null || g1.size()==0){
			throw new IllegalStateException("invalid arguments");
		}
		this.dimension = g1.get(0).getCoordinates().length;
		this.highs = new long[dimension];
		this.lows = new long[dimension];
		
		for(int i=0;i<dimension;i++){
			highs[i] = CollectionUtils.findMax(g1, i);
			lows[i] = CollectionUtils.findMin(g1, i);
		}
	}

	public void setHigh(int index, long value){
		if(index<0 || index>=dimension){
			return;
		}
		highs[index] = value;
	}

	public void setLow(int index, long value){
		if(index<0 || index>=dimension){
			return;
		}
		lows[index] = value;
	}
	
	public long [] getLowCorner(){
		return Arrays.copyOf(lows, lows.length);
	}

	public long [] getHighCorner(){
		return Arrays.copyOf(highs, highs.length);
	}
	
	private boolean include(long[] coordinates){
		if(coordinates==null || coordinates.length!=dimension){
			return false;
		}
		
		for(int i=0;i<dimension;i++){
			
			if(coordinates[i]>highs[i] || coordinates[i]<lows[i]){
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
	
	public String toString(){
		return String.format("[%s\t%s]\n[%d\t%d]\n[%d\t%d]",
				lows[0],highs[0],lows[1],highs[1],lows[2],highs[2]);
	}
	
	
	private void grow(long [] coordinates){		
		for(int i=0;i<dimension;i++){
			lows[i] = (lows[i]>coordinates[i])?coordinates[i]:lows[i];
			highs[i] = (highs[i]<coordinates[i])?coordinates[i]:highs[i];
		}		
	}
	
	public void grow(Datum point){
		grow(point.getCoordinates());
	}

	public void grow(MBB mbb) {
		grow(mbb.getHighCorner());
		grow(mbb.getLowCorner());
	}
	
	public boolean overlap(MBB mbb){
		double overlap = overlap(this, mbb);
		return overlap>DELTA;
	}

	public long enlargement(Datum datum) {
		if(datum==null){
			throw new NullPointerException();
		}
		if(include(datum)){
			return 0;
		}else{
			long delta[] = new long[dimension];
			long newLens[] = new long[dimension];
			long newVolume = 1, oldVolume = 1;
			for(int i=0;i<dimension;i++){
				delta[i] = enlargement(lows[i],highs[i],datum.getCoordinates()[i]);
				newLens[i] = highs[i]-lows[i]+delta[i];
				newVolume *= newLens[i];
				oldVolume *= (highs[i]-lows[i]);
			}
			return newVolume - oldVolume;
		}
	}

	private long enlargement(long low, long high, long value) {
		if(value>high){
			return value-high;
		}else if(value<low){
			return low-value;
		}else{
			return 0;
		}
	}

	public int getDimension() {
		return dimension;
	}
	
	@Override
	public MBB clone(){
		MBB mbb = new MBB(dimension);
		for(int i=0;i<dimension;i++){
			mbb.highs[i] = highs[i];
			mbb.lows[i] = lows[i];
		}
		return mbb;
	}
	
	public static MBB union(MBB m1, MBB m2){
		if(m1.getDimension()!=m2.getDimension()){
			throw new IllegalStateException("MBBs with different dimensions");
		}
		MBB result = m1.clone();
		result.grow(m2);
		return result;
	}
	
	public static double overlap(MBB m1, MBB m2){
		if(m1.getDimension()!=m2.getDimension()){
			throw new IllegalStateException("MBBs with different dimensions");
		}

		double overlap = 1;
		for(int i=0;i<m1.getDimension();i++){
			long o = overlap(m1,m2,i);
			if(o==0){
				return 0;
			}else{
				overlap *= (double) o;
			}
		}
		
		
		return overlap;
	}

	private static long overlap(MBB m1, MBB m2, int i) {
		long min1 = m1.lows[i], min2 = m2.lows[i];
		long max1 = m1.highs[i], max2 = m2.highs[i];
		
		if(min1>=min2 && min1<=max2){
			return Math.min(max1, max2)-min1+1;
		}else if(min2>=min1 && min2<=max1){
			return Math.min(max1, max2)-min2+1;
		}else{		
			return 0;
		}
	}

	private static double volume(long[] p1, long[] p2) {
		double product = 1.0;
		for(int i=0;i<p1.length;i++){
			product *= (double) Math.abs(p1[i]-p2[i]);
		}
		
		return product;
	}

	public double volume() {
		return volume(highs, lows);
	}
	
	public long margin(){
		long margin = 0;
		for(int i=0;i<highs.length;i++){
			margin += (highs[i]-lows[i]);
		}
		return margin;
	}
	
	public boolean equals(Object obj){
		if(!(obj instanceof MBB)){
			return false;
		}
		
		MBB mbb = (MBB) obj;
		
		if(mbb.getDimension()!=this.getDimension()){
			return false;
		}
		
		for(int i=0;i<mbb.getDimension();i++){
			if(mbb.highs[i]!=this.highs[i] ||
					mbb.lows[i]!=this.lows[i]){
				return false;
			}
		}
		return true;
	}

	public static MBB bigBox() {
		return new MBB(new long[]{Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE},
				new long[]{Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE});
	}

}
