package com.geofeedia.rtree.data;

import java.util.Collection;

public interface LeafStub {
	LeafStub insert(Collection<Datum> data);
	Collection<Datum> search(Range range);
}
