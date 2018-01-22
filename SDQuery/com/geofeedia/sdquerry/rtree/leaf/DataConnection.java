package com.geofeedia.sdquerry.rtree.leaf;

import java.util.Collection;
import java.util.List;

import com.geofeedia.sdquerry.datatypes.Datum;

public interface DataConnection {
	void save(Collection<Datum> data);
	void save(Datum datum);
	void close();
	List<Datum> loadAll();
	void clear();
	Collection<Datum> load(GeoMBB range);
}
