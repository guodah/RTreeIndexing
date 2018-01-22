package com.geofeedia.sdquerry.rtree.leaf.factory;

import java.util.ArrayList;
import java.util.List;

import com.geofeedia.sdquerry.rtree.LeafType;
import com.geofeedia.sdquerry.rtree.leaf.LeafStub;

public abstract class LeafStubFactory {
	
	private List<LeafStubWatcher> watchers;

	public abstract LeafStub checkOutLeafStub(LeafType type, int dimension, int capacity, float minUtil);
	
	public void addObserver(LeafStubWatcher watcher){
		if(watchers==null){
			watchers = new ArrayList<LeafStubWatcher>();
		}
		watchers.add(watcher);
	}
	
	public void notifyWatchers(LeafStub stub){
		if(watchers==null){
			return;
		}
		for(LeafStubWatcher watcher:watchers){
			watcher.receive(stub);
		}
	}
}
