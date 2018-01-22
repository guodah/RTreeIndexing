package com.geofeedia.sdquerry.rtree;

import java.util.ArrayList;


import java.util.Collection;
import java.util.List;

import com.geofeedia.sdquerry.datatypes.Datum;
import com.geofeedia.sdquerry.rtree.leaf.GeoMBB;
import com.geofeedia.sdquerry.rtree.leaf.LeafStub;
import com.geofeedia.sdquerry.rtree.leaf.factory.LeafStubWatcher;

public abstract class RTreeDirectory implements LeafStubWatcher{
	private LeafType leafType;
	private List<LeafStub> leafStubs;
	
	public RTreeDirectory(LeafType type){
		this.leafType = type;
		leafStubs = new ArrayList<LeafStub>();
	}

	public abstract void setRTree(BinaryRTree rtree);

	abstract public void insert(Collection<Datum> data);
	abstract public void search(GeoMBB range);
	public List<LeafStub> getLeafStubs(){
		return leafStubs;
	}
	
	protected void addLeafStub(LeafStub leaf){
		leafStubs.add(leaf);
	}
}
