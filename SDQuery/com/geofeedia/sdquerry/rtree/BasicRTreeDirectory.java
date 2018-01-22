package com.geofeedia.sdquerry.rtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.geofeedia.rtree.util.CollectionUtils;
import com.geofeedia.sdquerry.datatypes.Datum;
import com.geofeedia.sdquerry.rtree.leaf.GeoMBB;
import com.geofeedia.sdquerry.rtree.leaf.LeafStub;
import com.geofeedia.sdquerry.rtree.leaf.MBB;
import com.geofeedia.sdquerry.rtree.leaf.factory.LeafStubWatcher;

public class BasicRTreeDirectory extends RTreeDirectory {
	private BinaryRTree rtree;
	
	public BasicRTreeDirectory(LeafType type){
		super(type);
		rtree=null;
	}
	
	@Override
	public void insert(Collection<Datum> data) {
		if(data==null){
			throw new RuntimeException("null data inserted");
		}if(data.size()==0){
			return;
		}
		
		
		/****** approach one *****/
		// 1. build a rtree with smaller capacity
		// 2. for each leaf, find an enclosing leaf in the distributed rtree without overflow
		//    a) if successful, call batch insert
		//    b) otherwise, insert each datum to the rtree
//		packAndSend(data);
		
		/****** approach two *****/
		// 1. for each leaf in the distributed rtree, find the data to fill it
		// 2. sequential insert the rest of the data.
		selectAndSend(data);
				
		/****** Question ******/
		// Upon split, how to re-adjust the insert
		
	}

	private void selectAndSend(Collection<Datum> data) {
		Collection<LeafStub> leaves = getLeafStubs();
		Map<LeafStub, List<Datum>> categories = new HashMap<LeafStub, List<Datum>>();
		List<Datum> residue = new ArrayList<Datum>();
		for(Datum datum:data){
			boolean found = false;
			for(LeafStub leaf:leaves){
				if(leaf.getMbb().include(datum) && 
						(leaf.getSize()+
								(categories.get(leaf)==null?0:categories.get(leaf).size()))+1
									<=Platform.getLeafCapacity()){
					if(!categories.containsKey(leaf)){
						categories.put(leaf, new ArrayList<Datum>());
					}
					categories.get(leaf).add(datum);
					found = true;
				}
			}
			if(!found){
				residue.add(datum);
			}
		}
		
		for(LeafStub leaf:categories.keySet()){
			List<Datum> leafData = categories.get(leaf);
			leaf.batchInsert(leafData);
		}
		System.out.println(residue.size()*1.0/data.size());
		storeResidue(residue);
	}

	private void storeResidue(List<Datum> residue) {
		for(int i=0;i<residue.size();i++){
			Datum datum = residue.get(i);
			int before = getLeafStubs().size();
			rtree.insert(datum);
/*
			if(getLeafStubs().size()>before &&
					(i+1)<residue.size()){
				packAndSend(CollectionUtils.buildList(
						residue, i+1, residue.size()-1));
			}
*/			
		}
	}

	private void packAndSend(Collection<Datum> data) {
		
		BinaryRTree tempRtree = new BinaryRTree(LeafType.LOCAL, Platform.getTempLeafCapacity());
		for(Datum datum:data){
			tempRtree.insert(datum);
		}
		
		MBB bigBox = MBB.bigBox();
		Collection<LeafStub> leaves = getLeafStubs();
		Collection<LeafStub> tempLeaves = tempRtree.searchForLeaves(bigBox);
		Collection<LeafStub> residue = new ArrayList<LeafStub>();
		for(LeafStub tempLeaf:tempLeaves){
			boolean saved = false;
			for(LeafStub leaf:leaves){
				if(leaf.getMbb().include(tempLeaf.getMbb())
						&& (leaf.getSize()+tempLeaf.getSize())<=Platform.getLeafCapacity()){
					leaf.batchInsert(tempLeaf.loadAll());
					saved = true;
					System.out.println("Found enclosing box: "+tempLeaf.getSize());
				}
				
				break;
			}
			if(!saved){
				residue.add(tempLeaf);
			}
		}		
		
		List<Datum> residueData = new ArrayList<Datum>();
		for(LeafStub leaf:residue){
			residueData.addAll(leaf.loadAll());
		}
		System.out.println(residueData.size()*1.0/data.size());
		storeResidue(residueData);
	}

	@Override
	public void search(GeoMBB range) {
		throw new RuntimeException("unimplemented");		
	}

	@Override
	public void receive(LeafStub stub) {
		if(stub.getCapacity()==Platform.getLeafCapacity()){
			addLeafStub(stub);
		}
	}

	@Override
	public void setRTree(BinaryRTree rtree) {
		this.rtree = rtree;
	}

	
}
