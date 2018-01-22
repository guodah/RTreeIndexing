package com.geofeedia.rtree.util;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.geofeedia.sdquerry.rtree.LeafType;
import com.geofeedia.sdquerry.rtree.RTreeDirectory;
import com.geofeedia.sdquerry.rtree.SystemUtil;
import com.geofeedia.sdquerry.rtree.leaf.LeafStub;
import com.geofeedia.sdquerry.rtree.leaf.LocalLeaf;
import com.geofeedia.sdquerry.rtree.leaf.LocalLeafStub;
import com.geofeedia.sdquerry.rtree.leaf.RStarSplitter;
import com.geofeedia.sdquerry.rtree.leaf.factory.LeafStubFactory;

public class BasicLocalSystemUtil extends SystemUtil{

	private Map<LeafType, LeafStubFactory> LeafFactories;
	
	public BasicLocalSystemUtil(){
		LeafFactories = new HashMap<LeafType, LeafStubFactory>();
	}
	
	@Override
	public void addLeafStubFactory(LeafType type, LeafStubFactory factory) {
		this.LeafFactories.put(type, factory);
	}

	@Override
	public LeafStub checkOutLeafStub(LeafType type, int dimension,
			int capacity, float minUtil) {
		
		if(!this.LeafFactories.containsKey(type)){
			throw new RuntimeException("The factory for "+type+" does not exist.");
		}
		return LeafFactories.get(type).checkOutLeafStub(type, dimension, capacity, minUtil);
	}
/*
	private static String LOCAL_LEAF_PATH;
	
	public BasicLocalSystemUtil(String path){
		LOCAL_LEAF_PATH = path;

		if(LocalFileSystemUtil.exist(LOCAL_LEAF_PATH)){
			LocalFileSystemUtil.clearDirectory(LOCAL_LEAF_PATH);
		}
		LocalFileSystemUtil.createPath(LOCAL_LEAF_PATH);
	}
	
	@Override
	public LeafStub checkOutLeafStub(LeafType type, int dimension, int capacity, float minUtil) {
		return new LocalLeafStub(dimension, capacity, minUtil, checkOutLocalLeaf()); 
	}

	private LocalLeaf checkOutLocalLeaf() {
		
		// randomize a name
		Random random = new Random();
		String name;
		do{
			name = "L"+random.nextInt(100000000);
		}while(LocalFileSystemUtil.find(LOCAL_LEAF_PATH, name));
		
		// create a leaf with a random folder name
		String leafPath = String.format("%s//%s", LOCAL_LEAF_PATH, name);
	
		try {
			LocalLeaf leaf = new LocalLeaf(leafPath, new RStarSplitter());
			return leaf;
		} catch (IlegalArgumentsException e) {
			throw new RuntimeException("Unable to create a local leaf");
		}
	}
*/

	@Override
	public void setDirectory(RTreeDirectory directory) {
		if(directory==null){
			throw new RuntimeException("directory is null");
		}
		Collection<LeafStubFactory> factories = LeafFactories.values();
		for(LeafStubFactory factory:factories){
			factory.addObserver(directory);
		}
	}
	
}
