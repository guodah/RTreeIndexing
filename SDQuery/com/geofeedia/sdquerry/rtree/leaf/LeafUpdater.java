package com.geofeedia.sdquerry.rtree.leaf;

import java.util.Collection;

import com.geofeedia.sdquerry.datatypes.Datum;

public interface LeafUpdater {
	Splitter getSplitter();
	LeafStub insert(Collection<Datum> data);
}
