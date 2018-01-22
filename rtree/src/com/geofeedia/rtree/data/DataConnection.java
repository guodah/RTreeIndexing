package com.geofeedia.rtree.data;

import java.util.Collection;

public interface DataConnection {
	Collection<Datum> load(Range range);
}
