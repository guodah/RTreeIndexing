package com.geofeedia.sdquerry.rtree.leaf;

import java.util.Collection;
import java.util.List;

import com.geofeedia.sdquerry.datatypes.Datum;

public interface Splitter {

	List<List<Datum>> split(List<Datum> items, float minUtil);
}
