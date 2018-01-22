package com.geofeedia.sdquerry.rtree;

import java.util.Map;

import com.geofeedia.sdquerry.rtree.leaf.LeafStub;
import com.geofeedia.sdquerry.rtree.leaf.factory.LeafStubFactory;

public abstract class SystemUtil {
	public abstract void addLeafStubFactory(LeafType type, LeafStubFactory factory);
	public abstract LeafStub checkOutLeafStub(LeafType type, 
			int dimension, int capacity, float minUtil);
	public abstract void setDirectory(RTreeDirectory directory);
}
