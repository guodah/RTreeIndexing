package com.geofeedia.sdquerry.rtree.leaf.factory;

import java.util.List;
import java.util.Random;

import com.geofeedia.rtree.util.IlegalArgumentsException;
import com.geofeedia.rtree.util.LocalFileSystemUtil;
import com.geofeedia.sdquerry.rtree.LeafType;
import com.geofeedia.sdquerry.rtree.leaf.LeafStub;
import com.geofeedia.sdquerry.rtree.leaf.LocalLeaf;
import com.geofeedia.sdquerry.rtree.leaf.LocalLeafStub;
import com.geofeedia.sdquerry.rtree.leaf.RStarSplitter;

public class LocalDiskLeafStubFactory extends LocalLeafStubFactory{
	private String LOCAL_LEAF_PATH;
	public LocalDiskLeafStubFactory(List<String> args){
		if(args==null || args.size()!=1){
			throw new RuntimeException("Incorrect arguments for LocalDiskLeafStubFactory");
		}
		this.LOCAL_LEAF_PATH = args.get(0);
	}
	
	@Override
	public LeafStub checkOutLeafStub(LeafType type, int dimension,
			int capacity, float minUtil) {
		if(!type.equals(LeafType.LOCAL)){
			return null;
		}
		LeafStub stub = new LocalLeafStub(dimension, capacity, minUtil, checkOutLocalLeaf());
		notifyWatchers(stub);
		return stub;
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


}
