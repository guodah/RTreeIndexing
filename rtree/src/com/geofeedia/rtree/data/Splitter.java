package com.geofeedia.rtree.data;

import java.util.Collection;

public interface Splitter {
	Leaf split(Leaf source, Collection<Datum> items);
}
