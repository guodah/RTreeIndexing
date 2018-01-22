package com.geofeedia.sdquerry.rtree;

import com.geofeedia.sdquerry.rtree.leaf.LeafStub;

public interface Balancer {
	void balance(LeafStub leaf);
}
