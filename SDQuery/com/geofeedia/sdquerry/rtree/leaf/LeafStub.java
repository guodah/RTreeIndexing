package com.geofeedia.sdquerry.rtree.leaf;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.geofeedia.sdquerry.datatypes.Datum;
import com.geofeedia.sdquerry.rtree.BasicRTreeDirectory;
import com.geofeedia.sdquerry.rtree.BinaryRTreeNode;

public abstract class LeafStub extends BinaryRTreeNode implements Serializable{
	protected int capacity;
	protected int size;
	protected float minUtil;
	
	/**
	 * Inserts to the leaf. all data must be in the mbb
	 * 
	 * @param data
	 * @return
	 */
	public abstract void batchInsert(Collection<Datum> data);
	
	public abstract List<Datum> loadAll();
	
	public abstract LeafStub insert(Datum datum);
	
	/**
	 * Searches for the data in the range
	 * 
	 * @param range
	 * @return
	 */
	public abstract Collection<Datum> search(MBB range);
	
	public LeafStub(){
	}
	
	public LeafStub(int dimension, int capacity, float minUtil){
		super(dimension);
		this.capacity = capacity;
		this.minUtil = minUtil;
	}

	public int getSize() {
		return size;
	}

	public long getCapacity() {
		// TODO Auto-generated method stub
		return capacity;
	}
}
