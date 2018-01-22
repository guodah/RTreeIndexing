package com.geofeedia.sdquerry.rtree.leaf;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.geofeedia.sdquerry.datatypes.Datum;
import com.geofeedia.sdquerry.rtree.LeafType;
import com.geofeedia.sdquerry.rtree.Platform;

public class RMILeafStub extends LeafStub implements Serializable{
	private RMILeafInterface leaf;
	public RMILeafStub(int dimension, int capacity, float minUtil, RMILeafInterface leaf) {
		super(dimension, capacity, minUtil);
		this.leaf = leaf;
	}
	
	@Override
	public void batchInsert(Collection<Datum> data) {
		if(data==null || data.size()==0){
			throw new IllegalStateException("data is either null or empty");
		}
		try{
			if(size+data.size()<=capacity){
				leaf.insert(data);
				size+=data.size();
			}else{
				throw new IllegalStateException("batch insert will overfill the leaf "+capacity);
			}
		}catch(RemoteException e){
			throw new RuntimeException("Failed to batch insert to remote leaf", e);
		}
	}
	@Override
	public List<Datum> loadAll() {
		try {
			return leaf.loadAll();
		} catch (RemoteException e) {
			throw new RuntimeException("Could not load all from rmi", e);
		}
	}
	@Override
	public LeafStub insert(Datum datum) {
		if(datum==null){
			throw new IllegalStateException("datum is null");
		}

		if(size==capacity){
			LeafStub newLeaf = Platform.checkOutLeafStub(LeafType.JVM,capacity);
			try {
		//		System.out.println("In leaf stub: "+ minUtil);
		//		System.out.println("In Leaf stub: "+ newLeaf.minUtil);

				Map<Class, Object> result = leaf.split(datum, this, (RMILeafStub) newLeaf);
				newLeaf = (LeafStub) result.get(RMILeafStub.class);
				MBB mbb = (MBB) result.get(MBB.class);
				setMBB(mbb);
			} catch (RemoteException e) {
				throw new RuntimeException("Could not insert the datum", e);
			}
			size = (capacity+1-newLeaf.getSize());
			return newLeaf;
		}else{
			try {
				leaf.insert(datum);
			} catch (RemoteException e) {
				throw new RuntimeException("Could not insert the datum", e);
			}
			this.getMbb().grow(datum);
			size++;
			return null;
		}
	}
	
	@Override
	public Collection<Datum> search(MBB range) {
		
		try {
			return leaf.search(new GeoMBB(range));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

}
