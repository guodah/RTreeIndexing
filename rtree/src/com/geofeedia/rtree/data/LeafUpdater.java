package com.geofeedia.rtree.data;

import java.util.Collection;

public abstract class LeafUpdater {
	protected Splitter splitter;
	abstract public LeafStub insert(Collection<Datum> data);
}
