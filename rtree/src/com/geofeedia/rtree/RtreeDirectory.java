package com.geofeedia.rtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.geofeedia.rtree.data.Datum;
import com.geofeedia.rtree.data.Leaf;
import com.geofeedia.rtree.data.LeafStub;
import com.geofeedia.rtree.data.Range;

public abstract class RtreeDirectory {
	private int leafCapacity;
	private LeafType leafType;
	private List<LeafStub> leafSubs;
	
	private RtreeDirectory(int leafCacpacity, LeafType type){
		this.leafCapacity = leafCacpacity;
		this.leafType = type;
		leafSubs = new ArrayList<LeafStub>();
	}
	
	abstract public void insert(Collection<Datum> data);
	abstract public void search(Range range);	
}
