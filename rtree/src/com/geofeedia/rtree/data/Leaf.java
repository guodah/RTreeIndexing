package com.geofeedia.rtree.data;

import com.geofeedia.rtree.RtreeDirectory;

public abstract class Leaf {
	protected DataConnection dataConn;
	protected LeafUpdater updater;
	private MBB mbb;
	public MBB getMBB(){
		return mbb;
	}
}
